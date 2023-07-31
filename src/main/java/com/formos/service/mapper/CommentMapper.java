package com.formos.service.mapper;

import static com.formos.config.Constants.MMM_DD_AT_H_MM_A;

import com.formos.config.Constants;
import com.formos.domain.Profile;
import com.formos.service.ClickUpClientService;
import com.formos.service.dto.clickup.*;
import com.formos.service.utils.CommonUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommentMapper {

    private final ClickUpClientService clickUpClientService;
    private final ContentItemMapper contentItemMapper;
    private final AttachmentMapper attachmentMapper;

    @Value("${clickup.base-folder}")
    private String baseFolder;

    public CommentMapper(
        ClickUpClientService clickUpClientService,
        ContentItemMapper contentItemMapper,
        AttachmentMapper attachmentMapper
    ) {
        this.clickUpClientService = clickUpClientService;
        this.contentItemMapper = contentItemMapper;
        this.attachmentMapper = attachmentMapper;
    }

    public CommentDTO toCommentDTO(TaskHistory taskHistory, TaskComments.Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.id);
        commentDTO.setType(comment.type);
        commentDTO.setCommentText(comment.comment_text);
        commentDTO.setTextComment(comment.text_content);
        commentDTO.setTimeStamp(Long.parseLong(comment.date));
        commentDTO.setDateTime(CommonUtils.formatToDateTimeFromTimestamp(comment.date, MMM_DD_AT_H_MM_A));
        commentDTO.setUsername(Objects.nonNull(comment.user) ? comment.user.username : null);
        commentDTO.setCommentItems(comment.commentDetails.stream().map(contentItemMapper::toContentItemDTO).collect(Collectors.toList()));
        commentDTO.setUser(Objects.nonNull(comment.user) ? new UserDTO(comment.user) : null);
        commentDTO.setAssignedBy(Objects.nonNull(comment.assignedBy) ? new UserDTO(comment.assignedBy) : null);
        commentDTO.setAssignee(Objects.nonNull(comment.assignee) ? new UserDTO(comment.assignee) : null);
        commentDTO.setResolvedBy(Objects.nonNull(comment.resolvedBy) ? new UserDTO(comment.resolvedBy) : null);
        commentDTO.setResolved(comment.resolved);
        commentDTO.setAttachments(
            comment.commentDetails
                .stream()
                .filter(commentItem -> Objects.nonNull(commentItem) && Objects.nonNull(commentItem.attachment))
                .map(commentItem -> attachmentMapper.toAttachmentDTO(commentItem.attachment))
                .collect(Collectors.toSet())
        );
        commentDTO.setHtmlText(buildContentHtml(taskHistory, commentDTO.getCommentItems()));
        return commentDTO;
    }

    public CommentDTO toCommentDTO(TaskHistory taskHistory, History history) {
        CommentDTO commentDTO = toCommentDTO(taskHistory, history.comment);
        if (Objects.nonNull(history.data) && Objects.nonNull(history.data.attachmentId)) {
            commentDTO.setCommentAttachmentId(history.data.attachmentId);
        }
        return commentDTO;
    }

    public CommentDTO processAddHistory(TaskHistory taskHistory, Map<String, CommentDTO> map, TaskComments.Comment comment) {
        CommentDTO commentDTO = toCommentDTO(taskHistory, comment);
        CommentDTO commentHistory = map.get(commentDTO.getId());
        if (Objects.nonNull(commentHistory)) {
            commentDTO.addHistory(commentHistory);
        }
        return commentDTO;
    }

    public String buildContentHtml(Profile profile, TaskHistory taskHistory, TaskContentData taskContentData) {
        return buildContentHtml(
            taskHistory,
            taskContentData.contentItemData
                .stream()
                .map(taskContentItemData -> contentItemMapper.toContentItemDTO(profile, taskContentItemData))
                .collect(Collectors.toList())
        );
    }

    public String buildContentHtml(TaskHistory taskHistory, List<ContentItemDTO> commentItems) {
        StringBuilder htmlBuilder = new StringBuilder();
        StringBuilder blockBuilder = new StringBuilder();
        int index = 0;
        while (index < commentItems.size()) {
            ContentItemDTO commentItem = commentItems.get(index);
            String text = commentItem.getText();
            AttributesDTO attributes = commentItem.getAttributes();
            if (Objects.isNull(attributes)) {
                buildSingleBlock(taskHistory, blockBuilder, commentItem, text);
            } else if (Objects.nonNull(attributes.getTableColWidth())) {
                index = buildTableBlock(blockBuilder, commentItems, index);
                htmlBuilder.append(blockBuilder);
                blockBuilder = new StringBuilder();
            } else if (Objects.nonNull(attributes.getList()) && !"none".equals(attributes.getList())) {
                index = buildListBlock(blockBuilder, commentItems, index);
                htmlBuilder.append(blockBuilder);
                blockBuilder = new StringBuilder();
            } else if (Objects.nonNull(attributes.getBlockId())) {
                String block = blockBuilder.toString();
                blockBuilder = new StringBuilder();
                blockBuilder.append("<div>");
                applyAttributes(blockBuilder, block, attributes);
                blockBuilder.append("</div>");
                htmlBuilder.append(blockBuilder);
                blockBuilder = new StringBuilder();
            } else if (isSingleBlock(commentItem.getType())) {
                buildSingleBlock(taskHistory, blockBuilder, commentItem, text);
                htmlBuilder.append(blockBuilder);
                blockBuilder = new StringBuilder();
            } else {
                applyAttributes(blockBuilder, text, attributes);
            }
            index++;
        }
        if (!blockBuilder.toString().isEmpty()) {
            htmlBuilder.append(blockBuilder);
        }
        return htmlBuilder.toString();
    }

    public String buildHtml(Profile profile, TaskHistory taskHistory, TaskContentData taskContentData) {
        return buildHtml(
            taskHistory,
            taskContentData.contentItemData
                .stream()
                .map(taskContentItemData -> contentItemMapper.toContentItemDTO(profile, taskContentItemData))
                .collect(Collectors.toList())
        );
    }

    public String buildHtml(TaskHistory taskHistory, List<ContentItemDTO> commentItems) {
        StringBuilder htmlBuilder = new StringBuilder();
        StringBuilder blockBuilder = new StringBuilder();
        StringBuilder orderedBuilder = new StringBuilder();
        StringBuilder tableBuilder = new StringBuilder();
        StringBuilder rowBuilder = new StringBuilder();
        ContentItemDTO previousBlockItem = null;
        String beforeBlockType = "";
        int size = commentItems.size();
        for (int index = 0; index < size; index++) {
            ContentItemDTO commentItem = commentItems.get(index);
            String text = commentItem.getText();
            AttributesDTO attributes = commentItem.getAttributes();
            if (Objects.isNull(attributes) && Objects.isNull(commentItem.getType())) {
                blockBuilder.append(text);
            } else {
                if (Objects.nonNull(attributes) && Objects.nonNull(attributes.getBlockId())) {
                    if (Objects.nonNull(attributes.getList()) && ("ordered".equals(attributes.getList()))) {
                        blockBuilder = appendOrderedList(blockBuilder, orderedBuilder, beforeBlockType);
                        beforeBlockType = "ordered";
                    }
                    if (Objects.nonNull(attributes.getTableCellLineCell())) {
                        if ("table".equals(beforeBlockType)) {
                            blockBuilder = appendRow(blockBuilder, rowBuilder, previousBlockItem, attributes);
                        } else if (rowBuilder.toString().isEmpty()) {
                            tableBuilder = appendEndOfHeaderTable(htmlBuilder, tableBuilder);
                            blockBuilder = appendFirstRowTable(blockBuilder, rowBuilder, previousBlockItem, attributes);
                        }
                        beforeBlockType = "table";
                        previousBlockItem = commentItem;
                    }
                    if (index == size - 1) {
                        if ((!rowBuilder.toString().isEmpty() && "table".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            rowBuilder = appendEndOfTable(htmlBuilder, blockBuilder, rowBuilder);
                            blockBuilder = new StringBuilder();
                        } else if ((!orderedBuilder.toString().isEmpty() && "ordered".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            orderedBuilder = appendEndOfOrderedList(htmlBuilder, blockBuilder, orderedBuilder);
                            blockBuilder = new StringBuilder();
                        }
                    } else {
                        if (
                            (!rowBuilder.toString().isEmpty() && "table".equals(beforeBlockType)) &&
                            Objects.isNull(attributes.getTableCellLineCell())
                        ) {
                            blockBuilder = new StringBuilder();
                            rowBuilder = appendEndOfTable(htmlBuilder, blockBuilder, rowBuilder);
                            blockBuilder = new StringBuilder();
                        }
                        if ((!orderedBuilder.toString().isEmpty() && !"ordered".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            orderedBuilder = appendEndOfOrderedList(htmlBuilder, blockBuilder, orderedBuilder);
                            blockBuilder = new StringBuilder();
                        }
                    }
                    if (
                        (Objects.nonNull(attributes.getList()) && !"ordered".equals(attributes.getList())) ||
                        (Objects.isNull(attributes.getTableCellLineCell()))
                    ) {
                        String block = blockBuilder.toString();
                        blockBuilder = new StringBuilder();
                        blockBuilder.append("<div>");
                        applyAttributes(blockBuilder, block, attributes);
                        blockBuilder.append("</div>");
                        htmlBuilder.append(blockBuilder);
                        blockBuilder = new StringBuilder();
                    }
                } else if (Objects.nonNull(attributes)) {
                    if (Objects.nonNull(attributes.getTableColWidth())) {
                        blockBuilder = appendTableHeader(blockBuilder, tableBuilder, beforeBlockType, attributes);
                        previousBlockItem = commentItem;
                        beforeBlockType = "tableHeader";
                    } else if (Objects.nonNull(attributes.getDataId())) {
                        appendImage(taskHistory, blockBuilder, commentItem, true);
                    } else {
                        applyAttributes(blockBuilder, text, attributes);
                    }
                } else {
                    if ("attachment".equals(commentItem.getType())) {
                        if (commentItem.isImage()) {
                            appendImage(taskHistory, blockBuilder, commentItem, false);
                        }
                        blockBuilder.append("<a href=\"").append(commentItem.getAttachmentId()).append("\">");
                        blockBuilder.append(text);
                        blockBuilder.append("</a>");
                        blockBuilder.append("<br>");
                    } else if ("emoticon".equals(commentItem.getType())) {
                        blockBuilder.append("<span class=\"emoji\">").append(text).append("</span>");
                    } else if ("userMention".equals(commentItem.getType())) {
                        //                        blockBuilder.append(text);
                    } else if ("divider".equals(commentItem.getType())) {
                        blockBuilder.append("<hr>");
                    } else if ("taskImage".equals(commentItem.getType())) {
                        appendImage(taskHistory, blockBuilder, commentItem, true);
                    } else if ("taskMention".equals(commentItem.getType())) {
                        appendTaskMention(commentItem.getText(), commentItem.getUrl(), blockBuilder);
                    } else if ("tag".equals(commentItem.getType())) {
                        blockBuilder.append(text);
                    }
                }
            }
        }
        if (!blockBuilder.isEmpty()) {
            htmlBuilder.append(blockBuilder);
        }
        return htmlBuilder.toString();
    }

    private void buildSingleBlock(TaskHistory taskHistory, StringBuilder blockBuilder, ContentItemDTO commentItem, String text) {
        if ("attachment".equals(commentItem.getType())) {
            if (commentItem.isImage()) {
                appendImage(taskHistory, blockBuilder, commentItem, false);
            }
            blockBuilder.append("<a href=\"").append(commentItem.getAttachmentId()).append("\">");
            blockBuilder.append(text);
            blockBuilder.append("</a>");
            blockBuilder.append("<br>");
        } else if ("emoticon".equals(commentItem.getType())) {
            blockBuilder.append("<span class=\"emoji\">").append(text).append("</span>");
        } else if ("userMention".equals(commentItem.getType())) {
            //                        blockBuilder.append(text);
        } else if ("divider".equals(commentItem.getType())) {
            blockBuilder.append("<hr>");
        } else if ("taskImage".equals(commentItem.getType())) {
            appendImage(taskHistory, blockBuilder, commentItem, true);
        } else if ("taskMention".equals(commentItem.getType())) {
            appendTaskMention(commentItem.getText(), commentItem.getUrl(), blockBuilder);
        } else if ("tag".equals(commentItem.getType())) {
            blockBuilder.append(text);
        } else {
            blockBuilder.append(text);
        }
    }

    private int buildTableBlock(StringBuilder blockBuilder, List<ContentItemDTO> commentItems, int originIndex) {
        int index = originIndex;
        ContentItemDTO commentItem = commentItems.get(index);
        String text = commentItem.getText();
        AttributesDTO attributes = commentItem.getAttributes();
        blockBuilder.append("<table>");
        blockBuilder.append("<colgroup>");
        while (index < commentItems.size() && Objects.nonNull(attributes) && Objects.nonNull(attributes.getTableColWidth())) {
            appendCol(blockBuilder, attributes.getTableColWidth());
            index++;
            attributes = commentItems.get(index).getAttributes();
            text = commentItems.get(index).getText();
        }
        blockBuilder.append("</colgroup>");
        blockBuilder.append("<tbody>");
        StringBuilder rowBuilder = new StringBuilder();
        StringBuilder cellBuilder = new StringBuilder();
        String currentRow = "1";
        int lastIndex = index;
        while (
            index < commentItems.size() &&
            (Objects.isNull(attributes) || !(Objects.nonNull(attributes.getBlockId()) && Objects.isNull(attributes.getTableCellLineCell())))
        ) {
            if (Objects.isNull(attributes)) {
                cellBuilder.append(text);
            } else if (Objects.nonNull(attributes.getTableCellLineRow())) {
                if (currentRow.equals(attributes.getTableCellLineRow())) {
                    if (rowBuilder.toString().isEmpty()) {
                        rowBuilder.append("<tr>");
                    }
                } else {
                    blockBuilder.append(rowBuilder);
                    blockBuilder.append("</tr>");
                    rowBuilder = new StringBuilder("<tr>");
                    currentRow = attributes.getTableCellLineRow();
                }
                if (Objects.nonNull(attributes.getTableCellLineCell())) {
                    appendCell(rowBuilder, attributes.getTableCellLineRowspan(), attributes.getTableCellLineColspan());

                    rowBuilder.append(cellBuilder);
                    rowBuilder.append("</td>");
                    cellBuilder = new StringBuilder();
                } else {
                    applyAttributes(cellBuilder, text, attributes);
                }
                lastIndex = index;
            } else {
                applyAttributes(cellBuilder, text, attributes);
            }

            index++;
            if (index < commentItems.size()) {
                attributes = commentItems.get(index).getAttributes();
                text = commentItems.get(index).getText();
            }
        }
        if (!rowBuilder.toString().isEmpty()) {
            rowBuilder.append("</tr>");
            blockBuilder.append(rowBuilder);
            //            lastIndex = index;
        }
        blockBuilder.append("</tbody>");
        blockBuilder.append("</table>");
        return lastIndex;
    }

    private int buildListBlock(StringBuilder blockBuilder, List<ContentItemDTO> commentItems, int originIndex) {
        int index = originIndex;
        ContentItemDTO commentItem = commentItems.get(index);
        String text = commentItem.getText();
        AttributesDTO attributes = commentItem.getAttributes();
        StringBuilder listBuilder = new StringBuilder(blockBuilder);
        blockBuilder.delete(0, blockBuilder.length());
        int lastIndexList = index;
        String listType = attributes.getList();
        lastIndexList = appendList(blockBuilder, commentItems, index, text, attributes, listBuilder, lastIndexList, listType);
        if ("ordered".equals(listType)) {
            blockBuilder.insert(0, "<ol type=\"1\">");
            blockBuilder.append("</ol>");
        } else if ("bullet".equals(listType)) {
            blockBuilder.insert(0, "<ul>");
            blockBuilder.append("</ul>");
        }
        return lastIndexList;
    }

    private int appendList(
        StringBuilder blockBuilder,
        List<ContentItemDTO> commentItems,
        int index,
        String text,
        AttributesDTO attributes,
        StringBuilder listBuilder,
        int lastIndexList,
        String listType
    ) {
        while (
            index < commentItems.size() &&
            (Objects.isNull(attributes) || Objects.isNull(attributes.getBlockId()) || listType.equals(attributes.getList()))
        ) {
            if (Objects.isNull(attributes)) {
                listBuilder.append(text);
            } else if (Objects.nonNull(attributes.getList())) {
                if ("ordered".equals(attributes.getList())) {
                    appendOrdered(blockBuilder).append(listBuilder).append("</li>");
                } else if ("bullet".equals(attributes.getList())) {
                    appendBullet(blockBuilder).append(listBuilder).append("</li>");
                } else if ("checked".equals(attributes.getList())) {
                    blockBuilder.append("<div>");
                    appendChecked(blockBuilder).append(listBuilder).append("</input>");
                    blockBuilder.append("</div>");
                } else if ("unchecked".equals(attributes.getList())) {
                    blockBuilder.append("<div>");
                    appendUnchecked(blockBuilder).append(listBuilder).append("</input>");
                    blockBuilder.append("</div>");
                }
                lastIndexList = index;
                listBuilder = new StringBuilder();
            } else {
                applyAttributes(listBuilder, text, attributes);
            }

            index++;
            if (index < commentItems.size()) {
                attributes = commentItems.get(index).getAttributes();
                text = commentItems.get(index).getText();
            }
        }
        return lastIndexList;
    }

    private boolean isSingleBlock(String commentItemType) {
        return Constants.SINGLE_BLOCK_TYPES.stream().anyMatch(type -> type.equals(commentItemType));
    }

    private StringBuilder appendTableHeader(
        StringBuilder blockBuilder,
        StringBuilder tableBuilder,
        String beforeBlockType,
        AttributesDTO attributes
    ) {
        if ("tableHeader".equals(beforeBlockType)) {
            blockBuilder = new StringBuilder();
            appendCol(tableBuilder, attributes.getTableColWidth()).append("</col>");
        } else if (tableBuilder.toString().isEmpty()) {
            blockBuilder = new StringBuilder();
            tableBuilder.append("<table>").append("<colgroup>");
            appendCol(tableBuilder, attributes.getTableColWidth()).append("</col>");
        }
        return blockBuilder;
    }

    private StringBuilder appendEndOfOrderedList(StringBuilder htmlBuilder, StringBuilder blockBuilder, StringBuilder orderedBuilder) {
        blockBuilder.append(orderedBuilder);
        blockBuilder.append("</ol>");
        htmlBuilder.append(blockBuilder);
        orderedBuilder = new StringBuilder();
        return orderedBuilder;
    }

    private StringBuilder appendEndOfTable(StringBuilder htmlBuilder, StringBuilder blockBuilder, StringBuilder rowBuilder) {
        blockBuilder.append(rowBuilder);
        blockBuilder.append("</tr>");
        blockBuilder.append("</tbody>");
        blockBuilder.append("</table>");
        htmlBuilder.append(blockBuilder);
        rowBuilder = new StringBuilder();
        return rowBuilder;
    }

    private StringBuilder appendFirstRowTable(
        StringBuilder blockBuilder,
        StringBuilder rowBuilder,
        ContentItemDTO previousBlockItem,
        AttributesDTO attributes
    ) {
        String orderedBlock = blockBuilder.toString();
        blockBuilder = new StringBuilder();
        appendRow(rowBuilder);
        appendCell(rowBuilder, attributes.getTableCellLineRowspan(), attributes.getTableCellLineColspan())
            .append(orderedBlock)
            .append("</td>");
        if (
            Objects.nonNull(previousBlockItem) &&
            Objects.nonNull(previousBlockItem.getAttributes()) &&
            Objects.nonNull(previousBlockItem.getAttributes().getTableCellLineCell())
        ) {
            String indexNextRow = previousBlockItem.getAttributes().getTableCellLineCell().split("-")[0];
            String indexCurrentRow = attributes.getTableCellLineCell().split("-")[0];
            if (!indexCurrentRow.equals(indexNextRow)) {
                rowBuilder.append("</tr>");
            }
        }
        return blockBuilder;
    }

    private StringBuilder appendEndOfHeaderTable(StringBuilder htmlBuilder, StringBuilder tableBuilder) {
        if (!tableBuilder.toString().isEmpty()) {
            htmlBuilder.append(tableBuilder);
            htmlBuilder.append("</colgroup>");
            htmlBuilder.append("<tbody>");
            tableBuilder = new StringBuilder();
        }
        return tableBuilder;
    }

    private StringBuilder appendRow(
        StringBuilder blockBuilder,
        StringBuilder rowBuilder,
        ContentItemDTO previousBlockItem,
        AttributesDTO attributes
    ) {
        String rowBlock = blockBuilder.toString();
        blockBuilder = new StringBuilder();
        if (
            Objects.nonNull(previousBlockItem) &&
            Objects.nonNull(previousBlockItem.getAttributes()) &&
            Objects.nonNull(previousBlockItem.getAttributes().getTableCellLineCell())
        ) {
            String indexNextRow = previousBlockItem.getAttributes().getTableCellLineRow();
            String indexCurrentRow = attributes.getTableCellLineRow();
            if (!indexCurrentRow.equals(indexNextRow)) {
                rowBuilder.append("</tr>");
                appendRow(rowBuilder);
            }
        }
        appendCell(rowBuilder, attributes.getTableCellLineRowspan(), attributes.getTableCellLineColspan()).append(rowBlock).append("</td>");
        return blockBuilder;
    }

    private StringBuilder appendOrderedList(StringBuilder blockBuilder, StringBuilder orderedBuilder, String beforeBlockType) {
        if ("ordered".equals(beforeBlockType)) {
            String orderedBlock = blockBuilder.toString();
            blockBuilder = new StringBuilder();
            appendOrdered(orderedBuilder).append(orderedBlock).append("</li>");
        } else if (orderedBuilder.toString().isEmpty()) {
            String orderedBlock = blockBuilder.toString();
            blockBuilder = new StringBuilder();
            orderedBuilder.append("<ol type=\"1\">");
            appendOrdered(orderedBuilder).append(orderedBlock).append("</li>");
        }
        return blockBuilder;
    }

    private void appendImage(TaskHistory taskHistory, StringBuilder blockBuilder, ContentItemDTO commentItem, boolean isTaskImage) {
        blockBuilder.append("<a class=\"block_image\" href=\"").append(commentItem.getAttachmentId()).append("\">");
        if (isTaskImage) {
            blockBuilder
                .append("<img class=\"task_image page_break_inside\" src=\"")
                .append(taskHistory.getFullPath())
                .append("\\/")
                .append(commentItem.getAttachmentId())
                .append("\"/>");
        } else {
            blockBuilder
                .append("<img class=\"comment_image page_break_inside\" src=\"")
                .append(taskHistory.getFullPath())
                .append("\\/")
                .append(commentItem.getAttachmentId())
                .append("\"/>");
        }
        blockBuilder.append("</a>");
    }

    private void appendTaskMention(String taskAndColor, String url, StringBuilder blockBuilder) {
        String[] taskAndColorArray = taskAndColor.split(" \\| ");
        String taskName = taskAndColorArray[0];
        String taskColor = taskAndColorArray[1];
        blockBuilder
            .append("<span class=\"task_mention\"><a href=\"")
            .append(url)
            .append("\"><span class=\"task_mention_color\" style=\"background-color: ")
            .append(taskColor)
            .append(";\"></span>")
            .append(taskName)
            .append("</a>")
            .append("</span>");
    }

    private void applyAttributes(StringBuilder blockBuilder, String text, AttributesDTO attributes) {
        if (Boolean.FALSE.equals(attributes.getMultiple())) {
            if (Objects.nonNull(attributes.getAdvancedBannerColor())) {
                appendAdvancedBannerColor(blockBuilder, attributes).append(text).append("</div>");
            }
            if (Boolean.TRUE.equals(attributes.getBold())) {
                appendBold(blockBuilder).append(text).append("</strong>");
            }
            if (Boolean.TRUE.equals(attributes.getItalic())) {
                appendItalic(blockBuilder).append(text).append("</em>");
            }
            if (Boolean.TRUE.equals(attributes.getUnderline())) {
                appendUnderline(blockBuilder).append(text).append("</u>");
            }
            if (Objects.nonNull(attributes.getHeader())) {
                int headerLevel = attributes.getHeader();
                appendHeader(blockBuilder, headerLevel).append(text).append("</h").append(headerLevel).append(">");
            }
            if (Objects.nonNull(attributes.getStrike())) {
                appendStrike(blockBuilder).append(text).append("</span>");
            }
            if (Objects.nonNull(attributes.getBlockQuote())) {
                appendBlockQuote(blockBuilder).append(text).append("</div>");
            }

            if (Objects.nonNull(attributes.getColorClass())) {
                appendColorClass(blockBuilder, attributes).append(text).append("</span>");
            }

            if (Objects.nonNull(attributes.getAlign())) {
                appendAlign(blockBuilder, attributes).append(text).append("</p>");
            }

            if (Objects.nonNull(attributes.getIndent())) {
                appendIndent(blockBuilder, attributes).append(text).append("</p>");
            }

            if (Boolean.TRUE.equals(attributes.getCode())) {
                appendCode(blockBuilder).append(text).append("</div>");
            }

            if (Objects.nonNull(attributes.getLink())) {
                appendLink(blockBuilder, attributes).append(text).append("</a>");
            }

            if (Objects.nonNull(attributes.getList())) {
                if ("bullet".equals(attributes.getList())) {
                    appendBullet(blockBuilder).append(text).append("</li>");
                } else if ("checked".equals(attributes.getList())) {
                    appendChecked(blockBuilder).append(text).append("</input>");
                } else if ("unchecked".equals(attributes.getList())) {
                    appendUnchecked(blockBuilder).append(text).append("</input>");
                }
            }

            if (Boolean.TRUE.equals(attributes.getEmpty())) {
                blockBuilder.append(text);
            }
        } else {
            StringBuilder prefix = new StringBuilder();
            StringBuilder suffix = new StringBuilder();
            if (Objects.nonNull(attributes.getAdvancedBannerColor())) {
                appendAdvancedBannerColor(prefix, attributes);
                suffix.insert(0, "</div>");
            }
            if (Boolean.TRUE.equals(attributes.getBold())) {
                appendBold(prefix);
                suffix.insert(0, "</strong>");
            }
            if (Boolean.TRUE.equals(attributes.getItalic())) {
                appendItalic(prefix);
                suffix.insert(0, "</em>");
            }
            if (Boolean.TRUE.equals(attributes.getUnderline())) {
                appendUnderline(prefix);
                suffix.insert(0, "</u>");
            }
            if (Objects.nonNull(attributes.getHeader())) {
                int headerLevel = attributes.getHeader();
                appendHeader(prefix, headerLevel);
                suffix.insert(0, "</h" + headerLevel + ">");
            }
            if (Objects.nonNull(attributes.getStrike())) {
                appendStrike(prefix);
                suffix.insert(0, "</span>");
            }
            if (Objects.nonNull(attributes.getBlockQuote())) {
                appendBlockQuote(prefix);
                suffix.insert(0, "</blockquote>");
            }

            if (Objects.nonNull(attributes.getColorClass())) {
                appendColorClass(prefix, attributes);
                suffix.insert(0, "</span>");
            }

            if (Objects.nonNull(attributes.getAlign())) {
                appendAlign(prefix, attributes);
                suffix.insert(0, "</p>");
            }

            if (Objects.nonNull(attributes.getIndent())) {
                appendIndent(prefix, attributes);
                suffix.insert(0, "</p>");
            }

            if (Boolean.TRUE.equals(attributes.getCode())) {
                appendCode(prefix);
                suffix.insert(0, "</code>");
            }

            if (Objects.nonNull(attributes.getLink())) {
                appendLink(prefix, attributes);
                suffix.insert(0, "</a>");
            }
            if (Objects.nonNull(attributes.getList())) {
                if ("bullet".equals(attributes.getList())) {
                    appendBullet(prefix);
                    suffix.insert(0, "</li>");
                } else if ("checked".equals(attributes.getList())) {
                    appendChecked(prefix);
                    suffix.insert(0, "</input>");
                } else if ("unchecked".equals(attributes.getList())) {
                    appendUnchecked(prefix);
                    suffix.insert(0, "</input>");
                }
            }

            blockBuilder.append(prefix).append(text).append(suffix);
        }
    }

    private StringBuilder appendUnchecked(StringBuilder blockBuilder) {
        return blockBuilder.append("<input type=\"checkbox\">");
    }

    private StringBuilder appendChecked(StringBuilder blockBuilder) {
        return blockBuilder.append("<input type=\"checkbox\" checked>");
    }

    private StringBuilder appendBullet(StringBuilder blockBuilder) {
        return blockBuilder.append("<li>");
    }

    private StringBuilder appendOrdered(StringBuilder blockBuilder) {
        return blockBuilder.append("<li>");
    }

    private StringBuilder appendRow(StringBuilder blockBuilder) {
        return blockBuilder.append("<tr>");
    }

    private StringBuilder appendCell(StringBuilder blockBuilder, String rowspan, String colspan) {
        return blockBuilder.append("<td rowspan=\"").append(rowspan).append("\" colspan=\"").append(colspan).append("\">");
    }

    private StringBuilder appendColGroup(StringBuilder blockBuilder) {
        return blockBuilder.append("<colgroup>");
    }

    private StringBuilder appendCol(StringBuilder blockBuilder, String width) {
        return blockBuilder.append("<col width=\"").append(width).append("\">");
    }

    private StringBuilder appendCode(StringBuilder blockBuilder) {
        return blockBuilder.append("<div class=\"code\">");
    }

    private StringBuilder appendIndent(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder.append("<p style=\"padding-left: ").append(attributes.getIndent() * 40).append("px;\">");
    }

    private StringBuilder appendAlign(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder.append("<p style=\"text-align: ").append(attributes.getAlign()).append(";\">");
    }

    private StringBuilder appendColorClass(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder.append("<span style=\"color: ").append(attributes.getColorClass()).append(";\">");
    }

    private StringBuilder appendBlockQuote(StringBuilder blockBuilder) {
        return blockBuilder.append("<div class=\"quote\">");
    }

    private StringBuilder appendStrike(StringBuilder blockBuilder) {
        return blockBuilder.append("<span style=\"text-decoration: line-through;\">");
    }

    private StringBuilder appendHeader(StringBuilder blockBuilder, int headerLevel) {
        return blockBuilder.append("<h").append(headerLevel).append(">");
    }

    private StringBuilder appendUnderline(StringBuilder blockBuilder) {
        return blockBuilder.append("<u>");
    }

    private StringBuilder appendItalic(StringBuilder blockBuilder) {
        return blockBuilder.append("<em>");
    }

    private StringBuilder appendBold(StringBuilder blockBuilder) {
        return blockBuilder.append("<strong>");
    }

    private StringBuilder appendLink(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder.append("<a href=\"").append(attributes.getLink()).append("\">");
    }

    private StringBuilder appendAdvancedBannerColor(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder
            .append("<div class=\"banner\" style=\"background-color: ")
            .append(attributes.getAdvancedBannerBackgroundColor())
            .append("; border-left: 2px solid ")
            .append(attributes.getAdvancedBannerColor())
            .append(";\">");
    }
}

package com.formos.service.mapper;

import static com.formos.config.Constants.MMM_DD_AT_H_MM_A;

import com.formos.domain.Profile;
import com.formos.service.dto.clickup.*;
import com.formos.service.utils.CommonUtils;
import com.formos.service.utils.FileUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommentMapper {

    private final ContentItemMapper contentItemMapper;

    @Value("${clickup.base-folder}")
    private String baseFolder;

    public CommentMapper(ContentItemMapper contentItemMapper) {
        this.contentItemMapper = contentItemMapper;
    }

    public CommentDTO toCommentDTO(TaskHistory taskHistory, TaskComments.Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.id);
        commentDTO.setType(comment.type);
        commentDTO.setTimeStamp(Long.parseLong(comment.date));
        commentDTO.setDateTime(CommonUtils.formatToDateTimeFromTimestamp(comment.date, MMM_DD_AT_H_MM_A));
        commentDTO.setUsername(Objects.nonNull(comment.user) ? comment.user.username : null);
        commentDTO.setCommentItems(comment.commentDetails.stream().map(contentItemMapper::toContentItemDTO).collect(Collectors.toList()));
        commentDTO.setUser(new UserDTO(comment.user));
        commentDTO.setAssignedBy(new UserDTO(comment.assignedBy));
        commentDTO.setAssignee(new UserDTO(comment.assignee));
        commentDTO.setResolvedBy(new UserDTO(comment.resolvedBy));
        commentDTO.setResolved(comment.resolved);
        commentDTO.setHtmlText(buildHtml(taskHistory, commentDTO.getCommentItems()));
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
                htmlBuilder.append(text);
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
                        blockBuilder.append(text);
                    } else if ("divider".equals(commentItem.getType())) {
                        blockBuilder.append("<hr>");
                    } else if ("taskImage".equals(commentItem.getType())) {
                        appendImage(taskHistory, blockBuilder, commentItem, true);
                    } else if ("taskMention".equals(commentItem.getType())) {
                        appendTaskMention(commentItem.getText(), commentItem.getUrl(), blockBuilder);
                    }
                }
            }
        }
        return htmlBuilder.toString();
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

            if (Objects.nonNull(attributes.getColor_class())) {
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

            if (Objects.nonNull(attributes.getColor_class())) {
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
        return blockBuilder.append("<span style=\"color: ").append(attributes.getColor_class()).append(";\">");
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

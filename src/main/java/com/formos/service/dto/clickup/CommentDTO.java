package com.formos.service.dto.clickup;

import static com.formos.config.Constants.MMM_DD_AT_H_MM_A;

import com.formos.service.utils.CommonUtils;
import com.formos.service.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommentDTO {

    private List<CommentDTO> children = new ArrayList<>();
    private String id;
    private int type;
    private String username;
    private String dateTime;
    private Long timeStamp;
    private List<ContentItemDTO> commentItems;
    private UserDTO user;
    private UserDTO assignedBy;
    private UserDTO assignee;
    private UserDTO resolvedBy;
    private Boolean resolved;
    private String htmlText;

    public CommentDTO(String taskId, TaskComments.Comment comment) {
        this.id = comment.id;
        this.type = comment.type;
        this.timeStamp = Long.parseLong(comment.date);
        this.dateTime = CommonUtils.formatToDateTimeFromTimestamp(comment.date, MMM_DD_AT_H_MM_A);
        this.username = Objects.nonNull(comment.user) ? comment.user.username : null;
        this.commentItems = comment.commentDetails.stream().map(ContentItemDTO::new).collect(Collectors.toList());
        this.user = new UserDTO(comment.user);
        this.assignedBy = new UserDTO(comment.assignedBy);
        this.assignee = new UserDTO(comment.assignee);
        this.resolvedBy = new UserDTO(comment.resolvedBy);
        this.resolved = comment.resolved;
        this.htmlText = buildHtml(taskId);
    }

    public CommentDTO(TaskContentData taskContentData) {
        this.commentItems = taskContentData.contentItemData.stream().map(ContentItemDTO::new).collect(Collectors.toList());
    }

    public void addChild(CommentDTO commentDTO) {
        this.children.add(commentDTO);
    }

    public void addChildren(List<CommentDTO> commentDTOs) {
        this.children.addAll(commentDTOs);
    }

    public void addHistory(CommentDTO commentHistory) {
        this.user = commentHistory.getUser();
        this.assignedBy = commentHistory.getAssignedBy();
        this.assignee = commentHistory.getAssignee();
        this.resolvedBy = commentHistory.getResolvedBy();
        this.resolved = commentHistory.getResolved();
    }

    public String buildHtml(String taskId) {
        StringBuilder htmlBuilder = new StringBuilder();
        StringBuilder blockBuilder = new StringBuilder();
        StringBuilder orderedBuilder = new StringBuilder();
        StringBuilder tableBuilder = new StringBuilder();
        StringBuilder rowBuilder = new StringBuilder();
        int previousOrderedIndex = 0;
        int previousTableIndex = 0;
        int previousRowIndex = 0;
        ContentItemDTO previousBlockItem = null;
        String beforeBlockType = "";
        int size = this.commentItems.size();
        for (int index = 0; index < size; index++) {
            ContentItemDTO commentItem = this.commentItems.get(index);
            String text = commentItem.getText();
            if (Objects.nonNull(commentItem.getText()) && commentItem.getText().contains("This is just the HAQ")) {
                System.out.println(commentItem);
            }
            AttributesDTO attributes = commentItem.getAttributes();
            if (Objects.isNull(attributes)) {
                htmlBuilder.append(text);
            } else {
                if (Objects.nonNull(attributes.getBlockId())) {
                    if (Objects.nonNull(attributes.getList()) && ("ordered".equals(attributes.getList()))) {
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
                        beforeBlockType = "ordered";
                    }
                    if (Objects.nonNull(attributes.getTableCellLineCell())) {
                        if ("table".equals(beforeBlockType)) {
                            String rowBlock = blockBuilder.toString();
                            blockBuilder = new StringBuilder();
                            if (
                                Objects.nonNull(previousBlockItem) &&
                                Objects.nonNull(previousBlockItem.getAttributes()) &&
                                Objects.nonNull(previousBlockItem.getAttributes().getTableCellLineCell())
                            ) {
                                String indexNextRow = previousBlockItem.getAttributes().getTableCellLineCell().split("-")[0];
                                String indexCurrentRow = attributes.getTableCellLineCell().split("-")[0];
                                if (!indexCurrentRow.equals(indexNextRow)) {
                                    rowBuilder.append("</tr>");
                                    appendRow(rowBuilder);
                                }
                            }
                            appendCell(rowBuilder, attributes.getTableCellLineRowspan(), attributes.getTableCellLineColspan())
                                .append(rowBlock)
                                .append("</td>");
                        } else if (rowBuilder.toString().isEmpty()) {
                            if (!tableBuilder.toString().isEmpty()) {
                                htmlBuilder.append(tableBuilder);
                                htmlBuilder.append("</colgroup>");
                                htmlBuilder.append("<tbody>");
                                tableBuilder = new StringBuilder();
                            }
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
                        }
                        beforeBlockType = "table";
                        previousBlockItem = commentItem;
                    }
                    if (index == size - 1) {
                        if ((!rowBuilder.toString().isEmpty() && "table".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            blockBuilder.append(rowBuilder);
                            blockBuilder.append("</tr>");
                            blockBuilder.append("</tbody>");
                            blockBuilder.append("</table>");
                            htmlBuilder.append(blockBuilder);
                            rowBuilder = new StringBuilder();
                            blockBuilder = new StringBuilder();
                        } else if ((!orderedBuilder.toString().isEmpty() && "ordered".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            blockBuilder.append(orderedBuilder);
                            blockBuilder.append("</ol>");
                            htmlBuilder.append(blockBuilder);
                            orderedBuilder = new StringBuilder();
                            blockBuilder = new StringBuilder();
                        } else {
                            String block = blockBuilder.toString();
                            blockBuilder = new StringBuilder();
                            blockBuilder.append("<div>");
                            applyAttributes(blockBuilder, block, attributes);
                            blockBuilder.append("</div>");
                            htmlBuilder.append(blockBuilder);
                            blockBuilder = new StringBuilder();
                        }
                    } else {
                        if (
                            (!rowBuilder.toString().isEmpty() && "table".equals(beforeBlockType)) &&
                            Objects.isNull(attributes.getTableCellLineCell())
                        ) {
                            blockBuilder = new StringBuilder();
                            blockBuilder.append(rowBuilder);
                            blockBuilder.append("</tr>");
                            blockBuilder.append("</tbody>");
                            blockBuilder.append("</table>");
                            htmlBuilder.append(blockBuilder);
                            rowBuilder = new StringBuilder();
                            blockBuilder = new StringBuilder();
                        }
                        if ((!orderedBuilder.toString().isEmpty() && !"ordered".equals(beforeBlockType))) {
                            blockBuilder = new StringBuilder();
                            blockBuilder.append(orderedBuilder);
                            blockBuilder.append("</ol>");
                            htmlBuilder.append(blockBuilder);
                            orderedBuilder = new StringBuilder();
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
                } else {
                    if (Objects.nonNull(attributes.getTableColWidth())) {
                        ContentItemDTO nextItem = null;
                        ContentItemDTO nextTwoItem = null;
                        if (index + 1 < this.commentItems.size()) {
                            nextItem = this.commentItems.get(index + 1);
                        }
                        if (index + 2 < this.commentItems.size()) {
                            nextTwoItem = this.commentItems.get(index + 2);
                        }
                        if ("tableHeader".equals(beforeBlockType)) {
                            blockBuilder = new StringBuilder();
                            appendCol(tableBuilder, attributes.getTableColWidth()).append("</col>");
                            previousTableIndex = index;
                        } else if (tableBuilder.toString().isEmpty()) {
                            blockBuilder = new StringBuilder();
                            tableBuilder.append("<table>").append("<colgroup>");
                            appendCol(tableBuilder, attributes.getTableColWidth()).append("</col>");
                            previousTableIndex = index;
                        }
                        previousBlockItem = commentItem;
                        beforeBlockType = "tableHeader";
                    } else if ("attachment".equals(commentItem.getType())) {
                        if (commentItem.isImage()) {
                            appendImage(taskId, blockBuilder, commentItem, false);
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
                        appendImage(taskId, blockBuilder, commentItem, true);
                    } else {
                        applyAttributes(blockBuilder, text, attributes);
                    }
                }
            }
        }
        return htmlBuilder.toString();
    }

    private void appendImage(String taskId, StringBuilder blockBuilder, ContentItemDTO commentItem, boolean isTaskImage) {
        blockBuilder.append("<a class=\"block_image\" href=\"").append(commentItem.getAttachmentId()).append("\">");
        if (isTaskImage) {
            blockBuilder
                .append("<img class=\"task_image page_break_inside\" src=\"")
                .append(FileUtils.getOutputDirectoryForTask(taskId))
                .append(commentItem.getAttachmentId())
                .append("\"/>");
        } else {
            blockBuilder
                .append("<img class=\"comment_image page_break_inside\" src=\"")
                .append(FileUtils.getOutputDirectoryForTask(taskId))
                .append(commentItem.getAttachmentId())
                .append("\"/>");
        }
        blockBuilder.append("</a>");
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

    private StringBuilder appendAdvancedBannerColor(StringBuilder blockBuilder, AttributesDTO attributes) {
        return blockBuilder
            .append("<div class=\"banner\" style=\"background-color: ")
            .append(attributes.getAdvancedBannerBackgroundColor())
            .append("; border-left: 2px solid ")
            .append(attributes.getAdvancedBannerColor())
            .append(";\">");
    }

    public List<CommentDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CommentDTO> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<ContentItemDTO> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(List<ContentItemDTO> commentItems) {
        this.commentItems = commentItems;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserDTO assignedBy) {
        this.assignedBy = assignedBy;
    }

    public UserDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDTO assignee) {
        this.assignee = assignee;
    }

    public UserDTO getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UserDTO resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDTO that = (CommentDTO) o;
        return Objects.equals(id, that.id);
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "CommentDTO{" +
            "children=" +
            children +
            ", id='" +
            id +
            '\'' +
            ", type=" +
            type +
            ", username='" +
            username +
            '\'' +
            ", date='" +
            dateTime.toString() +
            '\'' +
            ", commentItems=" +
            commentItems +
            '}'
        );
    }
}

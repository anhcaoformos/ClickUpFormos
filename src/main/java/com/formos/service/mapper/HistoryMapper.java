package com.formos.service.mapper;

import com.formos.config.Constants;
import com.formos.service.dto.clickup.*;
import com.formos.service.utils.CommonUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import io.swagger.v3.core.util.Json;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class HistoryMapper {

    private final AttachmentMapper attachmentMapper;
    private final CommentMapper commentMapper;

    public HistoryMapper(AttachmentMapper attachmentMapper, CommentMapper commentMapper) {
        this.attachmentMapper = attachmentMapper;
        this.commentMapper = commentMapper;
    }

    public HistoryDTO toHistoryDTO(TaskHistory taskHistory, History history) {
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setId(history.id);
        //        historyDTO.setAttachments(CollectionUtils.isEmpty(history.attachments) ? new ArrayList<>() : history.attachments.stream().map(attachmentMapper::toAttachmentDTO).collect(Collectors.toList()));
        historyDTO.setType(history.type);
        //        historyDTO.setBefore(history.before);
        //        historyDTO.setAfter(history.after);
        historyDTO.setField(history.field);
        historyDTO.setDate(CommonUtils.formatToDateTimeFromTimestamp(history.date, Constants.MMM_DD_AT_H_MM_A));
        historyDTO.setUser(Objects.nonNull(history.user) ? new UserDTO(history.user) : null);
        if (("comment".equals(history.field) || "attachment_comment".equals(history.field)) && Objects.nonNull(history.comment)) {
            historyDTO.setComment(commentMapper.toCommentDTO(taskHistory, history.comment));
        }
        historyDTO.setDescription(buildDescription(taskHistory, history));
        return historyDTO;
    }

    private String buildDescription(TaskHistory taskHistory, History history) {
        return switch (history.field) {
            case "tag" -> getTagDescription(history);
            case "tag_removed" -> getTagRemoveDescription(history);
            case "name" -> getChangeNameDescription(history);
            case "due_date" -> getDueDateDescription(history);
            case "checklist_items_added" -> getChecklistItemsAddedDescription(history);
            case "checklist_item_resolved" -> getChecklistItemResolvedDescription(history);
            case "checklist_item_assignee" -> getChecklistItemAssigneeDescription(history);
            case "new_subtask" -> getNewSubtaskDescription(history);
            case "status" -> getChangeStatusDescription(history);
            case "comment_resolved" -> getResolvedCommentDescription(history);
            case "comment_assigned" -> getCommentAssignedDescription(history);
            case "attachment_comment" -> getAttachmentCommentDescription(history);
            case "attachments" -> getAttachmentsDescription(history, taskHistory.getFullPath());
            default -> "";
        };
    }

    private String getTagDescription(History history) {
        StringBuilder tagDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.after)) {
            tagDescription.append(history.user.username);
            Gson gson = new Gson();
            JsonArray tags = gson.toJsonTree(history.after).getAsJsonArray();
            if (tags.size() == 1) {
                tagDescription.append(" added tag ");
            } else {
                tagDescription.append(" added tags ");
            }
            tagDescription.append("<div class=\"row_start\">");
            tags.forEach(tagElement -> {
                JsonObject tag = tagElement.getAsJsonObject();
                tagDescription
                    .append("<div class=\"tag mr10px\"> style=\"background: ")
                    .append(tag.get("tag_bg"))
                    .append("; text-decoration: ")
                    .append(tag.get("tag_fg"))
                    .append(";>")
                    .append(tag.get("name"))
                    .append("</>");
            });
            tagDescription.append("</div>");
        }
        return tagDescription.toString();
    }

    private String getTagRemoveDescription(History history) {
        StringBuilder tagRemoveDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.before)) {
            tagRemoveDescription.append(history.user.username);
            Gson gson = new Gson();
            JsonArray tags = gson.toJsonTree(history.before).getAsJsonArray();
            if (tags.size() == 1) {
                tagRemoveDescription.append(" removed tag ");
            } else {
                tagRemoveDescription.append(" removed tags ");
            }
            tagRemoveDescription.append("<div class=\"row_start\">");
            tags.forEach(tagElement -> {
                JsonObject tag = tagElement.getAsJsonObject();
                tagRemoveDescription
                    .append("<div class=\"tag mr10px\"> style=\"background: ")
                    .append(tag.get("tag_bg"))
                    .append("; text-decoration: ")
                    .append(tag.get("tag_fg"))
                    .append(";>")
                    .append(tag.get("name"))
                    .append("</>");
            });
            tagRemoveDescription.append("</div>");
        }
        return tagRemoveDescription.toString();
    }

    private String getChangeNameDescription(History history) {
        StringBuilder changeNameDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.after)) {
            changeNameDescription.append(history.user.username);
            String name = history.after.toString();
            changeNameDescription.append(" changed name: ").append(name);
        }
        return changeNameDescription.toString();
    }

    private String getDueDateDescription(History history) {
        StringBuilder dueDateDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.after)) {
            dueDateDescription.append(history.user.username);
            String dueDate = history.after.toString();
            dueDateDescription
                .append(" set the due date to ")
                .append(CommonUtils.formatToDateTimeFromTimestamp(dueDate, Constants.MMM_DD_AT_H_MM_A));
        }
        return dueDateDescription.toString();
    }

    private String getChecklistItemsAddedDescription(History history) {
        StringBuilder checklistItemsAddedDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.checklist) && Objects.nonNull(history.checklistItems)) {
            checklistItemsAddedDescription.append(history.user.username);
            if (history.checklistItems.size() == 1) {
                checklistItemsAddedDescription.append(" added item ");
            } else {
                checklistItemsAddedDescription.append(" added items ");
            }
            checklistItemsAddedDescription.append(
                history.checklistItems.stream().map(checklistItem -> checklistItem.name).collect(Collectors.joining(", "))
            );
            checklistItemsAddedDescription.append(" to checklist ");
            checklistItemsAddedDescription.append(history.checklist.name);
        }
        return checklistItemsAddedDescription.toString();
    }

    private String getChecklistItemResolvedDescription(History history) {
        StringBuilder checklistItemResolvedDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.checklist) && Objects.nonNull(history.checklistItem)) {
            checklistItemResolvedDescription.append(history.user.username);
            checklistItemResolvedDescription.append(" checked ");
            checklistItemResolvedDescription.append(history.checklistItem.name);
            checklistItemResolvedDescription.append(" in ");
            checklistItemResolvedDescription.append(history.checklist.name);
        }
        return checklistItemResolvedDescription.toString();
    }

    private String getChecklistItemAssigneeDescription(History history) {
        StringBuilder checklistItemAssigneeDescription = new StringBuilder();
        if (
            Objects.nonNull(history.user) &&
            Objects.nonNull(history.after) &&
            Objects.nonNull(history.checklist) &&
            Objects.nonNull(history.checklistItem)
        ) {
            checklistItemAssigneeDescription.append(history.user.username);
            checklistItemAssigneeDescription.append(" assigned ");
            Gson gson = new Gson();
            JsonObject assignee = gson.toJsonTree(history.after).getAsJsonObject();
            checklistItemAssigneeDescription.append(CommonUtils.getStringPropertyOfJsonObject(assignee, "username"));
            checklistItemAssigneeDescription.append(" to ");
            checklistItemAssigneeDescription.append(history.checklistItem.name);
            checklistItemAssigneeDescription.append(" in ");
            checklistItemAssigneeDescription.append(history.checklist.name);
        }
        return checklistItemAssigneeDescription.toString();
    }

    private String getNewSubtaskDescription(History history) {
        StringBuilder newSubtaskDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.after)) {
            newSubtaskDescription.append(history.user.username);
            Gson gson = new Gson();
            JsonObject subtask = gson.toJsonTree(history.after).getAsJsonObject();
            newSubtaskDescription.append(" created subtask: ");
            String subtaskId = CommonUtils.getStringPropertyOfJsonObject(subtask, "id");
            String subtaskLink = Constants.TASK_URL_PREFIX + subtaskId;
            String subtaskName = CommonUtils.getStringPropertyOfJsonObject(subtask, "name");
            newSubtaskDescription.append("<a href=\"").append(subtaskLink).append("\">").append(subtaskName).append("</a>");
        }
        return newSubtaskDescription.toString();
    }

    private String getChangeStatusDescription(History history) {
        StringBuilder changeStatusDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.after) && Objects.nonNull(history.before)) {
            changeStatusDescription.append(history.user.username);
            Gson gson = new Gson();
            changeStatusDescription.append(" change status from ");
            JsonObject beforeObject = gson.toJsonTree(history.before).getAsJsonObject();
            changeStatusDescription.append(CommonUtils.getStringPropertyOfJsonObject(beforeObject, "status"));
            changeStatusDescription.append(" to ");
            JsonObject afterObject = gson.toJsonTree(history.after).getAsJsonObject();
            changeStatusDescription.append(CommonUtils.getStringPropertyOfJsonObject(afterObject, "status"));
        }
        return changeStatusDescription.toString();
    }

    private String getResolvedCommentDescription(History history) {
        StringBuilder resolvedCommentDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.comment)) {
            resolvedCommentDescription.append(history.user.username);
            if (history.comment.resolved) {
                resolvedCommentDescription.append(" resolved a comment: ");
            } else {
                resolvedCommentDescription.append(" reopened  a comment: ");
            }
            resolvedCommentDescription.append(history.comment.text_content);
        }
        return resolvedCommentDescription.toString();
    }

    private String getCommentAssignedDescription(History history) {
        StringBuilder commentAssignedDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.comment)) {
            commentAssignedDescription.append(history.user.username);
            commentAssignedDescription.append(" assigned a comment: ");
            commentAssignedDescription.append(history.comment.text_content);
        }
        return commentAssignedDescription.toString();
    }

    private String getAttachmentCommentDescription(History history) {
        StringBuilder attachmentCommentDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.data)) {
            attachmentCommentDescription.append(history.user.username);
            attachmentCommentDescription.append(" comment on attachment: ");
            attachmentCommentDescription.append(history.data.attachmentId);
        }
        return attachmentCommentDescription.toString();
    }

    private String getAttachmentsDescription(History history, String taskPath) {
        StringBuilder attachmentsDescription = new StringBuilder();
        if (Objects.nonNull(history.user) && Objects.nonNull(history.attachments)) {
            attachmentsDescription.append(history.user.username);
            if (history.attachments.size() == 1) {
                attachmentsDescription.append(" uploaded a file");
            } else {
                attachmentsDescription.append(" uploaded ").append(history.attachments.size()).append(" files");
            }
            attachmentsDescription.append("<div class=\"row_start\">");
            history.attachments.forEach(attachment -> {
                String imagePath = taskPath + "\\/" + attachment.id;
                attachmentsDescription
                    .append("<div class=\"attachment_item\"><img class=\"attachment_image page_break_inside\" src=\"")
                    .append(imagePath)
                    .append("\"/></div>");
            });
            attachmentsDescription.append("</div>");
        }
        return attachmentsDescription.toString();
    }
}

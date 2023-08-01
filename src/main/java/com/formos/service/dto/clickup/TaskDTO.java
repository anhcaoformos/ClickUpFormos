package com.formos.service.dto.clickup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskDTO {

    private String id;
    private String name;
    private String status;
    private String statusColor;
    private String description;
    private List<HistoryDTO> histories;
    private List<CommentDTO> highlightComments;
    private Set<AttachmentDTO> attachments = new HashSet<>();
    private String baseImagePath;
    private UserDTO creator;
    private String priority;
    private String priorityColor;
    private List<TagDTO> tags;
    private List<SubtaskDTO> subtasks;
    private List<ChecklistDTO> checklists;

    public TaskDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public void addAttachments(Set<AttachmentDTO> attachmentDTOs) {
        this.attachments.addAll(attachmentDTOs);
    }

    public String getBaseImagePath() {
        return baseImagePath;
    }

    public void setBaseImagePath(String baseImagePath) {
        this.baseImagePath = baseImagePath;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(String priorityColor) {
        this.priorityColor = priorityColor;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public List<SubtaskDTO> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<SubtaskDTO> subtasks) {
        this.subtasks = subtasks;
    }

    public List<ChecklistDTO> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<ChecklistDTO> checklists) {
        this.checklists = checklists;
    }

    public List<CommentDTO> getHighlightComments() {
        return highlightComments;
    }

    public void setHighlightComments(List<CommentDTO> highlightComments) {
        this.highlightComments = highlightComments;
    }

    public List<HistoryDTO> getHistories() {
        return histories;
    }

    public void setHistories(List<HistoryDTO> histories) {
        this.histories = histories;
    }

    @Override
    public String toString() {
        return (
            "TaskDTO{" +
            "id='" +
            id +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", status='" +
            status +
            '\'' +
            ", statusColor='" +
            statusColor +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", attachments=" +
            attachments +
            ", baseImagePath='" +
            baseImagePath +
            '\'' +
            ", creator=" +
            creator +
            ", priority='" +
            priority +
            '\'' +
            ", priorityColor='" +
            priorityColor +
            '\'' +
            ", tags=" +
            tags +
            ", subtasks=" +
            subtasks +
            '}'
        );
    }
}

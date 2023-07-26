package com.formos.service.dto.clickup;

import java.util.List;
import java.util.Set;

public class TaskDTO {

    private String id;
    private String name;
    private String status;
    private String statusColor;
    private String description;
    private List<CommentDTO> comments;
    private Set<AttachmentDTO> attachments;
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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
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
            ", comments=" +
            comments +
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

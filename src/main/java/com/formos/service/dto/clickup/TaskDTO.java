package com.formos.service.dto.clickup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskDTO {

    private String id;
    private String name;
    private String status;
    private String statusColor;
    private String description;
    private List<CommentDTO> comments;
    private Set<AttachmentDTO> attachments;
    private String baseImagePath;

    public TaskDTO(TaskData task) {
        this.id = task.id;
        this.name = task.name;
        this.status = Objects.nonNull(task.status) ? task.status.status.toUpperCase() : null;
        this.statusColor = Objects.nonNull(task.status) ? task.status.color : null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TaskContentData contentData = objectMapper.readValue(task.content, TaskContentData.class);
            this.description = new CommentDTO(contentData).buildHtml(task.id);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        this.attachments = task.attachments.stream().map(AttachmentDTO::new).collect(Collectors.toSet());
    }

    public TaskDTO(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.status = Objects.nonNull(task.status) ? task.status.status : null;
        this.description = task.description;
        this.attachments = task.attachments.stream().map(AttachmentDTO::new).collect(Collectors.toSet());
    }

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
            ", description='" +
            description +
            '\'' +
            ", comments=" +
            comments +
            ", attachments=" +
            attachments +
            '}'
        );
    }
}

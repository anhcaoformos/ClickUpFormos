package com.formos.service.dto.clickup;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SubtaskDTO {

    private String id;
    private String name;
    private String status;
    private String statusColor;
    private UserDTO creator;
    private List<UserDTO> assignees;
    private String priority;
    private String priorityColor;
    private List<TagDTO> tags;
    private String dueDate;

    public SubtaskDTO() {}

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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public List<UserDTO> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<UserDTO> assignees) {
        this.assignees = assignees;
    }

    @Override
    public String toString() {
        return (
            "SubtaskDTO{" +
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
            ", creator=" +
            creator +
            ", assignees=" +
            assignees +
            ", priority='" +
            priority +
            '\'' +
            ", priorityColor='" +
            priorityColor +
            '\'' +
            ", tags=" +
            tags +
            ", dueDate='" +
            dueDate +
            '\'' +
            '}'
        );
    }
}

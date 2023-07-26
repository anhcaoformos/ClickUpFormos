package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskData {

    String id;

    @JsonProperty("attachments")
    List<TaskComments.Attachment> attachments = new ArrayList<>();

    String name;
    Task.Status status;
    String content;
    TaskComments.User creator;
    Task.Priority priority;
    List<Task.Tag> tags;
    List<Task.Subtask> subtasks;
    List<Task.Checklist> checklists;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TaskComments.Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TaskComments.Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TaskComments.User getCreator() {
        return creator;
    }

    public void setCreator(TaskComments.User creator) {
        this.creator = creator;
    }

    public Task.Priority getPriority() {
        return priority;
    }

    public void setPriority(Task.Priority priority) {
        this.priority = priority;
    }

    public List<Task.Tag> getTags() {
        return tags;
    }

    public void setTags(List<Task.Tag> tags) {
        this.tags = tags;
    }

    public List<Task.Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Task.Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Task.Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(List<Task.Checklist> checklists) {
        this.checklists = checklists;
    }
}

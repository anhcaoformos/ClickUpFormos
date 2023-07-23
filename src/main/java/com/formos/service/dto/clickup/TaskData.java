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
}

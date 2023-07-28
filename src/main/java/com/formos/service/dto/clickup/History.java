package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class History {

    public String id;
    public Object before;
    public Object after;
    public int type;
    public TaskComments.Comment comment;
    public List<TaskComments.Attachment> attachments;
    public String field;

    @JsonProperty("parent_id")
    public String parentId;

    public TaskComments.User user;
    public Data data;
    public String date;
    public Task.Checklist checklist;
    public Task.ChecklistItem checklistItem;
    public List<Task.ChecklistItem> checklistItems;

    @Override
    public String toString() {
        return (
            "History{" +
            "id='" +
            id +
            '\'' +
            ", before=" +
            before +
            ", after=" +
            after +
            ", comment=" +
            comment +
            ", field='" +
            field +
            '\'' +
            ", parentId='" +
            parentId +
            '\'' +
            ", user=" +
            user +
            '}'
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        @JsonProperty("attachment_id")
        public String attachmentId;

        @JsonProperty("due_date_time")
        public Boolean dueDateTime;

        @JsonProperty("old_due_date_time")
        public Boolean oldDueDateTime;

        @JsonProperty("checklist_id")
        public String checklistId;

        @JsonProperty("status_type")
        public String statusType;
    }
}

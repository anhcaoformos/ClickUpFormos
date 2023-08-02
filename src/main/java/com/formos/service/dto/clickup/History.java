package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
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
    public int count;

    @SerializedName("parent_id")
    public String parentId;

    @SerializedName("custom_field")
    public CustomField customField;

    public TaskComments.User user;
    public Data data;
    public String date;
    public Task.Checklist checklist;

    @SerializedName("checklist_item")
    public Task.ChecklistItem checklistItem;

    @SerializedName("checklist_items")
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

        @SerializedName("attachment_id")
        public String attachmentId;

        @SerializedName("due_date_time")
        public Boolean dueDateTime;

        @SerializedName("old_due_date_time")
        public Boolean oldDueDateTime;

        @SerializedName("checklist_id")
        public String checklistId;

        @SerializedName("status_type")
        public String statusType;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomField {

        public String id;
        public String name;
        public String type;

        @SerializedName("type_config")
        public Object typeConfig;
    }
}

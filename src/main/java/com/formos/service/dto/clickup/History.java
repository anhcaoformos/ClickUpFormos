package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class History {

    public String id;
    public Object before;
    public Object after;
    public TaskComments.Comment comment;
    public String field;

    @JsonProperty("parent_id")
    public String parentId;

    public TaskComments.User user;
    public Data data;

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
    }
}

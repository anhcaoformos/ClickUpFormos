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
}

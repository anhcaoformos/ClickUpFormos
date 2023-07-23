package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskContentItemData {

    @JsonProperty("insert")
    public Object content;

    public TaskComments.Attributes attributes;
    public String type;
}

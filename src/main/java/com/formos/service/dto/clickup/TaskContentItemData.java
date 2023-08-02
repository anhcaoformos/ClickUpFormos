package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskContentItemData {

    @SerializedName("insert")
    public Object content;

    public TaskComments.Attributes attributes;
    public String type;
}

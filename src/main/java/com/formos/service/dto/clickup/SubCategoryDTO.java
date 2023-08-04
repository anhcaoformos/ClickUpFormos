package com.formos.service.dto.clickup;

import java.util.List;

public class SubCategoryDTO {

    private String id;
    private String name;
    private List<String> taskIds;

    public SubCategoryDTO() {}

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

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }
}

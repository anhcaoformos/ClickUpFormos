package com.formos.service.dto.clickup;

import java.util.List;

public class CategoryDTO {

    private String id;
    private String name;
    private List<SubCategoryDTO> subCategories;

    public CategoryDTO() {}

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

    public List<SubCategoryDTO> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategoryDTO> subCategories) {
        this.subCategories = subCategories;
    }
}

package com.formos.service.mapper;

import com.formos.service.dto.clickup.CategoryDTO;
import com.formos.service.dto.clickup.Team;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    private final SubCategoryMapper subCategoryMapper;

    public CategoryMapper(SubCategoryMapper subCategoryMapper) {
        this.subCategoryMapper = subCategoryMapper;
    }

    public CategoryDTO toCategoryDTO(Team.Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.id);
        categoryDTO.setName(category.name);
        if (Objects.nonNull(category.subCategories)) {
            categoryDTO.setSubCategories(subCategoryMapper.toSubCategoryDTOs(category.subCategories));
        }
        return categoryDTO;
    }

    public List<CategoryDTO> toCategoryDTOs(List<Team.Category> categories) {
        return categories.stream().map(this::toCategoryDTO).collect(Collectors.toList());
    }
}

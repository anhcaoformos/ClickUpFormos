package com.formos.service.mapper;

import com.formos.service.dto.clickup.SubCategoryDTO;
import com.formos.service.dto.clickup.Team;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SubCategoryMapper {

    public SubCategoryDTO toSubCategoryDTO(Team.SubCategory subCategory) {
        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
        subCategoryDTO.setId(subCategory.id);
        subCategoryDTO.setName(subCategory.name);
        return subCategoryDTO;
    }

    public List<SubCategoryDTO> toSubCategoryDTOs(List<Team.SubCategory> subCategories) {
        return subCategories.stream().map(this::toSubCategoryDTO).collect(Collectors.toList());
    }
}

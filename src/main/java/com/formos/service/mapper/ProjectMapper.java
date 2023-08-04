package com.formos.service.mapper;

import com.formos.service.dto.clickup.ProjectDTO;
import com.formos.service.dto.clickup.Team;
import com.formos.service.dto.clickup.UserDTO;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProjectMapper {

    private final CategoryMapper categoryMapper;

    public ProjectMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ProjectDTO toProjectDTO(Team.Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.id);
        projectDTO.setName(project.name);
        if (Objects.nonNull(project.categories)) {
            projectDTO.setCategories(categoryMapper.toCategoryDTOs(project.categories));
        }
        return projectDTO;
    }

    public List<ProjectDTO> toProjectDTOs(List<Team.Project> projects) {
        return projects.stream().map(this::toProjectDTO).collect(Collectors.toList());
    }
}

package com.formos.service.mapper;

import com.formos.service.dto.clickup.ProjectDTO;
import com.formos.service.dto.clickup.Team;
import com.formos.service.dto.clickup.TeamDTO;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TeamMapper {

    private final ProjectMapper projectMapper;

    public TeamMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public TeamDTO toTeamDTO(Team team) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(team.id);
        teamDTO.setName(team.name);
        if (Objects.nonNull(team.projects)) {
            teamDTO.setProjects(projectMapper.toProjectDTOs(team.projects));
        }
        return teamDTO;
    }

    public List<TeamDTO> toTeamDTOs(List<Team> teams) {
        return teams.stream().map(this::toTeamDTO).collect(Collectors.toList());
    }
}

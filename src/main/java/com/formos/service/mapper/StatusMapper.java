package com.formos.service.mapper;

import com.formos.service.dto.clickup.StatusDTO;
import com.formos.service.dto.clickup.Task;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatusMapper {

    public StatusDTO toStatusDTO(Task.Status status) {
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setId(status.id);
        statusDTO.setColor(status.color);
        statusDTO.setStatus(status.status);
        return statusDTO;
    }

    public List<StatusDTO> toStatusDTOs(List<Task.Status> statuses) {
        return statuses.stream().map(this::toStatusDTO).collect(Collectors.toList());
    }
}

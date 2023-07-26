package com.formos.service.mapper;

import com.formos.config.Constants;
import com.formos.service.dto.clickup.*;
import com.formos.service.utils.CommonUtils;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SubtaskMapper {

    public SubtaskDTO toSubtaskDTO(Task.Subtask subtask) {
        SubtaskDTO subtaskDTO = new SubtaskDTO();
        subtaskDTO.setId(subtask.id);
        subtaskDTO.setName(subtask.name);
        subtaskDTO.setStatus(Objects.nonNull(subtask.status) ? subtask.status.status.toUpperCase() : null);
        subtaskDTO.setStatusColor(Objects.nonNull(subtask.status) ? subtask.status.color : null);
        subtaskDTO.setPriority(Objects.nonNull(subtask.priority) ? subtask.priority.priority : null);
        subtaskDTO.setPriorityColor(Objects.nonNull(subtask.priority) ? subtask.priority.color : "none");
        if (!CollectionUtils.isEmpty(subtask.tags)) {
            subtaskDTO.setTags(subtask.tags.stream().map(TagDTO::new).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(subtask.assignees)) {
            subtaskDTO.setAssignees(subtask.assignees.stream().map(UserDTO::new).collect(Collectors.toList()));
        }
        subtaskDTO.setDueDate(CommonUtils.formatToDateTimeFromTimestamp(subtask.dueDate, Constants.MMM_DD_AT_H_MM_A));
        return subtaskDTO;
    }
}

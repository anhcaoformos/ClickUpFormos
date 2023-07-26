package com.formos.service.mapper;

import com.formos.service.dto.clickup.ChecklistItemDTO;
import com.formos.service.dto.clickup.Task;
import com.formos.service.dto.clickup.UserDTO;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ChecklistItemMapper {

    public ChecklistItemDTO toChecklistItemDTO(Task.ChecklistItem checklistItem) {
        ChecklistItemDTO checklistItemDTO = new ChecklistItemDTO();
        checklistItemDTO.setId(checklistItem.id);
        checklistItemDTO.setName(checklistItem.name);
        checklistItemDTO.setResolved(checklistItem.resolved);
        checklistItemDTO.setAssignee(Objects.nonNull(checklistItem.assignee) ? new UserDTO(checklistItem.assignee) : null);
        return checklistItemDTO;
    }
}

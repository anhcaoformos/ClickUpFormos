package com.formos.service.mapper;

import com.formos.service.dto.clickup.ChecklistDTO;
import com.formos.service.dto.clickup.Task;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class ChecklistMapper {

    private final ChecklistItemMapper checklistItemMapper;

    public ChecklistMapper(ChecklistItemMapper checklistItemMapper) {
        this.checklistItemMapper = checklistItemMapper;
    }

    public ChecklistDTO toChecklistDTO(Task.Checklist checklist) {
        ChecklistDTO checklistDTO = new ChecklistDTO();
        checklistDTO.setId(checklist.id);
        checklistDTO.setName(checklist.name);
        checklistDTO.setResolved(checklist.resolved);
        checklistDTO.setUnresolved(checklist.unresolved);
        checklistDTO.setCreatorId(checklist.creatorId);
        checklistDTO.setOrderIndex(checklist.orderIndex);
        checklistDTO.setItems(
            CollectionUtils.isEmpty(checklist.items)
                ? new ArrayList<>()
                : checklist.items.stream().map(checklistItemMapper::toChecklistItemDTO).collect(Collectors.toList())
        );
        return checklistDTO;
    }
}

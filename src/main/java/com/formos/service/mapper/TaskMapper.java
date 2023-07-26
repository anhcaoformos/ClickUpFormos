package com.formos.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formos.domain.Profile;
import com.formos.service.dto.clickup.*;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TaskMapper {

    private final CommentMapper commentMapper;
    private final AttachmentMapper attachmentMapper;
    private final SubtaskMapper subtaskMapper;

    public TaskMapper(CommentMapper commentMapper, AttachmentMapper attachmentMapper, SubtaskMapper subtaskMapper) {
        this.commentMapper = commentMapper;
        this.attachmentMapper = attachmentMapper;
        this.subtaskMapper = subtaskMapper;
    }

    public TaskDTO toTaskDTO(Profile profile, TaskData task, TaskHistory taskHistory) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setName(task.getName());
        taskDTO.setStatus(Objects.nonNull(task.getStatus()) ? task.getStatus().status.toUpperCase() : null);
        taskDTO.setStatusColor(Objects.nonNull(task.getStatus()) ? task.getStatus().color : null);
        taskDTO.setCreator(Objects.nonNull(task.getCreator()) ? new UserDTO(task.getCreator()) : null);
        taskDTO.setPriority(Objects.nonNull(task.getPriority()) ? task.getPriority().priority : null);
        taskDTO.setPriorityColor(Objects.nonNull(task.getPriority()) ? task.getPriority().color : "none");
        if (!CollectionUtils.isEmpty(task.getSubtasks())) {
            taskDTO.setSubtasks(task.getSubtasks().stream().map(subtaskMapper::toSubtaskDTO).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(task.getTags())) {
            taskDTO.setTags(task.getTags().stream().map(TagDTO::new).collect(Collectors.toList()));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TaskContentData contentData = objectMapper.readValue(task.getContent(), TaskContentData.class);
            taskDTO.setDescription(commentMapper.buildHtml(profile, taskHistory, contentData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        taskDTO.setAttachments(task.getAttachments().stream().map(attachmentMapper::toAttachmentDTO).collect(Collectors.toSet()));
        return taskDTO;
    }
}

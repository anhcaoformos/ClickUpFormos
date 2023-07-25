package com.formos.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formos.domain.Profile;
import com.formos.service.dto.clickup.*;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TaskMapper {

    private final CommentMapper commentMapper;

    public TaskMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public TaskDTO toTaskDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.id);
        taskDTO.setName(task.name);
        taskDTO.setStatus(Objects.nonNull(task.status) ? task.status.status : null);
        taskDTO.setDescription(task.description);
        taskDTO.setAttachments(task.attachments.stream().map(AttachmentDTO::new).collect(Collectors.toSet()));
        return taskDTO;
    }

    public TaskDTO toTaskDTO(Profile profile, TaskData task, TaskHistory taskHistory) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setName(task.getName());
        taskDTO.setStatus(Objects.nonNull(task.getStatus()) ? task.getStatus().status.toUpperCase() : null);
        taskDTO.setStatusColor(Objects.nonNull(task.getStatus()) ? task.getStatus().color : null);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TaskContentData contentData = objectMapper.readValue(task.getContent(), TaskContentData.class);
            taskDTO.setDescription(commentMapper.buildHtml(profile, taskHistory, contentData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        taskDTO.setAttachments(task.getAttachments().stream().map(AttachmentDTO::new).collect(Collectors.toSet()));
        return taskDTO;
    }
}

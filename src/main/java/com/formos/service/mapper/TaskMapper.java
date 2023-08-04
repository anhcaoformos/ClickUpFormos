package com.formos.service.mapper;

import com.formos.domain.Profile;
import com.formos.service.dto.clickup.*;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TaskMapper {

    private final CommentMapper commentMapper;
    private final AttachmentMapper attachmentMapper;
    private final SubtaskMapper subtaskMapper;
    private final ChecklistMapper checklistMapper;
    private final TagMapper tagMapper;
    private final Gson gson;

    public TaskMapper(
        CommentMapper commentMapper,
        AttachmentMapper attachmentMapper,
        SubtaskMapper subtaskMapper,
        ChecklistMapper checklistMapper,
        TagMapper tagMapper,
        Gson gson
    ) {
        this.commentMapper = commentMapper;
        this.attachmentMapper = attachmentMapper;
        this.subtaskMapper = subtaskMapper;
        this.checklistMapper = checklistMapper;
        this.tagMapper = tagMapper;
        this.gson = gson;
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
        taskDTO.setSubtasks(
            CollectionUtils.isEmpty(task.getSubtasks())
                ? new ArrayList<>()
                : task.getSubtasks().stream().map(subtaskMapper::toSubtaskDTO).collect(Collectors.toList())
        );
        taskDTO.setTags(
            CollectionUtils.isEmpty(task.getTags())
                ? new ArrayList<>()
                : task.getTags().stream().map(tagMapper::toTagDTO).collect(Collectors.toList())
        );
        taskDTO.setChecklists(
            CollectionUtils.isEmpty(task.getChecklists())
                ? new ArrayList<>()
                : task.getChecklists().stream().map(checklistMapper::toChecklistDTO).collect(Collectors.toList())
        );
        TaskContentData contentData = gson.fromJson(task.getContent(), TaskContentData.class);
        taskDTO.setDescription(commentMapper.buildContentHtml(profile, taskHistory, contentData));
        Set<AttachmentDTO> attachments = contentData.contentItemData
            .stream()
            .map(taskContentItemData -> {
                AttachmentDTO attachmentDTO = null;
                if (taskContentItemData.content instanceof HashMap) {
                    JsonObject content = gson.toJsonTree(taskContentItemData.content).getAsJsonObject();
                    if (content.has("attachment")) {
                        JsonObject attachment = content.get("attachment").getAsJsonObject();
                        attachmentDTO = attachmentMapper.toAttachmentDTO(attachment);
                    }
                }
                return attachmentDTO;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        taskDTO.addAttachments(attachments);
        taskDTO.addAttachments(task.getAttachments().stream().map(attachmentMapper::toAttachmentDTO).collect(Collectors.toSet()));
        return taskDTO;
    }
}

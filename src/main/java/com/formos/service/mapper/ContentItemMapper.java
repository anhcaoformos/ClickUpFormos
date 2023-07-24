package com.formos.service.mapper;

import com.formos.config.Constants;
import com.formos.domain.Profile;
import com.formos.service.ClickUpClientService;
import com.formos.service.dto.clickup.AttributesDTO;
import com.formos.service.dto.clickup.ContentItemDTO;
import com.formos.service.dto.clickup.TaskContentItemData;
import com.formos.service.utils.CommonUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import java.util.HashMap;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ContentItemMapper {

    private final ClickUpClientService clickUpClientService;

    public ContentItemMapper(ClickUpClientService clickUpClientService) {
        this.clickUpClientService = clickUpClientService;
    }

    ContentItemDTO toContentItemDTO(Profile profile, TaskContentItemData taskContentItemData) {
        ContentItemDTO contentItemDTO = new ContentItemDTO();
        contentItemDTO.setAttributes(new AttributesDTO(taskContentItemData.attributes));
        if (taskContentItemData.content instanceof String) {
            contentItemDTO.setText(taskContentItemData.content.toString());
        } else if (taskContentItemData.content instanceof HashMap) {
            Gson gson = new Gson();
            JsonObject content = gson.toJsonTree(taskContentItemData.content).getAsJsonObject();
            if (content.has("attachment")) {
                JsonObject attachment = content.get("attachment").getAsJsonObject();

                contentItemDTO.setText(CommonUtils.getStringPropertyOfJsonObject(attachment, "name"));
                contentItemDTO.setAttachmentId(CommonUtils.getStringPropertyOfJsonObject(attachment, "id"));
                contentItemDTO.setUrl(CommonUtils.getStringPropertyOfJsonObject(attachment, "url"));
                contentItemDTO.setExtension(CommonUtils.getStringPropertyOfJsonObject(attachment, "extension"));
                contentItemDTO.setImage(Constants.IMAGE_EXTENSION.contains(contentItemDTO.getExtension()));
                contentItemDTO.setType("attachment");
            }

            if (content.has("emoticon")) {
                contentItemDTO.setEmoticonCode(
                    CommonUtils.getStringPropertyOfJsonObject(content.get("emoticon").getAsJsonObject(), "code")
                );
                if (Objects.nonNull(contentItemDTO.getEmoticonCode())) {
                    contentItemDTO.setText("&#" + Integer.parseInt(contentItemDTO.getEmoticonCode(), 16));
                }
                contentItemDTO.setType("emoticon");
            }

            if (content.has("user_mention")) {
                JsonObject userMention = content.get("user_mention").getAsJsonObject();
                contentItemDTO.setText("@" + CommonUtils.getStringPropertyOfJsonObject(userMention, "name"));
                contentItemDTO.setType("userMention");
            }

            if (content.has("image")) {
                contentItemDTO.setText(CommonUtils.getStringPropertyOfJsonObject(content, "image"));
                contentItemDTO.setAttachmentId(contentItemDTO.getAttributes().getDataId());
                contentItemDTO.setType("taskImage");
            }

            if (content.has("table-col")) {
                JsonObject tableCol = content.get("table-col").getAsJsonObject();
                contentItemDTO.setText(CommonUtils.getStringPropertyOfJsonObject(tableCol, "width"));
                contentItemDTO.setType("tableCol");
            }

            if (content.has("task_mention")) {
                JsonObject taskMention = content.get("task_mention").getAsJsonObject();
                String taskId = CommonUtils.getStringPropertyOfJsonObject(taskMention, "task_id");
                contentItemDTO.setUrl(Constants.TASK_URL_PREFIX + taskId);
                contentItemDTO.setText(clickUpClientService.getTaskTitle(profile, taskId));
                contentItemDTO.setType("taskMention");
            }

            if (content.has("divider")) {
                contentItemDTO.setType("divider");
            }
        }
        return contentItemDTO;
    }
}

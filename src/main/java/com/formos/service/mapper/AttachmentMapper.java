package com.formos.service.mapper;

import com.formos.config.Constants;
import com.formos.service.dto.clickup.AttachmentDTO;
import com.formos.service.dto.clickup.ContentItemDTO;
import com.formos.service.dto.clickup.TaskComments;
import com.formos.service.utils.CommonUtils;
import com.nimbusds.jose.shaded.gson.JsonObject;
import org.springframework.stereotype.Service;

@Service
public class AttachmentMapper {

    public AttachmentDTO toAttachmentDTO(TaskComments.Attachment attachment) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setId(attachment.id);
        attachmentDTO.setUrl(attachment.url);
        attachmentDTO.setTitle(attachment.title);
        attachmentDTO.setDateTime(CommonUtils.formatToDateTimeFromTimestamp(attachment.date, Constants.MMM_DD_AT_H_MM_A));
        attachmentDTO.setImage(CommonUtils.isImage(attachment.extension));
        return attachmentDTO;
    }

    public AttachmentDTO toAttachmentDTO(String dateTime, ContentItemDTO commentItem) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setId(commentItem.getAttachmentId());
        attachmentDTO.setUrl(commentItem.getUrl());
        attachmentDTO.setTitle(commentItem.getText());
        attachmentDTO.setDateTime(dateTime);
        attachmentDTO.setImage(commentItem.isImage());
        return attachmentDTO;
    }

    public AttachmentDTO toAttachmentDTO(JsonObject attachment) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setTitle(CommonUtils.getStringPropertyOfJsonObject(attachment, "name"));
        attachmentDTO.setId(CommonUtils.getStringPropertyOfJsonObject(attachment, "id"));
        attachmentDTO.setUrl(CommonUtils.getStringPropertyOfJsonObject(attachment, "url"));
        attachmentDTO.setImage(CommonUtils.isImage(CommonUtils.getStringPropertyOfJsonObject(attachment, "extension")));
        attachmentDTO.setDateTime(
            CommonUtils.formatToDateTimeFromTimestamp(
                CommonUtils.getStringPropertyOfJsonObject(attachment, "date"),
                Constants.MMM_DD_AT_H_MM_A
            )
        );
        return attachmentDTO;
    }
}

package com.formos.service.dto.clickup;

import com.formos.config.Constants;
import com.formos.service.utils.CommonUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import java.util.HashMap;
import java.util.Objects;

public class ContentItemDTO {

    private String text;
    private String type;
    private boolean isImage;
    private String url;
    private String attachmentId;
    private AttributesDTO attributes;
    private String emoticonCode;
    private String extension;
    private String userMention;

    public ContentItemDTO() {}

    public ContentItemDTO(TaskComments.CommentItem commentItem) {
        this.text = commentItem.text;
        this.type = commentItem.type;
        this.attributes = Objects.nonNull(commentItem.attributes) ? new AttributesDTO(commentItem.attributes) : null;
        if (Objects.nonNull(commentItem.attachment)) {
            this.url = commentItem.attachment.url;
            this.attachmentId = commentItem.attachment.id;
            this.extension = commentItem.attachment.extension;
            this.isImage = Constants.IMAGE_EXTENSION.contains(commentItem.attachment.extension);
        }
        if (Objects.nonNull(commentItem.emoticon) && Objects.nonNull(commentItem.emoticon.code)) {
            this.emoticonCode = commentItem.emoticon.code;
        }
    }

    //    public ContentItemDTO(TaskContentItemData taskContentItemData) {
    //        this.attributes = new AttributesDTO(taskContentItemData.attributes);
    //        if (taskContentItemData.content instanceof String) {
    //            this.text = taskContentItemData.content.toString();
    //        } else if (taskContentItemData.content instanceof HashMap) {
    //            Gson gson = new Gson();
    //            JsonObject content = gson.toJsonTree(taskContentItemData.content).getAsJsonObject();
    //            if (content.has("attachment")) {
    //                JsonObject attachment = content.get("attachment").getAsJsonObject();
    //
    //                this.text = CommonUtils.getStringPropertyOfJsonObject(attachment, "name");
    //                this.attachmentId = CommonUtils.getStringPropertyOfJsonObject(attachment,"id");
    //                this.url = CommonUtils.getStringPropertyOfJsonObject(attachment,"url");
    //                this.extension = CommonUtils.getStringPropertyOfJsonObject(attachment,"extension");
    //                this.isImage = Constants.IMAGE_EXTENSION.contains(this.extension);
    //                this.type = "attachment";
    //            }
    //
    //            if (content.has("emoticon")) {
    //                this.emoticonCode = CommonUtils.getStringPropertyOfJsonObject(content.get("emoticon").getAsJsonObject(),"code");
    //                if (Objects.nonNull(this.emoticonCode)) {
    //                    this.text = "&#" + Integer.parseInt(this.emoticonCode, 16);
    //                }
    //                this.type = "emoticon";
    //            }
    //
    //            if (content.has("user_mention")) {
    //                JsonObject userMention = content.get("user_mention").getAsJsonObject();
    //                this.text = "@" + CommonUtils.getStringPropertyOfJsonObject(userMention, "name");
    //                this.type = "userMention";
    //            }
    //
    //            if (content.has("image")) {
    //                this.text = CommonUtils.getStringPropertyOfJsonObject(content, "image");
    //                this.attachmentId = this.attributes.getDataId();
    //                this.type = "taskImage";
    //            }
    //
    //            if (content.has("table-col")) {
    //                JsonObject tableCol = content.get("table-col").getAsJsonObject();
    //                this.text = CommonUtils.getStringPropertyOfJsonObject(tableCol, "width");
    //                this.type = "tableCol";
    //            }
    //
    //            if (content.has("task_mention")) {
    //                JsonObject taskMention = content.get("task_mention").getAsJsonObject();
    //                String taskId = CommonUtils.getStringPropertyOfJsonObject(taskMention, "task_id");
    //                this.url = Constants.TASK_URL_PREFIX + taskId;
    //                this.text = ClickUpCall.getTaskTitle(taskId);
    //                this.type = "taskMention";
    //            }
    //
    //            if (content.has("divider")) {
    //                this.type = "divider";
    //            }
    //        }
    //    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public AttributesDTO getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesDTO attributes) {
        this.attributes = attributes;
    }

    public String getEmoticonCode() {
        return emoticonCode;
    }

    public void setEmoticonCode(String emoticonCode) {
        this.emoticonCode = emoticonCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUserMention() {
        return userMention;
    }

    public void setUserMention(String userMention) {
        this.userMention = userMention;
    }

    @Override
    public String toString() {
        return (
            "CommentItemDTO{" +
            "text='" +
            text +
            '\'' +
            ", type='" +
            type +
            '\'' +
            ", isImage=" +
            isImage +
            ", url='" +
            url +
            '\'' +
            ", attachmentId='" +
            attachmentId +
            '\'' +
            ", attributes=" +
            attributes +
            ", emoticonCode='" +
            emoticonCode +
            '\'' +
            '}'
        );
    }
}

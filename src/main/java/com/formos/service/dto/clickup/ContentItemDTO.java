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
            "ContentItemDTO{" +
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
            ", extension='" +
            extension +
            '\'' +
            ", userMention='" +
            userMention +
            '\'' +
            '}'
        );
    }
}

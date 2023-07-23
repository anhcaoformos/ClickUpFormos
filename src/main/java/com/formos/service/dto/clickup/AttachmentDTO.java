package com.formos.service.dto.clickup;

import com.formos.config.Constants;
import com.formos.service.utils.CommonUtils;
import java.util.Objects;

public class AttachmentDTO {

    private String id;
    private String url;
    private String title;
    private String dateTime;
    private boolean isImage;

    public AttachmentDTO(TaskComments.Attachment attachment) {
        this.id = attachment.id;
        this.url = attachment.url;
        this.title = attachment.title;
        this.dateTime = CommonUtils.formatToDateTimeFromTimestamp(attachment.date, Constants.MMM_DD_AT_H_MM_A);
        this.isImage = Constants.IMAGE_EXTENSION.contains(attachment.extension);
    }

    public AttachmentDTO(String dateTime, ContentItemDTO commentItem) {
        this.id = commentItem.getAttachmentId();
        this.url = commentItem.getUrl();
        this.title = commentItem.getText();
        this.dateTime = dateTime;
        this.isImage = commentItem.isImage();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentDTO that = (AttachmentDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

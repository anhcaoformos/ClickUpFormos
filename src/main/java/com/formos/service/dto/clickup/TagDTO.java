package com.formos.service.dto.clickup;

public class TagDTO {

    private Long creatorId;
    private String name;
    private String tagBackground;
    private String tagForeground;

    public TagDTO() {}

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagBackground() {
        return tagBackground;
    }

    public void setTagBackground(String tagBackground) {
        this.tagBackground = tagBackground;
    }

    public String getTagForeground() {
        return tagForeground;
    }

    public void setTagForeground(String tagForeground) {
        this.tagForeground = tagForeground;
    }

    @Override
    public String toString() {
        return (
            "TagDTO{" +
            "creatorId=" +
            creatorId +
            ", name='" +
            name +
            '\'' +
            ", tagBackground='" +
            tagBackground +
            '\'' +
            ", tagForeground='" +
            tagForeground +
            '\'' +
            '}'
        );
    }
}

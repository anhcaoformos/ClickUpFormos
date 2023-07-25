package com.formos.service.dto.clickup;

import static com.formos.config.Constants.MMM_DD_AT_H_MM_A;

import com.formos.service.utils.CommonUtils;
import com.formos.service.utils.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommentDTO {

    private List<CommentDTO> children = new ArrayList<>();
    private String id;
    private int type;
    private String username;
    private String dateTime;
    private Long timeStamp;
    private List<ContentItemDTO> commentItems;
    private UserDTO user;
    private UserDTO assignedBy;
    private UserDTO assignee;
    private UserDTO resolvedBy;
    private Boolean resolved;
    private String htmlText;

    public CommentDTO() {}

    public void addChild(CommentDTO commentDTO) {
        this.children.add(commentDTO);
    }

    public void addChildren(List<CommentDTO> commentDTOs) {
        this.children.addAll(commentDTOs);
    }

    public void addHistory(CommentDTO commentHistory) {
        this.user = commentHistory.getUser();
        this.assignedBy = commentHistory.getAssignedBy();
        this.assignee = commentHistory.getAssignee();
        this.resolvedBy = commentHistory.getResolvedBy();
        this.resolved = commentHistory.getResolved();
    }

    public List<CommentDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CommentDTO> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<ContentItemDTO> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(List<ContentItemDTO> commentItems) {
        this.commentItems = commentItems;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public UserDTO getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UserDTO assignedBy) {
        this.assignedBy = assignedBy;
    }

    public UserDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDTO assignee) {
        this.assignee = assignee;
    }

    public UserDTO getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UserDTO resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDTO that = (CommentDTO) o;
        return Objects.equals(id, that.id);
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (
            "CommentDTO{" +
            "children=" +
            children +
            ", id='" +
            id +
            '\'' +
            ", type=" +
            type +
            ", username='" +
            username +
            '\'' +
            ", date='" +
            dateTime.toString() +
            '\'' +
            ", commentItems=" +
            commentItems +
            '}'
        );
    }
}

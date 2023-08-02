package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskComments {

    public ArrayList<Comment> comments;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attributes {

        @SerializedName("block-id")
        public String blockId;

        public Boolean bold;
        public Boolean italic;
        public Boolean underline;
        public Integer header;
        public Boolean strike;
        public List list;
        public String link;

        @SerializedName("color-class")
        public String color_class;

        public String align;
        public Integer indent;
        public Boolean code;

        @SerializedName("blockquote")
        public Object blockQuote;

        @SerializedName("advanced-banner-color")
        public String advancedBannerColor;

        @SerializedName("data-id")
        public String dataId;

        @SerializedName("table-col")
        public TableCol tableCol;

        @SerializedName("table-cell-line")
        public TableCellLine tableCellLine;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class List {

        public String list;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TableCellLine {

        public String row;

        @SerializedName("rowspan")
        public String rowspan;

        @SerializedName("colspan")
        public String colspan;

        public String cell;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TableCol {

        public String width;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Comment {

        public String id;

        @SerializedName("comment")
        public ArrayList<CommentItem> commentDetails;

        public String comment_text;

        public String text_content;
        public String parent;

        public int type;
        public Boolean resolved;
        public User user;

        @SerializedName("assigned_by")
        public User assignedBy;

        public User assignee;

        @SerializedName("resolved_by")
        public User resolvedBy;

        public ArrayList<Object> reactions;
        public String date;

        @Override
        public String toString() {
            return (
                "Comment{" +
                "id='" +
                id +
                '\'' +
                ", commentDetails=" +
                commentDetails +
                ", comment_text='" +
                comment_text +
                '\'' +
                ", text_content='" +
                text_content +
                '\'' +
                ", parent='" +
                parent +
                '\'' +
                ", type=" +
                type +
                ", user=" +
                user +
                ", assignedBy=" +
                assignedBy +
                ", assignee=" +
                assignee +
                ", resolvedBy=" +
                resolvedBy +
                ", reactions=" +
                reactions +
                ", date='" +
                date +
                '\'' +
                '}'
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentItem {

        public String text;
        public Attributes attributes;
        public String type;

        public Attachment attachment;
        public Emoticon emoticon;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Emoticon {

        public String code;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {

        public String id;
        public String date;
        public String title;
        public int type;
        public int source;
        public int version;
        public String extension;
        public String thumbnail_small;
        public String thumbnail_medium;
        public String thumbnail_large;
        public Object is_folder;
        public String mimetype;
        public boolean hidden;
        public String parent_id;
        public int size;
        public int total_comments;
        public int resolved_comments;
        public User user;
        public boolean deleted;
        public Object orientation;
        public String url;
        public int parent_comment_type;
        public String parent_comment_parent;
        public Object email_data;
        public String url_w_query;
        public String url_w_host;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {

        public Long id;
        public String username;
        public String email;
        public String color;
        public String initials;
        public String profilePicture;
    }

    @Override
    public String toString() {
        return "TaskComments{" + "comments=" + comments + '}';
    }
}

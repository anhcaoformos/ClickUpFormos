package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

    public String id;
    public String custom_id;
    public String name;
    public String text_content;
    public String description;
    public Status status;
    public String orderindex;
    public String date_created;
    public String date_updated;
    public Object date_closed;
    public String date_done;
    public boolean archived;
    public Creator creator;
    public ArrayList<Object> assignees;
    public ArrayList<Watcher> watchers;
    public ArrayList<Object> checklists;
    public ArrayList<Tag> tags;
    public Object parent;
    public Priority priority;
    public long due_date;
    public long start_date;
    public Object points;
    public int time_estimate;
    public int time_spent;
    public ArrayList<Object> custom_fields;
    public ArrayList<Object> dependencies;
    public ArrayList<Object> linked_tasks;
    public String team_id;
    public String url;
    public Sharing sharing;
    public String permission_level;
    public List list;
    public Project project;
    public Folder folder;
    public Space space;
    public ArrayList<TaskComments.Attachment> attachments;
    public ArrayList<Subtask> subtasks;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subtask {

        public String id;
        public String name;
        public ArrayList<TaskComments.User> assignees;
        public Priority priority;
        public Status status;

        @JsonProperty("due_date")
        public String dueDate;

        public ArrayList<Tag> tags;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {

        @JsonProperty("creator")
        public long creatorId;

        public String name;

        @JsonProperty("tag_bg")
        public String tagBackground;

        @JsonProperty("tag_fg")
        public String tagForeground;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Priority {

        public String id;
        public String color;

        @JsonProperty("orderindex")
        public String orderIndex;

        public String priority;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Creator {

        public int id;
        public String username;
        public String color;
        public String email;
        public String profilePicture;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Folder {

        public String id;
        public String name;
        public boolean hidden;
        public boolean access;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class List {

        public String id;
        public String name;
        public boolean access;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Project {

        public String id;
        public String name;
        public boolean hidden;
        public boolean access;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sharing {

        @JsonProperty("public")
        public boolean mypublic;

        public String public_share_expires_on;
        public ArrayList<Object> public_fields;
        public String token;
        public boolean seo_optimized;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Space {

        public String id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {

        public String id;
        public String status;
        public String color;
        public int orderindex;
        public String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Watcher {

        public int id;
        public String username;
        public String color;
        public String initials;
        public String email;
        public String profilePicture;
    }

    @Override
    public String toString() {
        return (
            "Task{" +
            "id='" +
            id +
            '\'' +
            ", custom_id=" +
            custom_id +
            ", name='" +
            name +
            '\'' +
            ", text_content=" +
            text_content +
            ", description=" +
            description +
            ", status=" +
            status +
            ", orderindex='" +
            orderindex +
            '\'' +
            ", date_created='" +
            date_created +
            '\'' +
            ", date_updated='" +
            date_updated +
            '\'' +
            ", date_closed=" +
            date_closed +
            ", date_done='" +
            date_done +
            '\'' +
            ", archived=" +
            archived +
            ", creator=" +
            creator +
            ", assignees=" +
            assignees +
            ", watchers=" +
            watchers +
            ", checklists=" +
            checklists +
            ", tags=" +
            tags +
            ", parent=" +
            parent +
            ", priority=" +
            priority +
            ", due_date=" +
            due_date +
            ", start_date=" +
            start_date +
            ", points=" +
            points +
            ", time_estimate=" +
            time_estimate +
            ", time_spent=" +
            time_spent +
            ", custom_fields=" +
            custom_fields +
            ", dependencies=" +
            dependencies +
            ", linked_tasks=" +
            linked_tasks +
            ", team_id='" +
            team_id +
            '\'' +
            ", url='" +
            url +
            '\'' +
            ", sharing=" +
            sharing +
            ", permission_level='" +
            permission_level +
            '\'' +
            ", list=" +
            list +
            ", project=" +
            project +
            ", folder=" +
            folder +
            ", space=" +
            space +
            ", attachments=" +
            attachments +
            '}'
        );
    }
}

package com.formos.service.dto.clickup;

import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
import java.util.List;

public class Team {

    public String id;
    public String name;
    public List<Project> projects;

    public static class Project {

        public String id;
        public String name;
        public List<Category> categories;
        public List<Task.Status> statuses;
        public List<Member> members;
    }

    public static class Category {

        public String id;
        public String name;

        @SerializedName("subcategories")
        public List<SubCategory> subCategories;
    }

    public static class SubCategory {

        public String id;
        public String name;
    }

    public static class Member {

        public String joinedDate;
        public TaskComments.User user;
    }
}

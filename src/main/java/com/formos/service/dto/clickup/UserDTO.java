package com.formos.service.dto.clickup;

import java.util.Objects;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String color;
    private String initials;
    private String profilePicture;

    public UserDTO(TaskComments.User user) {
        if (Objects.nonNull(user)) {
            this.id = user.id;
            this.username = user.username;
            this.email = user.email;
            this.color = user.color;
            this.initials = user.initials;
            this.profilePicture = user.profilePicture;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}

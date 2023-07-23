package com.formos.web.rest.errors;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class ProfileNotFoundException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ProfileNotFoundException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Login name already used!", "userManagement", "userexists");
    }
}

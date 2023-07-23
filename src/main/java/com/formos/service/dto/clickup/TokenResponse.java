package com.formos.service.dto.clickup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    public String token;
}

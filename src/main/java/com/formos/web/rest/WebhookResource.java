package com.formos.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookResource {

    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    public WebhookResource() {}

    @PostMapping("/webhook-endpoint")
    public void handleWebhookEvent(@RequestBody String payload) {
        System.out.println("Received webhook payload: " + payload);
    }
}

package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookResponse {
    private String webhook;
    private String accessToken;
}

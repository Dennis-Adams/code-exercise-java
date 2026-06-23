package com.exercise.urlshortener.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class ShortenUrlRequest {

    // @NotBlank ensures the string is not null, not empty, and not just whitespace
    @NotBlank(message = "fullUrl is required")
    private String fullUrl;

    private String customAlias; // This is optional, according to the openapi.yaml

    // Getters and Setters
    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}

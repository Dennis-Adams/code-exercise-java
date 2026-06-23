package com.exercise.urlshortener.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlRequest {
    // @NotBlank ensures the string is not null, not empty, and not just whitespace
    @NotBlank(message = "fullUrl is required")
    private String fullUrl;

    private String customAlias; // This is optional, according to the openapi.yaml
}

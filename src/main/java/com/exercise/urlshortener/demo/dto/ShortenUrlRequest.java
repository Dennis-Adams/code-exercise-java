package com.exercise.urlshortener.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlRequest {
    // @NotBlank ensures the string is not null, not empty, and not just whitespace
    @NotBlank(message = "fullUrl is required")

    // check URL format
    @Pattern(
            regexp = "^https?://(localhost|(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6})\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)$",
            message = "Invalid URL format")

    private String fullUrl;

    @Pattern(
            regexp = "^[a-zA-Z0-9-_]+$",
            message = "Invalid input: Custom alias must contain only letters, numbers, hyphens, or underscores"
    )
    private String customAlias; // This is optional, according to the openapi.yaml
}

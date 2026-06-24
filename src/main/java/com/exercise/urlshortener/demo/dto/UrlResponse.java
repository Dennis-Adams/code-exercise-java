package com.exercise.urlshortener.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlResponse {
    private String alias;
    private String fullUrl;
    private String shortUrl;
}

package com.exercise.urlshortener.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShortenUrlResponse {
    private String shortUrl;

    public ShortenUrlResponse(String shortUrl) {
        this.shortUrl = shortUrl;
    }

}
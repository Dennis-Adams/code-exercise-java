package com.exercise.urlshortener.demo.controller;

import com.exercise.urlshortener.demo.dto.ShortenUrlRequest; // 🌟 Import the DTO from its package
import com.exercise.urlshortener.demo.dto.ShortenUrlResponse;
import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.repository.UrlRepository;
import com.exercise.urlshortener.demo.util.Base62Encoder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlRepository urlRepository;

    // Injecting the repository via constructor
    public UrlController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {

        // check if a custom alias is provided and if it's already taken
        if (request.getCustomAlias() != null && !request.getCustomAlias().trim().isEmpty()) {
            if (urlRepository.findByAlias(request.getCustomAlias()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Alias already taken");
            }
        }

        // save the entity to generate the unique auto-incrementing id
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl(request.getFullUrl());
        entity = urlRepository.save(entity);

        // determine the short code
        String shortCode = request.getCustomAlias();
        if (shortCode == null || shortCode.trim().isEmpty()) {
            // no custom alias provided, we can generate one
            shortCode = Base62Encoder.encode(entity.getId());
        }

        // update the entity with the final short code and save
        entity.setAlias(shortCode);
        urlRepository.save(entity);

        // Construct full short URL to match OpenAPI example (http://localhost:8080/{alias})
        String fullShortUrl = "http://localhost:8080/" + shortCode;

        // Return 201 Created with the formatted JSON response
        ShortenUrlResponse response = new ShortenUrlResponse(fullShortUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<?> shortenUrl(@Valid @PathVariable String alias) {
        var optionalUrl = urlRepository.findByAlias(alias);
        if (optionalUrl.isPresent()) {
            String originalUrl = optionalUrl.get().getFullUrl();
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alias not found");
    }
}
package com.exercise.urlshortener.demo.controller;

import com.exercise.urlshortener.demo.dto.ShortenUrlRequest; // 🌟 Import the DTO from its package
import com.exercise.urlshortener.demo.dto.ShortenUrlResponse;
import com.exercise.urlshortener.demo.dto.UrlResponse;
import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class UrlController {

    public static final String ALIAS_NOT_FOUND = "Alias not found";
    private final UrlService urlService;

    // Injecting the repository via constructor
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        try {
            UrlEntity savedUrl = urlService.shortenUrl(request.getFullUrl(), request.getCustomAlias());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenUrlResponse(savedUrl.getShortUrl()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{alias}")
    public ResponseEntity<?> shortenUrl(@Valid @PathVariable String alias) {

        Optional<String> originalUrl = urlService.getOriginalUrl(alias);

        if (originalUrl.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl.get())).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ALIAS_NOT_FOUND);
    }

    @DeleteMapping("/{alias}")
    public ResponseEntity<?> deleteUrl(@PathVariable String alias) {
        boolean deleted = urlService.deleteByAlias(alias);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ALIAS_NOT_FOUND);
    }

    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }
}
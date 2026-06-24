package com.exercise.urlshortener.demo.controller;

import com.exercise.urlshortener.demo.dto.ShortenUrlRequest; // 🌟 Import the DTO from its package
import com.exercise.urlshortener.demo.dto.ShortenUrlResponse;
import com.exercise.urlshortener.demo.dto.UrlResponse;
import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.exception.AliasNotFoundException;
import com.exercise.urlshortener.demo.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.base-url:http://localhost:8080/}")
    private String baseUrl;

    // Injecting the repository via constructor
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        UrlEntity savedUrl = urlService.shortenUrl(request.getFullUrl(), request.getCustomAlias());
        String finalShortUrl = baseUrl + "/" + savedUrl.getAlias();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenUrlResponse(finalShortUrl));
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirectUrl(@Valid @PathVariable String alias) {

        Optional<String> originalUrl = urlService.getOriginalUrl(alias);

        if (originalUrl.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl.get())).build();
        }

        throw new AliasNotFoundException(ALIAS_NOT_FOUND);
    }

    @DeleteMapping("/{alias}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String alias) {
        boolean deleted = urlService.deleteByAlias(alias);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        throw new AliasNotFoundException(ALIAS_NOT_FOUND);
    }

    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }
}
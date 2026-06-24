package com.exercise.urlshortener.demo.service;

import com.exercise.urlshortener.demo.dto.UrlResponse;
import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.repository.UrlRepository;
import com.exercise.urlshortener.demo.util.Base62Encoder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int MAX_RETRIES = 10;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }


    @Transactional
    public UrlEntity shortenUrl(String originalUrl, String customAlias) {
        String finalAlias;

        // Check if the user provided a custom alias
        if (customAlias != null && !customAlias.trim().isEmpty()) {
            // is customer alias already taken?
            if (urlRepository.findByAlias(customAlias).isPresent()) {
                throw new IllegalArgumentException("Alias already taken");
            }
            finalAlias = customAlias;

        } else {
            finalAlias = generateRandomAlias();
        }

        // 3. Create the new entity, set the values, and save to the database
        UrlEntity newUrl = new UrlEntity();
        newUrl.setFullUrl(originalUrl);
        newUrl.setAlias(finalAlias);

        return urlRepository.save(newUrl);
    }

    public Optional<String> getOriginalUrl(@Valid String alias) {
        return urlRepository.findByAlias(alias).map(UrlEntity::getFullUrl);
    }

    @Transactional
    public boolean deleteByAlias(String alias) {
        // Look up the alias in the database
        var optionalUrl = urlRepository.findByAlias(alias);

        // If it exists, delete it
        if (optionalUrl.isPresent()) {
            urlRepository.delete(optionalUrl.get());
            return true;
        }
        return false;
    }

    public List<UrlResponse> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(entity -> new UrlResponse(
                        entity.getAlias(),
                        entity.getFullUrl(),
                        entity.getShortUrl()))
                .toList();
    }

    private String generateRandomAlias() {
        String alias = "";
        boolean isUnique = false;
        int attempts = 0;

        // Loop until we find an alias that doesn't already exist in the database
        while (!isUnique && attempts < MAX_RETRIES) {
            // Generate a random positive long number
            // (Using bitwise AND with Long.MAX_VALUE ensures the number is always positive)
            long randomNumber = secureRandom.nextLong() & Long.MAX_VALUE;

            // Pass it to your existing Base62Encoder
            alias = Base62Encoder.encode(randomNumber);

            //  Keep the alias reasonably short (e.g., taking the first 7 characters)
            if (alias.length() > 7) {
                alias = alias.substring(0, 7);
            }

            // Check for collisions in the database
            if (!urlRepository.existsByAlias(alias)) {
                isUnique = true;
            } else {
                attempts++;
            }
        }

        if (!isUnique) {
            throw new IllegalStateException("Failed to generate a unique short alias after " + MAX_RETRIES + " attempts");
        }

        return alias;
    }
}

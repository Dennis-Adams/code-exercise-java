package com.exercise.urlshortener.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortenUrlRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void validUrlShouldPassValidation() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl("https://localhost"); // Testing localhost fix
        request.setCustomAlias(""); // Testing empty string fix

        // When
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Expected no validation errors for a valid URL and empty alias");
    }

    @Test
    void invalidUrlMissingProtocolShouldFailValidation() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl("www.google.com"); // Missing http:// or https://

        // When
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
    }

    @Test
    void invalidCustomAliasWithSpacesShouldFailValidation() {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setFullUrl("https://google.com");
        request.setCustomAlias("my alias"); // Spaces are not allowed

        // When
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
    }

}

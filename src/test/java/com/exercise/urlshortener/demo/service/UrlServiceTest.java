package com.exercise.urlshortener.demo.service;

import com.exercise.urlshortener.demo.dto.UrlResponse;
import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shortenUrlWithValidCustomAliasSavesAndReturnsUrl() {
        // Given
        String originalUrl = "https://example.com";
        String customAlias = "my-custom-alias";
        when(urlRepository.findByAlias(customAlias)).thenReturn(Optional.empty());
        when(urlRepository.save(any(UrlEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UrlEntity result = urlService.shortenUrl(originalUrl, customAlias);

        // Then
        assertEquals(customAlias, result.getAlias());
        assertEquals(originalUrl, result.getFullUrl());
        verify(urlRepository).save(any(UrlEntity.class));
    }

    @Test
    void shortenUrlWithTakenCustomAliasThrowsIllegalArgumentException() {
        // Given
        String originalUrl = "https://example.com";
        String customAlias = "taken-alias";
        when(urlRepository.findByAlias(customAlias)).thenReturn(Optional.of(new UrlEntity()));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> urlService.shortenUrl(originalUrl, customAlias));
        assertEquals("Alias already taken", exception.getMessage());
        verify(urlRepository, never()).save(any(UrlEntity.class));
    }

    @Test
    void shortenUrlWithBlankAliasGeneratesRandomAlias() {
        // Given
        String originalUrl = "https://example.com";
        // Simulate that the randomly generated alias is not taken in the DB
        when(urlRepository.existsByAlias(anyString())).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UrlEntity result = urlService.shortenUrl(originalUrl, "");

        // Then
        assertNotNull(result.getAlias());
        assertFalse(result.getAlias().isEmpty());
        assertEquals(originalUrl, result.getFullUrl());
        verify(urlRepository).save(any(UrlEntity.class));
    }

    @Test
    void getOriginalUrlReturnsUrlWhenFound() {
        // Given
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl("https://example.com");
        when(urlRepository.findByAlias("my-alias")).thenReturn(Optional.of(entity));

        // When
        Optional<String> result = urlService.getOriginalUrl("my-alias");

        // Then
        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get());
    }

    @Test
    void getOriginalUrlReturnsEmptyWhenNotFound() {
        // Given
        when(urlRepository.findByAlias("unknown-alias")).thenReturn(Optional.empty());

        // When
        Optional<String> result = urlService.getOriginalUrl("unknown-alias");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void deleteByAliasReturnsTrueWhenDeleted() {
        // Given
        UrlEntity entity = new UrlEntity();
        when(urlRepository.findByAlias("my-alias")).thenReturn(Optional.of(entity));

        // When
        boolean result = urlService.deleteByAlias("my-alias");

        // Then
        assertTrue(result);
        verify(urlRepository).delete(entity); // Verifies the delete method was actually called
    }

    @Test
    void getAllUrlsReturnsMappedDtoList() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080/");

        // Given
        UrlEntity entity = new UrlEntity();
        entity.setAlias("alias1");
        entity.setFullUrl("https://test.com");
        when(urlRepository.findAll()).thenReturn(List.of(entity));

        // When
        List<UrlResponse> results = urlService.getAllUrls();

        // Then
        assertEquals(1, results.size());
        assertEquals("alias1", results.get(0).getAlias());
        assertEquals("http://localhost:8080/alias1", results.get(0).getShortUrl());
    }


    @Test
    void shortenUrl_withBlankAlias_retriesOnCollisionAndSucceeds() {
        // Given
        String originalUrl = "https://example.com";

        // Mockito magic: Return TRUE for the first check (collision!), then FALSE for the second check
        when(urlRepository.existsByAlias(anyString())).thenReturn(true, false);
        when(urlRepository.save(any(UrlEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UrlEntity result = urlService.shortenUrl(originalUrl, "");

        // Then
        assertNotNull(result.getAlias());

        // Verify that the database was queried exactly TWICE because of the retry!
        verify(urlRepository, times(2)).existsByAlias(anyString());
        verify(urlRepository).save(any(UrlEntity.class));
    }

    @Test
    void shortenUrlWithBlankAliasThrowsExceptionAfterMaxRetries() {
        // Given
        String originalUrl = "https://example.com";

        // Simulate a worst-case scenario where every single generated alias is already taken
        when(urlRepository.existsByAlias(anyString())).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> urlService.shortenUrl(originalUrl, ""));

        assertTrue(exception.getMessage().contains("Failed to generate a unique short alias after 10 attempts"));

        // Verify that our loop retried exactly 5 times before giving up
        verify(urlRepository, times(10)).existsByAlias(anyString());

        // Verify we never attempted to save a duplicate to the database
        verify(urlRepository, never()).save(any(UrlEntity.class));
    }
}
package com.exercise.urlshortener.demo;

import com.exercise.urlshortener.demo.entity.UrlEntity;
import com.exercise.urlshortener.demo.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;


    @BeforeEach
    void setUp() {
        urlRepository.deleteAll();  // make sure the database is empty before each test
    }


    @Test
    void shouldReturnBadRequestWhenBodyIsCompletelyEmpty() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUrlFieldIsMissing() throws Exception {
        // Sending an empty JSON object "{}" to trigger the @Valid annotation
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnCreatedWithShortUrlWhenValidRequest() throws Exception {
        String validPayload = "{\"fullUrl\": \"https://example.com/very/long/url\"}";

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isCreated()) // Expects 201 Created
                .andExpect(jsonPath("$.shortUrl").exists()); // Expects JSON response with "shortUrl"
    }

    @Test
    void shouldRedirectToOriginalUrlWhenShortCodeExists() throws Exception {
        // Save a known URL into our test database
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl("https://spring.io");
        entity.setAlias("spring");
        urlRepository.save(entity);

        // 2. Action & Assert: Call the GET endpoint and expect a 302 redirect to the original URL
        mockMvc.perform(get("/spring"))
                .andExpect(status().isFound()) // Expects 302 Found
                .andExpect(header().string("Location", "https://spring.io"));
    }

    @Test
    void shouldReturnNotFoundWithCorrectMessageWhenShortCodeDoesNotExist() throws Exception {
        mockMvc.perform(get("/fake-code-123"))
                .andExpect(status().isNotFound()) // Expects 404 Found
                .andExpect(content().string("Alias not found"));
    }


    @Test
    void shouldDeleteUrlWhenShortCodeExists() throws Exception {
        // Save a known URL into our test database
        UrlEntity entity = new UrlEntity();
        entity.setFullUrl("https://spring.io");
        entity.setAlias("spring-delete");
        urlRepository.save(entity);

        // Call the DELETE endpoint
        mockMvc.perform(delete("/spring-delete"))
                .andExpect(status().isNoContent()); // Expects a 204 No Content status code upon successful deletion

        // 3. Assert: Verify the URL has actually been removed from the database
        boolean exists = urlRepository.findByAlias("spring-delete").isPresent();
        assertFalse(exists, "The entity should have been deleted from the database");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentAlias() throws Exception {
        // Call the DELETE endpoint with a fake alias and expect a 404 with the exact string
        mockMvc.perform(delete("/non-existent-alias-123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Alias not found"));
    }
}

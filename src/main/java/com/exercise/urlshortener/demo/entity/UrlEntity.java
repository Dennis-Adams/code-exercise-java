package com.exercise.urlshortener.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "urls")
public class UrlEntity {

    // Standard Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This auto-incrementing ID will be fed into your Base62Encoder!

    @Column(nullable = false)
    private String fullUrl;

    @Column(unique = true)
    private String shortCode; // This will store either the custom alias or the Base62 encoded ID

    public void setId(Long id) { this.id = id; }

    public void setFullUrl(String fullUrl) { this.fullUrl = fullUrl; }

    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
}
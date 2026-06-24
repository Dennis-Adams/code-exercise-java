package com.exercise.urlshortener.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "urls")
public class UrlEntity {

    // Standard Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This auto-incrementing ID will be fed into your Base62Encoder!

    @Column(nullable = false, length = 2048)
    private String fullUrl;

    @Column(unique = true, nullable = false)
    private String alias; // This will store either the custom alias or a generated alias

    @Transient
    public String getShortUrl() {
        if (this.alias != null) {
            return "http://localhost:8080/" + this.alias;
        }
        return null;
    }
}
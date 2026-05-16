package com.example.urlshortener.link.dto;

import java.time.LocalDateTime;

public class LinkResponse {

    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Long clickCount;

    public LinkResponse() {
    }

    public LinkResponse(Long id, String shortCode, String shortUrl, String originalUrl,
                        LocalDateTime createdAt, LocalDateTime expiresAt, Long clickCount) {
        this.id = id;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.clickCount = clickCount;
    }

    public Long getId() {
        return id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getClickCount() {
        return clickCount;
    }
}

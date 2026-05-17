package com.example.urlshortener.link.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateLinkRequest {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    @NotNull(message = "Expiration time is required")
    @Future(message = "Expiration time must be in the future")
    private LocalDateTime expiresAt;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

package com.example.urlshortener.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithConstructor() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User("admin", "encoded-password", now);

        assertEquals("admin", user.getUsername());
        assertEquals("encoded-password", user.getPasswordHash());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void shouldSetAndGetFields() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setUsername("user1");
        user.setPasswordHash("hash123");
        user.setCreatedAt(now);

        assertEquals("user1", user.getUsername());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals(now, user.getCreatedAt());
    }
}

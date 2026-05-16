package com.example.urlshortener.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleBadRequestException() {
        Map<String, Object> response =
                handler.handleBadRequest(new BadRequestException("Bad request"));

        assertEquals("Bad request", response.get("message"));
        assertEquals(400, response.get("status"));
    }

    @Test
    void shouldHandleNotFoundException() {
        Map<String, Object> response =
                handler.handleNotFound(new NotFoundException("Not found"));

        assertEquals("Not found", response.get("message"));
        assertEquals(404, response.get("status"));
    }

    @Test
    void shouldHandleUnauthorizedException() {
        Map<String, Object> response =
                handler.handleUnauthorized(new UnauthorizedException("Unauthorized"));

        assertEquals("Unauthorized", response.get("message"));
        assertEquals(401, response.get("status"));
    }

    @Test
    void shouldHandleGoneException() {
        Map<String, Object> response =
                handler.handleGone(new GoneException("Link expired"));

        assertEquals("Link expired", response.get("message"));
        assertEquals(410, response.get("status"));
    }
}
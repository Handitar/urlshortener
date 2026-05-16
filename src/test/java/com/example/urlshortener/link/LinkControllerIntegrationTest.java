package com.example.urlshortener.link;

import com.example.urlshortener.auth.JwtService;
import com.example.urlshortener.common.AbstractIntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LinkControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldRejectProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/links"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldCreateLinkWithToken() throws Exception {
        String username = "link_test_user_" + UUID.randomUUID();
        registerUser(username);

        String token = jwtService.generateToken(username);

        String body = """
                {
                  "originalUrl": "https://www.google.com",
                  "expiresAt": "2026-12-31T23:59:59"
                }
                """;

        mockMvc.perform(post("/api/v1/links")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnAllLinksWithToken() throws Exception {
        String username = "link_test_user_" + UUID.randomUUID();
        registerUser(username);

        String token = jwtService.generateToken(username);

        mockMvc.perform(get("/api/v1/links")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void registerUser(String username) throws Exception {
        String body = """
                {
                  "username": "%s",
                  "password": "Password123"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}

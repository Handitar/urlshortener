package com.example.urlshortener.link;

import com.example.urlshortener.auth.JwtService;
import com.example.urlshortener.common.AbstractIntegrationTest;
import com.example.urlshortener.link.dto.LinkResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LinkControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldReturnLinkByIdWithToken() throws Exception {
        String username = "link_test_user_" + UUID.randomUUID();
        registerUser(username);

        String token = jwtService.generateToken(username);

        String createBody = """
            {
              "originalUrl": "https://www.google.com",
              "expiresAt": "2026-12-31T23:59:59"
            }
            """;

        String responseBody = mockMvc.perform(post("/api/v1/links")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LinkResponse created = objectMapper.readValue(responseBody, LinkResponse.class);

        mockMvc.perform(get("/api/v1/links/" + created.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"));
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

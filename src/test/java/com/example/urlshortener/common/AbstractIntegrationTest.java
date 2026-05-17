package com.example.urlshortener.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static final boolean USE_TESTCONTAINERS =
            Boolean.parseBoolean(System.getProperty("useTestcontainers", "false"));

    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("urlshortener")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        if (USE_TESTCONTAINERS) {
            postgres.start();
        }
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        if (USE_TESTCONTAINERS) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
        } else {
            registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/urlshortener");
            registry.add("spring.datasource.username", () -> "postgres");
            registry.add("spring.datasource.password", () -> "postgres");
        }

        registry.add("jwt.secret", () -> "VerySecretKeyVerySecretKeyVerySecretKey123");
        registry.add("jwt.expiration", () -> "3600000");
    }
}
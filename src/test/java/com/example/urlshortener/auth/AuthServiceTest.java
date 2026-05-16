package com.example.urlshortener.auth;

import com.example.urlshortener.auth.dto.AuthResponse;
import com.example.urlshortener.auth.dto.LoginRequest;
import com.example.urlshortener.auth.dto.RegisterRequest;
import com.example.urlshortener.common.exception.BadRequestException;
import com.example.urlshortener.common.exception.UnauthorizedException;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword("Password123");

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Password123");

        User user = new User("admin", "encoded-password", LocalDateTime.now());

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken("admin")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Password123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("Password123");

        User user = new User("admin", "encoded-password", LocalDateTime.now());

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}

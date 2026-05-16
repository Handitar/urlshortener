package com.example.urlshortener.link;

import com.example.urlshortener.common.exception.BadRequestException;
import com.example.urlshortener.common.exception.NotFoundException;
import com.example.urlshortener.link.dto.CreateLinkRequest;
import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LinkServiceTest {

    private LinkRepository linkRepository;
    private UserRepository userRepository;
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        linkRepository = mock(LinkRepository.class);
        userRepository = mock(UserRepository.class);
        linkService = new LinkService(linkRepository, userRepository);
    }

    @Test
    void shouldCreateLinkSuccessfully() {
        User user = createUserWithId(1L, "admin");

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://www.google.com");
        request.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(linkRepository.existsByShortCode(anyString())).thenReturn(false);
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LinkResponse response = linkService.create("admin", request);

        assertNotNull(response);
        assertEquals("https://www.google.com", response.getOriginalUrl());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsInvalid() {
        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("invalid-url");
        request.setExpiresAt(LocalDateTime.now().plusDays(1));

        assertThrows(BadRequestException.class, () -> linkService.create("admin", request));
    }

    @Test
    void shouldReturnAllLinks() {
        User user = createUserWithId(1L, "admin");

        Link link = new Link();
        link.setShortCode("abc123");
        link.setOriginalUrl("https://www.google.com");
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(1));
        link.setClickCount(5L);
        link.setDeleted(false);
        link.setUser(user);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(linkRepository.findAllByUserIdAndDeletedFalse(1L)).thenReturn(List.of(link));

        List<LinkResponse> result = linkService.getAll("admin");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("https://www.google.com", result.get(0).getOriginalUrl());
    }

    @Test
    void shouldDeleteOwnLink() {
        User user = createUserWithId(1L, "admin");

        Link link = new Link();
        link.setDeleted(false);
        link.setUser(user);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        linkService.delete("admin", 1L);

        assertTrue(link.isDeleted());
        verify(linkRepository).save(link);
    }

    @Test
    void shouldThrowExceptionWhenLinkNotFound() {
        User user = createUserWithId(1L, "admin");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(linkRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> linkService.delete("admin", 1L));
    }

    private User createUserWithId(Long id, String username) {
        User user = new User(username, "password", LocalDateTime.now());
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }
    @Test
    void shouldReturnActiveLinks() {
        User user = createUserWithId(1L, "admin");

        Link activeLink = new Link();
        activeLink.setShortCode("active123");
        activeLink.setOriginalUrl("https://www.google.com");
        activeLink.setCreatedAt(LocalDateTime.now());
        activeLink.setExpiresAt(LocalDateTime.now().plusDays(1));
        activeLink.setClickCount(1L);
        activeLink.setDeleted(false);
        activeLink.setUser(user);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(linkRepository.findAllByUserIdAndDeletedFalseAndExpiresAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(activeLink));

        List<LinkResponse> result = linkService.getActive("admin");

        assertEquals(1, result.size());
        assertEquals("https://www.google.com", result.get(0).getOriginalUrl());
    }

    @Test
    void shouldThrowExceptionWhenDeletingForeignLink() {
        User currentUser = createUserWithId(1L, "admin");
        User owner = createUserWithId(2L, "other");

        Link link = new Link();
        link.setUser(owner);
        link.setDeleted(false);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentUser));
        when(linkRepository.findById(1L)).thenReturn(Optional.of(link));

        assertThrows(BadRequestException.class, () -> linkService.delete("admin", 1L));
    }

    @Test
    void shouldThrowExceptionWhenShortCodeNotFound() {
        when(linkRepository.findByShortCodeAndDeletedFalse("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> linkService.resolveOriginalUrl("missing"));
    }

    @Test
    void shouldThrowExceptionWhenLinkExpired() {
        Link link = new Link();
        link.setShortCode("expired123");
        link.setOriginalUrl("https://www.google.com");
        link.setCreatedAt(LocalDateTime.now().minusDays(2));
        link.setExpiresAt(LocalDateTime.now().minusDays(1));
        link.setClickCount(0L);
        link.setDeleted(false);

        when(linkRepository.findByShortCodeAndDeletedFalse("expired123")).thenReturn(Optional.of(link));

        assertThrows(RuntimeException.class, () -> linkService.resolveOriginalUrl("expired123"));
    }
}
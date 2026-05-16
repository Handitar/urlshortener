package com.example.urlshortener.link;

import com.example.urlshortener.common.exception.BadRequestException;
import com.example.urlshortener.common.exception.GoneException;
import com.example.urlshortener.common.exception.NotFoundException;
import com.example.urlshortener.link.dto.CreateLinkRequest;
import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LinkService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public LinkResponse create(String username, CreateLinkRequest request) {
        validateUrl(request.getOriginalUrl());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Link link = new Link();
        link.setShortCode(generateUniqueCode());
        link.setOriginalUrl(request.getOriginalUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(request.getExpiresAt());
        link.setClickCount(0L);
        link.setUser(user);
        link.setDeleted(false);

        Link saved = linkRepository.save(link);
        return map(saved);
    }

    public List<LinkResponse> getAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return linkRepository.findAllByUserIdAndDeletedFalse(user.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    public List<LinkResponse> getActive(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return linkRepository.findAllByUserIdAndDeletedFalseAndExpiresAtAfter(user.getId(), LocalDateTime.now())
                .stream()
                .map(this::map)
                .toList();
    }

    public void delete(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Link not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can delete only your own links");
        }

        link.setDeleted(true);
        linkRepository.save(link);
    }

    @Transactional
    public String resolveOriginalUrl(String shortCode) {
        Link link = linkRepository.findByShortCodeAndDeletedFalse(shortCode)
                .orElseThrow(() -> new NotFoundException("Short link not found"));

        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GoneException("Link expired");
        }

        link.setClickCount(link.getClickCount() + 1);
        return link.getOriginalUrl();
    }

    private void validateUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new BadRequestException("Invalid URL");
            }
        } catch (Exception ex) {
            throw new BadRequestException("Invalid URL");
        }
    }

    private String generateUniqueCode() {
        Random random = new Random();
        String code;

        do {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                builder.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            code = builder.toString();
        } while (linkRepository.existsByShortCode(code));

        return code;
    }

    private LinkResponse map(Link link) {
        return new LinkResponse(
                link.getId(),
                link.getShortCode(),
                "http://localhost:8080/r/" + link.getShortCode(),
                link.getOriginalUrl(),
                link.getCreatedAt(),
                link.getExpiresAt(),
                link.getClickCount()
        );
    }
}

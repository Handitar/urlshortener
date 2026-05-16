package com.example.urlshortener.link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
    boolean existsByShortCode(String shortCode);
    Optional<Link> findByShortCodeAndDeletedFalse(String shortCode);
    List<Link> findAllByUserIdAndDeletedFalse(Long userId);
    List<Link> findAllByUserIdAndDeletedFalseAndExpiresAtAfter(Long userId, LocalDateTime now);
}

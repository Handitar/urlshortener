package com.example.urlshortener.link;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LinkRepository extends JpaRepository<Link, Long> {

    boolean existsByShortCode(String shortCode);

    Optional<Link> findByShortCodeAndDeletedFalse(String shortCode);

    Optional<Link> findByIdAndDeletedFalse(Long id);

    List<Link> findAllByUserIdAndDeletedFalse(Long userId);

    List<Link> findAllByUserIdAndDeletedFalseAndExpiresAtAfter(Long userId, LocalDateTime now);

    @Modifying
    @Query("""
        update Link l
        set l.clickCount = l.clickCount + 1
        where l.id = :id
    """)
    void incrementClickCount(@Param("id") Long id);
}

package com.urlshortener.backend.repository;
import com.urlshortener.backend.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    Optional<Url> findByCustomAlias(String customAlias);
    boolean existsByShortCode(String shortCode);
    boolean existsByCustomAlias(String customAlias);
    Page<Url> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT u FROM Url u WHERE u.shortCode = :alias OR u.customAlias = :alias")
    Optional<Url> findByAlias(@Param("alias") String alias);
    
    long countByUserId(Long userId);
}

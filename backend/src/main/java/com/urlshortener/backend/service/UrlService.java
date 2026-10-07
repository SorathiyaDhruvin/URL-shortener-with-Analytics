package com.urlshortener.backend.service;
import com.urlshortener.backend.dto.UrlDto;
import com.urlshortener.backend.entity.Url;
import com.urlshortener.backend.entity.UrlStatus;
import com.urlshortener.backend.entity.User;
import com.urlshortener.backend.exception.CustomException;
import com.urlshortener.backend.repository.ClickEventRepository;
import com.urlshortener.backend.repository.UrlRepository;
import com.urlshortener.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service @RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final ClickEventRepository clickEventRepository;
    private final ShortCodeService shortCodeService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    @Transactional
    public UrlDto.Response createUrl(Long userId, UrlDto.CreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        
        String customAlias = request.getCustomAlias();
        if (customAlias != null && !customAlias.trim().isEmpty()) {
            if (urlRepository.existsByCustomAlias(customAlias) || urlRepository.existsByShortCode(customAlias)) {
                throw new CustomException("Custom alias already exists", "ALIAS_EXISTS");
            }
        } else {
            customAlias = null;
        }
        
        String shortCode;
        do {
            shortCode = shortCodeService.generateShortCode(6);
        } while (urlRepository.existsByShortCode(shortCode) || urlRepository.existsByCustomAlias(shortCode));
        
        Url url = Url.builder()
            .user(user)
            .originalUrl(request.getOriginalUrl())
            .shortCode(shortCode)
            .customAlias(customAlias)
            .description(request.getDescription())
            .status(UrlStatus.ACTIVE)
            .expiresAt(request.getExpiresAt())
            .build();
            
        url = urlRepository.save(url);
        cacheUrl(url);
        return mapToDto(url);
    }
    
    public Page<UrlDto.Response> getUserUrls(Long userId, Pageable pageable) {
        return urlRepository.findByUserId(userId, pageable).map(this::mapToDto);
    }
    
    @Transactional
    public UrlDto.Response updateUrl(Long userId, Long urlId, UrlDto.UpdateRequest request) {
        Url url = urlRepository.findById(urlId).orElseThrow(() -> new CustomException("URL not found", "NOT_FOUND"));
        if (!url.getUser().getId().equals(userId)) throw new CustomException("Forbidden", "FORBIDDEN");
        
        if (request.getDescription() != null) url.setDescription(request.getDescription());
        if (request.getExpiresAt() != null) url.setExpiresAt(request.getExpiresAt());
        if (request.getStatus() != null) url.setStatus(request.getStatus());
        
        url = urlRepository.save(url);
        cacheUrl(url);
        return mapToDto(url);
    }
    
    @Transactional
    public void deleteUrl(Long userId, Long urlId) {
        Url url = urlRepository.findById(urlId).orElseThrow(() -> new CustomException("URL not found", "NOT_FOUND"));
        if (!url.getUser().getId().equals(userId)) throw new CustomException("Forbidden", "FORBIDDEN");
        
        redisTemplate.delete("url:" + url.getShortCode());
        if (url.getCustomAlias() != null) redisTemplate.delete("url:" + url.getCustomAlias());
        
        urlRepository.delete(url);
    }
    
    public String resolveUrl(String alias) {
        String cacheKey = "url:" + alias;
        String cachedUrl = (String) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedUrl != null) {
            if (cachedUrl.equals("NOT_FOUND") || cachedUrl.equals("DISABLED") || cachedUrl.equals("EXPIRED")) {
                throw new CustomException("URL not available", cachedUrl);
            }
            return cachedUrl;
        }
        
        Optional<Url> urlOpt = urlRepository.findByAlias(alias);
        if (urlOpt.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, "NOT_FOUND", 10, TimeUnit.MINUTES);
            throw new CustomException("URL not found", "NOT_FOUND");
        }
        
        Url url = urlOpt.get();
        if (url.getStatus() == UrlStatus.DISABLED) {
            throw new CustomException("URL disabled", "DISABLED");
        }
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            url.setStatus(UrlStatus.EXPIRED);
            urlRepository.save(url);
            throw new CustomException("URL expired", "EXPIRED");
        }
        
        redisTemplate.opsForValue().set(cacheKey, url.getOriginalUrl(), 1, TimeUnit.DAYS);
        return url.getOriginalUrl();
    }
    
    public Url getUrlEntityByAlias(String alias) {
        return urlRepository.findByAlias(alias).orElseThrow(() -> new CustomException("URL not found", "NOT_FOUND"));
    }
    
    private void cacheUrl(Url url) {
        if (url.getStatus() == UrlStatus.ACTIVE && (url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now()))) {
            redisTemplate.opsForValue().set("url:" + url.getShortCode(), url.getOriginalUrl(), 1, TimeUnit.DAYS);
            if (url.getCustomAlias() != null) {
                redisTemplate.opsForValue().set("url:" + url.getCustomAlias(), url.getOriginalUrl(), 1, TimeUnit.DAYS);
            }
        } else {
            redisTemplate.delete("url:" + url.getShortCode());
            if (url.getCustomAlias() != null) redisTemplate.delete("url:" + url.getCustomAlias());
        }
    }
    
    private UrlDto.Response mapToDto(Url url) {
        UrlDto.Response response = new UrlDto.Response();
        response.setId(url.getId());
        response.setOriginalUrl(url.getOriginalUrl());
        response.setShortCode(url.getShortCode());
        response.setCustomAlias(url.getCustomAlias());
        response.setShortUrl("http://localhost:8080/" + (url.getCustomAlias() != null ? url.getCustomAlias() : url.getShortCode()));
        response.setDescription(url.getDescription());
        response.setStatus(url.getStatus());
        response.setExpiresAt(url.getExpiresAt());
        response.setCreatedAt(url.getCreatedAt());
        response.setClicks(clickEventRepository.countByUrlId(url.getId()));
        return response;
    }
}

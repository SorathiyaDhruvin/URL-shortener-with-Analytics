package com.urlshortener.backend.dto;
import com.urlshortener.backend.entity.UrlStatus;
import lombok.Data;
import java.time.LocalDateTime;

public class UrlDto {
    @Data
    public static class CreateRequest {
        private String originalUrl;
        private String customAlias;
        private String description;
        private LocalDateTime expiresAt;
    }
    @Data
    public static class UpdateRequest {
        private String description;
        private LocalDateTime expiresAt;
        private UrlStatus status;
    }
    @Data
    public static class Response {
        private Long id;
        private String originalUrl;
        private String shortCode;
        private String customAlias;
        private String shortUrl;
        private String description;
        private UrlStatus status;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private long clicks;
    }
}

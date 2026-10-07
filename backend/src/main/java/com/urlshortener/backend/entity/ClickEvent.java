package com.urlshortener.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "click_events", indexes = {
    @Index(name = "idx_url_id", columnList = "url_id"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClickEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;
    
    @Column(nullable = false)
    private LocalDateTime clickedAt;
    
    private String ipHash;
    private String userAgent;
    private String browser;
    private String operatingSystem;
    private String deviceType;
    private String referrer;
    private String country;
    private String region;
    private String city;
    
    @PrePersist
    protected void onCreate() { clickedAt = LocalDateTime.now(); }
}

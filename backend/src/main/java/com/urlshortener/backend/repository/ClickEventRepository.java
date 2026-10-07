package com.urlshortener.backend.repository;
import com.urlshortener.backend.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByUrlId(Long urlId);
    
    @Query("SELECT COUNT(DISTINCT c.ipHash) FROM ClickEvent c WHERE c.url.id = :urlId")
    long countUniqueVisitorsByUrlId(@Param("urlId") Long urlId);
    
    @Query("SELECT COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId AND c.clickedAt >= :startDate")
    long countByUrlIdAndClickedAtAfter(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT c.browser, COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId GROUP BY c.browser")
    List<Object[]> countClicksByBrowser(@Param("urlId") Long urlId);

    @Query("SELECT c.operatingSystem, COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId GROUP BY c.operatingSystem")
    List<Object[]> countClicksByOs(@Param("urlId") Long urlId);
    
    @Query("SELECT c.deviceType, COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId GROUP BY c.deviceType")
    List<Object[]> countClicksByDeviceType(@Param("urlId") Long urlId);
    
    @Query("SELECT c.country, COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId GROUP BY c.country")
    List<Object[]> countClicksByCountry(@Param("urlId") Long urlId);
    
    @Query("SELECT c.referrer, COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId GROUP BY c.referrer")
    List<Object[]> countClicksByReferrer(@Param("urlId") Long urlId);
    
    @Query("SELECT CAST(c.clickedAt AS date), COUNT(c) FROM ClickEvent c WHERE c.url.id = :urlId AND c.clickedAt >= :startDate GROUP BY CAST(c.clickedAt AS date)")
    List<Object[]> countClicksByDate(@Param("urlId") Long urlId, @Param("startDate") LocalDateTime startDate);
}

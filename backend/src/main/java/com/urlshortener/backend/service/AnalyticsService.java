package com.urlshortener.backend.service;
import com.urlshortener.backend.dto.AnalyticsDto;
import com.urlshortener.backend.entity.ClickEvent;
import com.urlshortener.backend.entity.Url;
import com.urlshortener.backend.repository.ClickEventRepository;
import com.urlshortener.backend.repository.UrlRepository;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class AnalyticsService {
    private final ClickEventRepository clickEventRepository;
    private final UrlRepository urlRepository;
    
    public void recordClick(Url url, String ipHash, String userAgentString, String referrer) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        ClickEvent event = ClickEvent.builder()
            .url(url)
            .ipHash(ipHash)
            .userAgent(userAgentString)
            .browser(userAgent.getBrowser().getGroup().getName())
            .operatingSystem(userAgent.getOperatingSystem().getGroup().getName())
            .deviceType(userAgent.getOperatingSystem().getDeviceType().getName())
            .referrer(referrer != null ? referrer : "Direct")
            .country("Unknown")
            .build();
        clickEventRepository.save(event);
    }
    
    public AnalyticsDto.Detailed getUrlAnalytics(Long urlId, Long userId) {
        Url url = urlRepository.findById(urlId).orElseThrow();
        if (!url.getUser().getId().equals(userId)) throw new RuntimeException("Forbidden");
        
        AnalyticsDto.Overview overview = new AnalyticsDto.Overview();
        overview.setTotalClicks(clickEventRepository.countByUrlId(urlId));
        overview.setUniqueVisitors(clickEventRepository.countUniqueVisitorsByUrlId(urlId));
        overview.setTodayClicks(clickEventRepository.countByUrlIdAndClickedAtAfter(urlId, LocalDateTime.now().toLocalDate().atStartOfDay()));
        overview.setLast7DaysClicks(clickEventRepository.countByUrlIdAndClickedAtAfter(urlId, LocalDateTime.now().minusDays(7)));
        
        AnalyticsDto.Detailed detailed = new AnalyticsDto.Detailed();
        detailed.setOverview(overview);
        
        detailed.setClicksOverTime(listToMap(clickEventRepository.countClicksByDate(urlId, LocalDateTime.now().minusDays(30))));
        detailed.setBrowsers(listToMap(clickEventRepository.countClicksByBrowser(urlId)));
        detailed.setOperatingSystems(listToMap(clickEventRepository.countClicksByOs(urlId)));
        detailed.setDeviceTypes(listToMap(clickEventRepository.countClicksByDeviceType(urlId)));
        detailed.setCountries(listToMap(clickEventRepository.countClicksByCountry(urlId)));
        detailed.setReferrers(listToMap(clickEventRepository.countClicksByReferrer(urlId)));
        
        return detailed;
    }
    
    private Map<String, Long> listToMap(List<Object[]> list) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : list) {
            map.put(row[0] != null ? row[0].toString() : "Unknown", ((Number) row[1]).longValue());
        }
        return map;
    }
}

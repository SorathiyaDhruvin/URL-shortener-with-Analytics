package com.urlshortener.backend.controller;
import com.urlshortener.backend.entity.Url;
import com.urlshortener.backend.service.AnalyticsService;
import com.urlshortener.backend.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class RedirectController {
    private final UrlService urlService;
    private final AnalyticsService analyticsService;
    
    @GetMapping("/{alias}")
    public void redirect(@PathVariable String alias, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String originalUrl = urlService.resolveUrl(alias);
            Url url = urlService.getUrlEntityByAlias(alias);
            
            String ipHash = String.valueOf(request.getRemoteAddr().hashCode());
            analyticsService.recordClick(url, ipHash, request.getHeader("User-Agent"), request.getHeader("Referer"));
            
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", originalUrl);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Link not found or inactive.");
        }
    }
}

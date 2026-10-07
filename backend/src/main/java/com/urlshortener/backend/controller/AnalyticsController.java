package com.urlshortener.backend.controller;
import com.urlshortener.backend.dto.AnalyticsDto;
import com.urlshortener.backend.dto.ApiResponse;
import com.urlshortener.backend.security.UserDetailsImpl;
import com.urlshortener.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls/{id}/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    
    @GetMapping
    public ApiResponse<AnalyticsDto.Detailed> getAnalytics(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        return ApiResponse.success(analyticsService.getUrlAnalytics(id, userDetails.getId()), "Analytics fetched");
    }
}

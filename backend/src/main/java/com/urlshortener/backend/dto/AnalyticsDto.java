package com.urlshortener.backend.dto;
import lombok.Data;
import java.util.Map;

public class AnalyticsDto {
    @Data
    public static class Overview {
        private long totalClicks;
        private long uniqueVisitors;
        private long todayClicks;
        private long last7DaysClicks;
    }
    @Data
    public static class Detailed {
        private Overview overview;
        private Map<String, Long> clicksOverTime;
        private Map<String, Long> browsers;
        private Map<String, Long> operatingSystems;
        private Map<String, Long> deviceTypes;
        private Map<String, Long> countries;
        private Map<String, Long> referrers;
    }
}

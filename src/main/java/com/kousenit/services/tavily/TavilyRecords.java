package com.kousenit.services.tavily;

import java.util.List;

public class TavilyRecords {
    public record SearchQuery(
            String query,
            String topic,
            String searchDepth,  // enum: basic, advanced
            int maxResults,
            String timeRange,  // enum: day, week, month, year, d, w, m, y
            int days,
            boolean includeAnswer,
            boolean includeRawContent,
            boolean includeImages,
            boolean includeImageDescriptions,
            List<String> includeDomains,
            List<String> excludeDomains
    ) {
    }

    public record SearchResponse(
            String query,
            String answer,
            List<Image> images,
            List<Result> results,
            double responseTime
    ) {
        public record Image(String url, String description) {}

        public record Result(
                String title,
                String url,
                String content,
                double score,
                String rawContent
        ) {}
    }

    public record ExtractRequest(
        String urls, // single url to extract
        boolean includeImages,
        String extractDepth // enum: basic, advanced
    ) {}

    public record ExtractResponse(
        List<Result> results,
        List<FailedResult> failedResults,
        double responseTime
    ) {
        public record Result(
                String url,
                String rawContent,
                List<String> images
        ) {}

        public record FailedResult(String url, String error) { }
    }
}

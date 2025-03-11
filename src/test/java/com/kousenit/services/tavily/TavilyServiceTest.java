package com.kousenit.services.tavily;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kousenit.services.tavily.TavilyRecords.*;
import static org.junit.jupiter.api.Assertions.*;

class TavilyServiceTest {
    private final TavilyService service = new TavilyService();

    @Test
    void search() {
        SearchResponse searchResponse = service.search(
                new SearchQuery(
                        "Tell me about the Kendrick Lamar and Drake feud",
                        "general",
                        "basic",
                        5,
                        "day",
                        3,
                        true,
                        false,
                        false,
                        false,
                        List.of(),
                        List.of()
                ));
        assertNotNull(searchResponse);
        searchResponse.results().forEach(result ->
                System.out.println(result.title() + ": " + result.url() + " (" + result.score() + ")"));
        System.out.println("Answer: " + searchResponse.answer());
        System.out.println("Response time: " + searchResponse.responseTime());
    }

    @Test
    void extract() {
        ExtractResponse extractResponse = service.extract(
                new ExtractRequest(
                        "https://en.wikipedia.org/wiki/Drake%E2%80%93Kendrick_Lamar_feud",
                        true,
                        "basic"
                ));
        assertNotNull(extractResponse);
        extractResponse.results().forEach(result -> {
                    System.out.println(result.url() + ": " + result.images() +
                        " (" + result.rawContent().length() + " chars)");
                    System.out.println(result.rawContent());
                });
        extractResponse.failedResults().forEach(failedResult ->
                System.out.println(failedResult.url() + ": " + failedResult.error()));
        System.out.println("Response time: " + extractResponse.responseTime());
    }

    @Test
    void testSearch() {
        String result = service.search("""
                What are the best tools for
                real-time language translation with AI?
                """);
        System.out.println(result);
    }
}
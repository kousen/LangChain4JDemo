package com.kousenit.mcp;

import com.kousenit.AiModels;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
@EnabledIfEnvironmentVariable(named = "TAVILY_API_KEY", matches = ".+")
class TavilyMcpServiceTest {
    private static TavilyMcpService service;
    private static final String TAVILY_API_KEY = System.getenv("TAVILY_API_KEY");

    @BeforeAll
    static void setUpService() throws Exception {
        service = new TavilyMcpService(AiModels.GPT_4_1_MINI, TAVILY_API_KEY);
        
        // Give the MCP server time to start
        Thread.sleep(2000);
    }

    @AfterAll
    static void tearDownService() throws Exception {
        if (service != null) {
            service.close();
        }
    }

    @Test
    void search() {
        String response = service.search("latest developments in AI");
        System.out.println("Search response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .hasSizeGreaterThan(10)
            .containsIgnoringCase("AI");
    }

    @Test
    void query() {
        String response = service.query("Find information about LangChain4j library");
        System.out.println("General query response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("LangChain4j");
    }

    @Test
    void extractContent() {
        String response = service.extractContent("https://www.wikipedia.org/wiki/Java_(programming_language)");
        System.out.println("Extract response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .hasSizeGreaterThan(100)
            .containsIgnoringCase("Java")
            .containsIgnoringCase("programming language");
    }

    @Test
    void searchWithMaxResults() {
        String response = service.searchWithMaxResults("quantum computing breakthroughs", 3);
        System.out.println("Search with max results response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("quantum");
    }

    @Test
    void searchNews() {
        String response = service.searchNews("artificial intelligence regulations");
        System.out.println("Search news response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .satisfiesAnyOf(
                s -> assertThat(s).containsIgnoringCase("AI"),
                s -> assertThat(s).containsIgnoringCase("artificial intelligence")
            );
    }

    @Test
    void searchAndSummarize() {
        String response = service.searchAndSummarize("renewable energy trends 2024");
        System.out.println("Search and summarize response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .hasSizeGreaterThan(50)
            .satisfiesAnyOf(
                s -> assertThat(s).containsIgnoringCase("renewable"),
                s -> assertThat(s).containsIgnoringCase("energy"),
                s -> assertThat(s).containsIgnoringCase("solar"),
                s -> assertThat(s).containsIgnoringCase("wind")
            );
    }

    @Test
    void searchWithTopic() {
        String response = service.searchWithTopic("machine learning", "healthcare applications");
        System.out.println("Search with topic response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .satisfiesAnyOf(
                s -> assertThat(s).containsIgnoringCase("machine learning"),
                s -> assertThat(s).containsIgnoringCase("ML"),
                s -> assertThat(s).containsIgnoringCase("healthcare"),
                s -> assertThat(s).containsIgnoringCase("medical")
            );
    }

}
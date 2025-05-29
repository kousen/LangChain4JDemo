package com.kousenit.mcp;

import com.kousenit.AiModels;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    void testSearch() {
        String response = service.search("latest developments in AI");
        System.out.println("Search response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .hasSizeGreaterThan(10)
            .containsIgnoringCase("AI");
    }

    @Test
    void testGeneralQuery() {
        String response = service.query("Find information about LangChain4j library");
        System.out.println("General query response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("LangChain4j");
    }

    @Test
    void testExtractContent() {
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
    void testServiceClosesWithoutException() {
        assertDoesNotThrow(() -> service.close());
    }
}
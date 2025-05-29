package com.kousenit.mcp;

import com.kousenit.AiModels;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class EverythingMcpServiceTest {
    private static EverythingMcpService service;

    @BeforeAll
    static void setUpService() throws Exception {
        service = new EverythingMcpService(AiModels.GPT_4_1_MINI);
        
        // Give the MCP server time to establish HTTP connection
        Thread.sleep(2000);
    }

    @AfterAll
    static void tearDownService() throws Exception {
        if (service != null) {
            service.close();
        }
    }

    @Test
    void testAddNumbers() {
        String response = service.addNumbers(5, 12);
        System.out.println("Add numbers response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("17");
    }

    @Test
    void testGetTime() {
        String response = service.getTime();
        System.out.println("Get time response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("time");
    }

    @Test
    void testSampleData() {
        String response = service.sampleData();
        System.out.println("Sample data response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void testListTools() {
        String response = service.listTools();
        System.out.println("List tools response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("tool");
    }

    @Test
    void testServiceClosesWithoutException() {
        assertDoesNotThrow(() -> service.close());
    }
}
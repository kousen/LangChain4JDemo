package com.kousenit.mcp;

import com.kousenit.AiModels;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class SqliteMcpServiceTest {
    private static SqliteMcpService service;
    private static Path tempDbFile;

    @BeforeAll
    static void setUpService() throws Exception {
        tempDbFile = Files.createTempFile("test", ".db");
        service = new SqliteMcpService(AiModels.GPT_4_1_MINI, tempDbFile);
        
        // Give the MCP server time to start
        Thread.sleep(2000);
    }

    @AfterAll
    static void tearDownService() throws Exception {
        if (service != null) {
            service.close();
        }
        if (tempDbFile != null && Files.exists(tempDbFile)) {
            Files.deleteIfExists(tempDbFile);
        }
    }

    @AfterEach
    void cleanUpTables() {
        // Clean up any test tables created during tests
        // SQLite will handle this automatically when we close the service
    }

    @Test
    void testCreateTableAndInsertData() {
        String createResponse = service.createTable("users", 
            "id INTEGER PRIMARY KEY, name TEXT, email TEXT");
        
        assertThat(createResponse)
            .containsIgnoringCase("table")
            .containsIgnoringCase("created");

        String insertResponse = service.insertData("users", 
            "John Doe, john@example.com", 
            "Jane Smith, jane@example.com");
        
        assertThat(insertResponse)
            .containsIgnoringCase("insert");
    }

    @Test
    void testListTables() {
        service.createTable("test_table", "id INTEGER PRIMARY KEY, name TEXT");
        
        String response = service.listTables();
        
        assertThat(response)
            .containsIgnoringCase("test_table");
    }

    @Test
    void testDescribeTable() {
        service.createTable("products", "id INTEGER PRIMARY KEY, name TEXT, price REAL");
        
        String description = service.describeTable("products");
        
        assertThat(description)
            .containsIgnoringCase("id")
            .containsIgnoringCase("name")
            .containsIgnoringCase("price");
    }

    @Test
    void testSelectAllData() {
        service.createTable("items", "id INTEGER PRIMARY KEY, description TEXT");
        service.insertData("items", "Item 1", "Item 2", "Item 3");
        
        String allData = service.selectAll("items");
        
        assertThat(allData)
            .containsIgnoringCase("Item 1")
            .containsIgnoringCase("Item 2")
            .containsIgnoringCase("Item 3");
    }

    @Test
    void testGeneralQuery() {
        String response = service.query(
                "Create a table called employees with columns: id, name, department, salary");
        
        assertThat(response)
            .containsIgnoringCase("employees")
            .containsIgnoringCase("table");
    }

    @Test
    void testServiceClosesWithoutException() {
        assertDoesNotThrow(() -> service.close());
    }
}
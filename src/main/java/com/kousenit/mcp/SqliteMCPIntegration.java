package com.kousenit.mcp;

import com.kousenit.AiModels;

// Simple demo using the SqliteMcpService
public class SqliteMCPIntegration {
    public static void main(String[] args) {
        String openaiApiKey = System.getenv("OPENAI_API_KEY");
        
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY environment variable not set");
            System.exit(1);
        }

        try (SqliteMcpService service = new SqliteMcpService(
                AiModels.GPT_4_1_NANO, "/Users/kennethkousen/test.db")) {
            System.out.println("Testing SQLite MCP service...");

            String response = service.query("Create a simple test table and insert some data");
            System.out.println("RESPONSE: " + response);

        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Failed to connect to SQLite MCP server", e);
        }
    }
}
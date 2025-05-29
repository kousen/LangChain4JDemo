package com.kousenit.mcp;

import com.kousenit.services.Assistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.util.List;

// Example of connecting to GitHub MCP server
public class GitHubMCPIntegration {
    public static void main(String[] args) {
        // Validate environment variables
        String openaiApiKey = System.getenv("OPENAI_API_KEY");
        String githubToken = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN");

        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY environment variable not set");
            System.exit(1);
        }
        
//        if (githubToken == null || githubToken.trim().isEmpty()) {
//            System.err.println("Error: GITHUB_PERSONAL_ACCESS_TOKEN environment variable not set");
//            System.exit(1);
//        }

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(openaiApiKey)
                .modelName("gpt-4o-mini")
                .logRequests(true)
                .logResponses(true)
                .build();

        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("docker", "run", "-i", "--rm", 
                    "-e", "GITHUB_PERSONAL_ACCESS_TOKEN=" + githubToken,
                    "ghcr.io/github/github-mcp-server"))
                .logEvents(true)
                .build();

        try (McpClient mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .build()) {

            // Test the connection and tool listing
            System.out.println("Testing MCP client connection...");

            ToolProvider toolProvider = McpToolProvider.builder()
                    .mcpClients(List.of(mcpClient))
                    .build();

            Assistant bot = AiServices.builder(Assistant.class)
                    .chatModel(model)
                    .toolProvider(toolProvider)
                    .build();

            String response = bot.chat("Summarize the last 3 commits of the LangChain4j GitHub repository");
            System.out.println("RESPONSE: " + response);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Unknown element type: null")) {
                System.err.println("Error: The GitHub MCP server returned malformed tool specifications.");
                System.err.println("This is likely due to an incompatible version or configuration issue.");
                System.err.println("Try updating the GitHub MCP server Docker image or check its documentation.");
                System.err.println("Detailed error: " + e.getMessage());
            } else {
                System.err.println("Configuration error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
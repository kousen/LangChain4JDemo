package com.kousenit.mcp;

import com.kousenit.services.Assistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.util.List;
import java.util.Map;

public class TavilyMcpService implements AutoCloseable {
    private final McpClient mcpClient;
    private final Assistant assistant;

    public TavilyMcpService(ChatModel chatModel, String tavilyApiKey) {
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "tavily-mcp@0.1.2"))
                .environment(Map.of("TAVILY_API_KEY", tavilyApiKey))
                .logEvents(false)
                .build();

        this.mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .build();

        ToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(List.of(mcpClient))
                .build();

        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .toolProvider(toolProvider)
                .build();
    }

    public String search(String query) {
        return assistant.chat("Search for: " + query);
    }

    public String searchWithMaxResults(String query, int maxResults) {
        return assistant.chat(
                "Search for '%s' and return at most %d results".formatted(query, maxResults));
    }

    public String searchNews(String query) {
        return assistant.chat("Search for recent news about: " + query);
    }

    public String searchAndSummarize(String query) {
        return assistant.chat(
                "Search for '%s' and provide a summary of the key findings".formatted(query));
    }

    public String searchWithTopic(String query, String topic) {
        return assistant.chat(
                "Search for '%s' specifically related to %s".formatted(query, topic));
    }

    public String extractContent(String url) {
        return assistant.chat("Extract content from this URL: " + url);
    }

    public String query(String request) {
        return assistant.chat(request);
    }

    @Override
    public void close() throws Exception {
        if (mcpClient != null) {
            mcpClient.close();
        }
    }
}
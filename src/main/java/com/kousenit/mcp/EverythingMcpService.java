package com.kousenit.mcp;

import com.kousenit.services.Assistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.time.Duration;
import java.util.List;

public class EverythingMcpService implements AutoCloseable {
    private final McpClient mcpClient;
    private final Assistant assistant;

    public EverythingMcpService(ChatModel chatModel) {
        this(chatModel, "http://localhost:3001/sse");
    }

    public EverythingMcpService(ChatModel chatModel, String sseUrl) {
        McpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl(sseUrl)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(false)
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

    public String addNumbers(int a, int b) {
        return assistant.chat("Add %d and %d using the provided tool".formatted(a, b));
    }

    public String getTime() {
        return assistant.chat("What is the current time? Use the provided tool.");
    }

    public String sampleData() {
        return assistant.chat("Get some sample data using the available tools");
    }

    public String listTools() {
        return assistant.chat("What tools are available to you?");
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
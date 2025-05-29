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

import java.nio.file.Path;
import java.util.List;

public class SqliteMcpService implements AutoCloseable {
    private final McpClient mcpClient;
    private final Assistant assistant;

    public SqliteMcpService(ChatModel chatModel, String dbPath) {
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("uvx", "mcp-server-sqlite", "--db-path", dbPath))
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

    public SqliteMcpService(ChatModel chatModel, Path dbPath) {
        this(chatModel, dbPath.toString());
    }

    public String query(String request) {
        return assistant.chat(request);
    }

    public String createTable(String tableName, String schema) {
        return assistant.chat(
                "Create a table named %s with schema: %s".formatted(tableName, schema));
    }

    public String insertData(String tableName, String... values) {
        String valueList = String.join(", ", values);
        return assistant.chat("Insert data into %s: %s".formatted(tableName, valueList));
    }

    public String selectAll(String tableName) {
        return assistant.chat("Select all data from table %s".formatted(tableName));
    }

    public String listTables() {
        return assistant.chat("List all tables in the database");
    }

    public String describeTable(String tableName) {
        return assistant.chat("Describe the schema of table %s".formatted(tableName));
    }

    @Override
    public void close() throws Exception {
        if (mcpClient != null) {
            mcpClient.close();
        }
    }
}
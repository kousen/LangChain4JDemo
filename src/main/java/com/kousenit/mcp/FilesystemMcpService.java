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

public class FilesystemMcpService implements AutoCloseable {
    private final McpClient mcpClient;
    private final Assistant assistant;

    public FilesystemMcpService(ChatModel chatModel, String rootPath) {
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("npx", "-y", "@modelcontextprotocol/server-filesystem", rootPath))
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

    public String listFiles(String directory) {
        return assistant.chat("List files in directory: " + directory);
    }

    public String readFile(String filePath) {
        return assistant.chat("Read the contents of file: " + filePath);
    }

    public String writeFile(String filePath, String content) {
        return assistant.chat("Write to file %s with content: %s".formatted(filePath, content));
    }

    public String createDirectory(String dirPath) {
        return assistant.chat("Create directory: " + dirPath);
    }

    public String getFileInfo(String filePath) {
        return assistant.chat("Get information about file: " + filePath);
    }

    public String moveFile(String sourcePath, String destinationPath) {
        return assistant.chat("Move file from %s to %s".formatted(sourcePath, destinationPath));
    }

    public String deleteFile(String filePath) {
        return assistant.chat("Delete file: " + filePath);
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
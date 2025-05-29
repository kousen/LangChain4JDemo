package com.kousenit.mcp;

import com.kousenit.AiModels;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class FilesystemMcpServiceTest {
    private static FilesystemMcpService service;
    private static Path tempDir;

    @BeforeAll
    static void setUpService() throws Exception {
        // Use the Desktop directory which definitely exists and matches your MCP config
        tempDir = Path.of("/Users/kennethkousen/Desktop");
        
        service = new FilesystemMcpService(AiModels.GPT_4_1_MINI, tempDir.toString());
        
        // Give the MCP server time to start
        Thread.sleep(2000);
    }

    @AfterAll
    static void tearDownService() throws Exception {
        if (service != null) {
            service.close();
        }
        if (tempDir != null && Files.exists(tempDir)) {
            try (Stream<Path> fileStream = Files.walk(tempDir)) {
                fileStream.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception e) {
                                // Ignore cleanup errors
                            }
                        });
            }
        }
    }

    @AfterEach
    void cleanUpTestFiles() throws Exception {
        // Clean up only the test files we created
        try {
            Files.deleteIfExists(tempDir.resolve("test.txt"));
            Files.deleteIfExists(tempDir.resolve("sample.md"));
            Files.deleteIfExists(tempDir.resolve("hello.txt"));
            Files.deleteIfExists(tempDir.resolve("info-test.txt"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void testListFiles() throws Exception {
        Files.createFile(tempDir.resolve("test.txt"));
        Files.createFile(tempDir.resolve("sample.md"));
        
        String response = service.listFiles(tempDir.toString());
        System.out.println("List files response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("test.txt");
    }

    @Test
    void testWriteAndReadFile() {
        String testContent = "Hello, MCP Filesystem!";
        String filePath = tempDir.resolve("hello.txt").toString();
        
        String writeResponse = service.writeFile(filePath, testContent);
        System.out.println("Write response: " + writeResponse);
        
        String readResponse = service.readFile(filePath);
        System.out.println("Read response: " + readResponse);
        
        assertThat(writeResponse).isNotNull();
        assertThat(readResponse)
            .isNotNull()
            .isNotEmpty()
            .containsIgnoringCase("Hello");
    }

    @Test
    void testGetFileInfo() throws Exception {
        Path testFile = tempDir.resolve("info-test.txt");
        Files.writeString(testFile, "Test file for info");
        
        String response = service.getFileInfo(testFile.toString());
        System.out.println("File info response: " + response);
        
        assertThat(response)
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void testServiceClosesWithoutException() {
        assertDoesNotThrow(() -> service.close());
    }
}
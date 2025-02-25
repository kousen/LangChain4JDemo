package com.kousenit;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".*")
class DeepSeekJacksonTest {
    private final DeepSeekJackson deepSeek = new DeepSeekJackson();

    @Test
    void chat() {
        deepSeek.setDebug(false);
        JsonNode root = deepSeek.chat("""
                How many r's are in the word "strawberry"?
                """, "deepseek-chat");
        assertNotNull(root);
        JsonNode message = root.at("/choices/0/message");
        assertAll(
                () -> assertTrue(message.at("/content").isValueNode()),
                () -> assertTrue(message.at("/reasoning_content").isMissingNode())
        );
        System.out.println("Content:");
        System.out.println(message.at("/content").asText());
    }

    @Test
    void reasoning() {
        deepSeek.setDebug(false);
        JsonNode root = deepSeek.chat("""
                How many r's are in the word "strawberry"?
                """, "deepseek-reasoner");
        assertNotNull(root);
        JsonNode message = root.at("/choices/0/message");
        assertAll(
                () -> assertTrue(message.at("/content").isValueNode()),
                () -> assertTrue(message.at("/reasoning_content").isValueNode())
        );
        System.out.println("Content:");
        System.out.println(message.at("/content").asText());
        System.out.println("\nReasoning Content:");
        System.out.println(message.at("/reasoning_content").asText());
    }
}
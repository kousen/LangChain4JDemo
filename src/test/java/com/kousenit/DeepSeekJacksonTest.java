package com.kousenit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".*")
class DeepSeekJacksonTest {
    private final DeepSeekJackson deepSeek = new DeepSeekJackson();

    @Test
    void models() {
        JsonNode root = deepSeek.models();
        assertNotNull(root);

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Model(String id, @JsonProperty("owned_by") String ownedBy) {}

        var mapper = new ObjectMapper();
        List<Model> models = mapper.convertValue(
                root.at("/data"), new TypeReference<>() {});
        models.forEach(System.out::println);
    }

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

    @Test
    void fim() {
        deepSeek.setDebug(false);
        String prompt = "def fib(a):";
        String suffix = "    return fib(a-1) + fib(a-2)";
        JsonNode root = deepSeek.fim("deepseek-chat", prompt, suffix);
        assertNotNull(root);
        System.out.println(prompt);
        System.out.println(root.at("/choices/0/text").asText());
        System.out.println(suffix);
    }
}
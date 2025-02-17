package com.kousenit.jsonpath;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeepSeekJsonPathTest {
    @Test
    void testChat() {
        DeepSeekJsonPath deepSeekJsonPath = new DeepSeekJsonPath();
        deepSeekJsonPath.setDebug(false);
        var document = deepSeekJsonPath.chat("""
                        How many r's are in the word "strawberry"?
                        """,
                "deepseek-chat");
        assertNotNull(document);

        System.out.println("Content:");
        String content = document.read("$.choices[0].message.content");
        assertNotNull(content);
        System.out.println(content);
        System.out.println("\nReasoning Content:");
        String reasoning = document.read("$.choices[0].message.reasoning_content");
        assertNull(reasoning);
        System.out.println("No reasoning content found");
    }

    @Test
    void testReasoning() {
        DeepSeekJsonPath deepSeekJsonPath = new DeepSeekJsonPath();
        deepSeekJsonPath.setDebug(true);
        var document = deepSeekJsonPath.chat("""
                        How many r's are in the word "strawberry"?
                        """,
                "deepseek-reasoner");
        assertNotNull(document);

        System.out.println("Content:");
        String content = document.read("$.choices[0].message.content");
        assertNotNull(content);
        System.out.println(content);
        System.out.println("\nReasoning Content:");
        String reasoning = document.read("$.choices[0].message.reasoning_content");
        assertNotNull(reasoning);
        System.out.println(reasoning);
    }
}
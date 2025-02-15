package com.kousenit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeepSeekJsonTest {
    @Test
    void testChat() {
        DeepSeekJson deepSeekJson = new DeepSeekJson();
        deepSeekJson.setDebug(false);
        var document = deepSeekJson.chat("""
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
        DeepSeekJson deepSeekJson = new DeepSeekJson();
        deepSeekJson.setDebug(true);
        var document = deepSeekJson.chat("""
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
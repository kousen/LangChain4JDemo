package com.kousenit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        System.out.println(content != null ? content : "No content found");
        System.out.println("\nReasoning Content:");
        String reasoning = document.read("$.choices[0].message.reasoning_content");
        System.out.println(reasoning != null ? reasoning : "No reasoning content found");
    }
}
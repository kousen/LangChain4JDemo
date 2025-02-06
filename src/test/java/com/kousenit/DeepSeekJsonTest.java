package com.kousenit;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(readJsonPath(document, "$.choices[0].message.content").isPresent());
        System.out.println(readJsonPath(document, "$.choices[0].message.content").get());
        System.out.println("\nReasoning Content:");
        System.out.println(readJsonPath(document, "$.choices[0].message.reasoning_content"));
    }

    private Optional<Object> readJsonPath(Object document, String path) {
        try {
            return Optional.ofNullable(JsonPath.read(document, path));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
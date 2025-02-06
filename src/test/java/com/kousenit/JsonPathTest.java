package com.kousenit;

import com.jayway.jsonpath.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonPathTest {

    private DocumentContext document;

    @BeforeEach
    void setUp() {
        String json = """
                {
                  "id": "e9eae289-cd8e-45b8-b22a-0b4a84a8d80b",
                  "object": "chat.completion",
                  "created": 1738851779,
                  "model": "deepseek-chat",
                  "choices": [
                    {
                      "index": 0,
                      "message": {
                        "role": "assistant",
                        "content": "Hello! How can I assist you today? 😊"
                      },
                      "logprobs": null,
                      "finish_reason": "stop"
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 9,
                    "completion_tokens": 11,
                    "total_tokens": 20,
                    "prompt_tokens_details": {
                      "cached_tokens": 0
                    },
                    "prompt_cache_hit_tokens": 0,
                    "prompt_cache_miss_tokens": 9
                  },
                  "system_fingerprint": "fp_3a5770e1b4"
                }
                """;
        Configuration conf = Configuration.defaultConfiguration()
                .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        document = JsonPath.using(conf).parse(json);
    }

    @Test
    void testJsonPath() {
        Object value = document.read("$.choices[0].message.content");
        System.out.println(value);
    }

    @Test
    void testJsonPathWithMissingElement() {
        String reasoning = document.read(
                "$.choices[0].message.reasoning_content");
        System.out.println(reasoning);
    }
}

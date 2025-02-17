package com.kousenit.jsonpath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathTest {

    private DocumentContext documentWithoutReasoning;
    private DocumentContext documentWithReasoning;

    private final Configuration conf =
            Configuration.builder()
                    .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                    .build();

    private final ObjectMapper mapper = new ObjectMapper();

    String jsonWithoutReasoning = """
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

    String jsonWithReasoning = """
            {
                "id":"6c92ad82-7fdd-4f33-b96f-050e679ba973",
                "object":"chat.completion",
                "created":1739652100,
                "model":"deepseek-reasoner",
                "choices":[
                    {
                        "index":0,
                        "message":{
                            "role":"assistant",
                            "content":"... content ...",
                            "reasoning_content":"... reasoning content ..."
                        },
                        "logprobs":null,
                        "finish_reason":"stop"
                    }
                ],
                "usage":{
                    "prompt_tokens":25,
                    "completion_tokens":405,
                    "total_tokens":430,
                    "prompt_tokens_details":{
                      "cached_tokens":0
                    },
                    "completion_tokens_details":{
                      "reasoning_tokens":286
                    },
                    "prompt_cache_hit_tokens":0,
                    "prompt_cache_miss_tokens":25
                },
                "system_fingerprint":"fp_5417b77867"
            }
            """;

    @BeforeEach
    void setUp() {
        documentWithoutReasoning = JsonPath.using(conf).parse(jsonWithoutReasoning);
        documentWithReasoning = JsonPath.using(conf).parse(jsonWithReasoning);
    }

    @Test
    void content_present_in_both() throws JsonProcessingException {
        Object value = documentWithoutReasoning.read(
                "$.choices[0].message.content");
        assertNotNull(value);
        value = documentWithReasoning.read(
                "$.choices[0].message.content");
        assertNotNull(value);

        JsonNode root = mapper.readTree(jsonWithoutReasoning);
        final JsonNode message = root.at("/choices/0/message");
        assertAll(
                () -> assertTrue(message.at("/content").isValueNode()),
                () -> assertTrue(message.at("/reasoning_content").isMissingNode())
        );

    }

    @Test
    void reasoning_only_if_asked() throws JsonProcessingException {
        String reasoning = documentWithoutReasoning.read(
                "$.choices[0].message.reasoning_content");
        assertNull(reasoning);
        reasoning = documentWithReasoning.read(
                "$.choices[0].message.reasoning_content");
        assertNotNull(reasoning);

        JsonNode root = mapper.readTree(jsonWithReasoning);
        final JsonNode message = root.at("/choices/0/message");
        assertAll(
                () -> assertTrue(message.at("/content").isValueNode()),
                () -> assertTrue(message.at("/reasoning_content").isValueNode())
        );
    }
}

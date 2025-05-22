package com.kousenit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnthropicTest {
    private final ChatModel model = AiModels.CLAUDE_SONNET;

    @Test
    void claude37sonnet_noThinking() {
        ChatModel model = AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName("claude-3-7-sonnet-20250219")
                .logRequests(true)
                .logResponses(true)
                .build();

        ChatResponse response = model.chat(UserMessage.from("""
                Are there an infinite number of
                prime numbers such that n % 4 == 3?
                """));
        System.out.println(response);
    }

    @Test
    void claude37sonnet_withThinking() throws IOException {
        var client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES) // Connection timeout
                .readTimeout(3, TimeUnit.MINUTES)    // Wait for response
                .writeTimeout(1, TimeUnit.MINUTES)   // Upload data
                .build();

        var mapper = new ObjectMapper();

        // From: https://docs.anthropic.com/en/docs/build-with-claude/extended-thinking
        String question = """
                {
                    "model": "claude-3-7-sonnet-20250219",
                    "max_tokens": 20000,
                    "thinking": {
                        "type": "enabled",
                        "budget_tokens": 16000
                    },
                    "messages": [
                        {
                            "role": "user",
                            "content": "Are there an infinite number of prime numbers such that n % 4 == 3?"
                        }
                    ]
                }
                """;

        // Synchronous HTTP request to Anthropic API
        var body = RequestBody.create(question,
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", ApiKeys.ANTHROPIC_API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Response Code: " + response.code());
                return;
            }
            assert response.body() != null;
            String answer = response.body().string();
            assertTrue(answer.toLowerCase().contains("thinking"));

            // Read JSON without mapping to classes
            JsonNode root = mapper.readTree(answer);

            // Path to thinking is $.content[0].thinking
            JsonNode thinking = root.at("/content/0/thinking");
            System.out.println("\nThinking: " + thinking);

            // Path to answer is $.content[1].text
            String output = root.at("/content/1/text").asText();
            System.out.println("\nOutput: " + output);
        }
    }


    @Test
    void test() {
        System.out.println("Testing the new model: (should be claude-3-5-sonnet-20241022");
        ChatResponse response = model.chat(
                UserMessage.from("""
                Shall I compare thee to a summer's day?
                Thou art more lovely and more temperate:
                Rough winds do shake the darling buds of May,
                And summer's lease hath all too short a date:
                Sometime too hot the eye of heaven shines,
                And often is his gold complexion dimm'd;
                And every fair from fair sometime declines,
                By chance or nature's changing course untrimm'd;
                But thy eternal summer shall not fade
                Nor lose possession of that fair thou owest;
                Nor shall Death brag thou wanderest in his shade,
                When in eternal lines to time thou growest:
                So long as men can breathe or eyes can see,
                So long lives this, and this gives life to thee.
    }"""));
        System.out.println(response);
        assertThat(response).isNotNull();
    }
}

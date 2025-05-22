package com.kousenit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "PERPLEXITY_API_KEY", matches = ".*")
public class PerplexityTest {

    private static final List<ChatModel> perplexityModels =
            List.of(AiModels.SONAR, AiModels.SONAR_PRO, AiModels.SONAR_REASONING);

    private static List<ChatModel> perplexityModels() {
        return perplexityModels;
    }

    private void saveAnswerToMarkdownFile(String question, String answer, ChatModel model) {
        String fileName = question.toLowerCase()
                .replace(" ", "_").substring(0, 20);
        fileName = "%s_%s".formatted(fileName, model.getClass().getSimpleName());
        Path path = Paths.get("src/test/resources/%s.md".formatted(fileName));
        try {
            Files.writeString(path,
                    "## Question: \n%s\n\n## Answer: \n%s\n".formatted(
                            question, answer));
            System.out.println("Saved to " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void perplexityVsGPT(ChatModel model) {
        String question = """
                Why should I use the Perplexity API instead of
                GPT-4o, Claude 3.5, Gemini 1.5 Flash, or even
                DeepSeek R1 directly?
                """;
        String answer = model.chat(question);
        saveAnswerToMarkdownFile(question, answer, model);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse(ChatModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        String answer = model.chat(question);
        saveAnswerToMarkdownFile(question, answer, model);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse_chatResponse(ChatModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        ChatResponse response = model.chat(ChatRequest.builder().messages(UserMessage.from(question)).build());
        System.out.println(response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse_userMessage(ChatModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        ChatResponse response = model.chat(UserMessage.from(question));
        System.out.println(response);
    }

    @Test
    void citations_missing() {
        // Use OpenAiChatModel to access Perplexity API
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.PERPLEXITY_API_KEY)
                .baseUrl("https://api.perplexity.ai")
                .modelName("sonar")
                .logRequests(true)
                .logResponses(true)
                .build();

        String question = """
                Where is the Eiffel Tower located?
                """;
        ChatResponse answer = model.chat(UserMessage.userMessage(question));
        System.out.println(answer);
        assertFalse(answer.aiMessage().text()
                .toLowerCase().contains("citations"));
    }

    @Test
    void citations_present() throws IOException {
        // Included with LangChain4j:
        var client = new OkHttpClient();  // Networking
        var mapper = new ObjectMapper();  // JSON parser/generator

        // Use Java textblock to create a question with a model
        String question = """
                {
                    "model": "sonar",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Where is the Eiffel Tower located?"
                        }
                    ]
                }
                """;

        // Send synchronous HTTP request to Perplexity API
        var body = RequestBody.create(question,
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.perplexity.ai/chat/completions")
                .addHeader("Authorization",
                        "Bearer %s".formatted(ApiKeys.PERPLEXITY_API_KEY))
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
            assertTrue(answer.toLowerCase().contains("citations"));

            // Read JSON without mapping to classes
            JsonNode root = mapper.readTree(answer);
            System.out.println(root);

            // Path to content is $.choices[0].message.content
            String output = root.at("/choices/0/message/content").asText();
            System.out.println("Output: " + output);

            // "citations" is a direct child of the root: $.citations
            // JSON type is an array of strings
            List<String> citations = mapper.convertValue(
                    root.path("citations"), new TypeReference<>() {});
            for (int i = 0; i < citations.size(); i++) {
                System.out.printf("[%d] %s%n", i + 1, citations.get(i));
            }
        }

    }
}

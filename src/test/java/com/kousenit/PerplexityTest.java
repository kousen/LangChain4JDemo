package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@EnabledIfEnvironmentVariable(named = "PERPLEXITY_API_KEY", matches = ".*")
public class PerplexityTest {

    private final ChatLanguageModel model = AiModels.PERPLEXITY_SMALL_SONAR;

    private void saveAnswerToMarkdownFile(String question, String answer) {
        String fileName = question.toLowerCase()
                .replace(" ", "_")
                .substring(0, 20);
        Path path = Paths.get("src/test/resources/%s.md".formatted(fileName));
        try {
            Files.writeString(path,
                    "## Question: \n%s\n\n## Answer: \n%s\n".formatted(question, answer));
            System.out.println("Saved to " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sonarVsChat() {
        String question = """
                What is the difference between the
                Perplexity Sonar online models and the
                Perplexity Chat models?
                """;
        String answer = model.generate(question);
        saveAnswerToMarkdownFile(question, answer);
    }

    @Test
    void perplexityVsGPT() {
        String question = """
                Why should I use the Perplexity API instead of
                GPT-4o, Claude 3.5, Gemini 1.5 Flash directly?
                """;
        String answer = model.generate(question);
        saveAnswerToMarkdownFile(question, answer);
    }

    @Test
    void starsInTheUniverse() {
        String question = """
                How many stars are there in the universe?
                """;
        String answer = model.generate(question);
        saveAnswerToMarkdownFile(question, answer);
    }

    @Test
    void starsInTheUniverse_chatResponse() {
        String question = """
                How many stars are there in the universe?
                """;
        ChatResponse response = model.chat(
                ChatRequest.builder()
                        .messages(UserMessage.from(question))
                        .build());
        System.out.println(response);
    }

    @Test
    void starsInTheUniverse_userMessage() {
        String question = """
                How many stars are there in the universe?
                """;
        Response<AiMessage> response = model.generate(UserMessage.from(question));
        System.out.println(response);
    }
}

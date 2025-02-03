package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@EnabledIfEnvironmentVariable(named = "PERPLEXITY_API_KEY", matches = ".*")
public class PerplexityTest {

    private static final List<ChatLanguageModel> perplexityModels =
            List.of(AiModels.SONAR, AiModels.SONAR_PRO, AiModels.SONAR_REASONING);

    private static List<ChatLanguageModel> perplexityModels() {
        return perplexityModels;
    }

    private void saveAnswerToMarkdownFile(String question, String answer, ChatLanguageModel model) {
        String fileName = question.toLowerCase()
                .replace(" ", "_")
                .substring(0, 20);
        fileName = "%s_%s".formatted(fileName, model.getClass().getSimpleName());
        Path path = Paths.get("src/test/resources/%s.md".formatted(fileName));
        try {
            Files.writeString(path,
                    "## Question: \n%s\n\n## Answer: \n%s\n".formatted(question, answer));
            System.out.println("Saved to " + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void perplexityVsGPT(ChatLanguageModel model) {
        String question = """
                Why should I use the Perplexity API instead of
                GPT-4o, Claude 3.5, Gemini 1.5 Flash, or even
                DeepSeek R1 directly?
                """;
        String answer = model.generate(question);
        saveAnswerToMarkdownFile(question, answer, model);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse(ChatLanguageModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        String answer = model.generate(question);
        saveAnswerToMarkdownFile(question, answer, model);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse_chatResponse(ChatLanguageModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        ChatResponse response = model.chat(
                ChatRequest.builder()
                        .messages(UserMessage.from(question))
                        .build());
        System.out.println(response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("perplexityModels")
    void starsInTheUniverse_userMessage(ChatLanguageModel model) {
        String question = """
                How many stars are there in the universe?
                """;
        Response<AiMessage> response = model.generate(UserMessage.from(question));
        System.out.println(response);
    }
}

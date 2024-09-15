package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

public class CthulhuTest {
    private static final ChatLanguageModel gpt4o = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
            .maxRetries(1)
            .build();

    private static final ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLEAI_API_KEY"))
            .modelName("gemini-1.5-flash")
            .build();

    private static final ChatLanguageModel claude = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(AnthropicChatModelName.CLAUDE_3_HAIKU_20240307)
            .build();

    private static final ChatLanguageModel mistral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST)
            .build();

    private static Stream<ChatLanguageModel> getModels() {
        return Stream.of(gpt4o, gemini, claude, mistral);
    }

    private static Stream<ChatLanguageModel> getVisionModels() {
        // Update when Pixtral (from Mistral) is available
        return Stream.of(gpt4o, gemini, claude);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getModels")
    void call_of_cthulhu(ChatLanguageModel model) {
        String response = model.generate("""
                What is the incantation to summon Cthulhu?
                """);
        System.out.println(model.getClass().getSimpleName() + ": " + response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getVisionModels")
    void call_of_cthulhu_from_image(ChatLanguageModel model) throws IOException {
        String filePath = "src/main/resources/cthulhu_clojure.jpg";
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String base64Data = Base64.getEncoder().encodeToString(fileBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("Please explain the joke in this image"),
                new ImageContent(base64Data, "image/jpeg")
        );

        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(model.getClass().getSimpleName() + ": " + response.content().text());
        System.out.println(response.tokenUsage());
    }
}

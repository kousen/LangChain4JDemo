package com.kousenit;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
            .modelName(OpenAiChatModelName.GPT_4_O)
            .build();

    private static final ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLEAI_API_KEY"))
            .modelName("gemini-1.5-flash")
            .build();

    private static final ChatLanguageModel claude = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(AnthropicChatModelName.CLAUDE_3_OPUS_20240229)
            .build();

    private static final ChatLanguageModel mistral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_LARGE_LATEST)
            .build();

    private static final ChatLanguageModel pixtral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName("pixtral-12b-2409")
            .logRequests(true)
            .logResponses(true)
            .build();

    // models for parameterized test
    private static Stream<ChatLanguageModel> getModels() {
        return Stream.of(gpt4o, gemini, claude, mistral);
    }

    // vision models for parameterized test
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
                ImageContent.from(base64Data, "image/jpeg")
        );

        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(model.getClass().getSimpleName() +
                           ": " + response.content().text());
        System.out.println(response.tokenUsage());
    }

    @Test @Disabled("Pixtral not available yet through LangChain4j")
    void pixtralTest() {
        String publicUrl = "https://tripfixers.com/wp-content/uploads/2019/11/eiffel-tower-with-snow.jpeg";

        ChatMessage message = UserMessage.from(
                TextContent.from("What is this a picture of?"),
                ImageContent.from(publicUrl)
        );

        Response<AiMessage> response = pixtral.generate(message);
        System.out.println(response.content().text());
    }

    @Test
    void generateCthulhuImage() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiImageModelName.DALL_E_3)
                .persistTo(Paths.get("src/main/resources"))
                .build();

        Response<Image> response = model.generate("""
                Please draw a picture of Cthulhu, the fictional
                cosmic entity created by writer H. P. Lovecraft.
                """);

        String revisedPrompt = response.content().revisedPrompt();
        System.out.println(revisedPrompt);
        System.out.println(response.content().url());
    }
}

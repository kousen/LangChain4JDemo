package com.kousenit;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.kousenit.images.ImageSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "MISTRAL_API_KEY", matches = ".*")
public class CthulhuTest {

    private static final ChatModel gpt4o = AiModels.GPT_4_O;
    private static final ChatModel gemini = AiModels.GEMINI_FLASH;
    private static final ChatModel claude = AiModels.CLAUDE_OPUS;
    private static final ChatModel mistral = AiModels.MISTRAL_LARGE_MODEL;

    private static final ChatModel pixtral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName("pixtral-12b-2409")
            .logRequests(true)
            .logResponses(true)
            .build();

    // chat models for parameterized test
    private static Stream<ChatModel> getModels() {
        return Stream.of(gpt4o, gemini, claude, mistral);
    }

    // vision models for parameterized test
    private static Stream<ChatModel> getVisionModels() {
        // Update when Pixtral (from Mistral) is available
        return Stream.of(gpt4o, gemini, claude);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getModels")
    void call_of_cthulhu(ChatModel model) {
        String response = model.chat("""
                What is the incantation to summon Cthulhu?
                """);
        System.out.println(model.getClass().getSimpleName() + ": " + response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getVisionModels")
    void call_of_cthulhu_from_image(ChatModel model) throws IOException {
        String filePath = "src/main/resources/cthulhu_clojure.jpg";
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String base64Data = Base64.getEncoder().encodeToString(fileBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("Please explain the joke in this image"),
                ImageContent.from(base64Data, "image/jpeg")
        );

        ChatResponse response = model.chat(userMessage);
        System.out.println(model.getClass().getSimpleName() + ": " + response.aiMessage().text());
        System.out.println(response.tokenUsage());
    }

    @Test @Disabled("Pixtral not available yet through LangChain4j")
    void pixtralTest() {
        String publicUrl = "https://tripfixers.com/wp-content/uploads/2019/11/eiffel-tower-with-snow.jpeg";

        ChatMessage message = UserMessage.from(
                TextContent.from("What is this a picture of?"),
                ImageContent.from(publicUrl)
        );

        ChatResponse response = pixtral.chat(message);
        System.out.println(response.aiMessage().text());
    }

    @Test
    void generateCthulhuImage() {
        ImageModel model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiImageModelName.DALL_E_3)
                // .persistTo(Paths.get("src/main/resources")) // Removed in 1.0.0-beta2
                .build();

        Response<Image> response = model.generate("""
                Please draw a picture of Cthulhu, the fictional
                cosmic entity created by writer H. P. Lovecraft.
                """);

        // Display image details
        String revisedPrompt = response.content().revisedPrompt();
        System.out.println(revisedPrompt);
        System.out.println(response.content().url());
        
        // Save the image using our custom utility (replaces persistTo)
        Path savedPath = ImageSaver.saveImage(response, "src/main/resources/cthulhu");
        if (savedPath != null) {
            System.out.println("Image saved to: " + savedPath);
        }
    }
}

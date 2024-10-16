package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class GeminiFlashTest {

    private final ChatLanguageModel model = AiModels.GEMINI_FLASH;

    @Test
    public void testGenerateWithMessages() {
        UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        System.out.println(aiMessage);
    }

    @Test
    void testGenerateWithFinishReasons() {
        UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        System.out.println(aiMessage);
        String response = switch (aiMessage.finishReason()) {
            case STOP -> aiMessage.content().text();
            case LENGTH -> "Token limit reached";
            case TOOL_EXECUTION -> "Tool execution needed";
            case CONTENT_FILTER -> "Content filtering required";
            case OTHER -> "Call finished for some other reason";
        };
        System.out.println(response);
    }

    @Test
    void testGenerateWithTokenUsage() {
        UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        TokenUsage tokenUsage = aiMessage.tokenUsage();
        System.out.println("Input tokens: " + tokenUsage.inputTokenCount());
        System.out.println("Output tokens: " + tokenUsage.outputTokenCount());
        System.out.println("Total tokens: " + tokenUsage.totalTokenCount());
    }

    @Test
    void vision_from_localFile() throws IOException {
        String filePath = "src/main/resources/langchain4j_get_doc_by_id.png";
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String base64Data = Base64.getEncoder().encodeToString(fileBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("How would you answer this question?"),
                new ImageContent(base64Data, "image/png")
        );

        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(response.content().text());
        System.out.println(response.tokenUsage());
    }

    @Test
    @Disabled("Invalid or unsupported file uri")
    void vision_from_publicURL() {
        String imageUrl =
                "https://upload.wikimedia.org/wikipedia/commons/a/a0/Hello_Kitty_coffee.jpg";
        UserMessage userMessage = UserMessage.from(
                TextContent.from("What character is shown in this image?"),
                ImageContent.from(imageUrl)
        );
        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(response.content().text());
        System.out.println(response.tokenUsage());

    }

}

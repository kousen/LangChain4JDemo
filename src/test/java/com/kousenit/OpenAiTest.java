package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class OpenAiTest {

    private final ChatLanguageModel model = AiModels.GPT_4_O;

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
    void streamingChat() throws InterruptedException {
        StreamingChatLanguageModel chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        var latch = new CountDownLatch(1);
        chatModel.generate("Tell me a joke about Java",
                new StreamingResponseHandler<>() {
                    @Override
                    public void onNext(String token) {
                        System.out.println("onNext(): " + token);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("onError(): " + error.getMessage());
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        System.out.println("completed");
                        StreamingResponseHandler.super.onComplete(response);
                        latch.countDown();
                    }
                });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "TokenStream did not complete in time");

    }

    @Test
    void vision_from_localFile() throws IOException {
        String filePath = "src/main/resources/skynet.jpg";
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String base64Data = Base64.getEncoder().encodeToString(fileBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("""
                        My boss wants me to embed
                        an AI model into this robot,
                        which my company (identified
                        by the logo in the picture)
                        is planning to build.
                        
                        What could go wrong?
                        """),
                new ImageContent(base64Data, "image/png")
        );

        Response<AiMessage> response = model.generate(userMessage);
        System.out.println(response.content().text());
        System.out.println(response.tokenUsage());
    }

    @Test
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

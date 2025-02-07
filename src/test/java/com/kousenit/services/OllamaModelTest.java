package com.kousenit.services;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class OllamaModelTest {

    @Test
    void simple_example() {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("orca-mini")
                .build();

        String answer = model.chat(
                "Provide 3 short bullet points explaining why Java is awesome");

        System.out.println(answer);
    }

    @Test
    void streaming_example() {
        var model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("orca-mini")
                .build();

        var futureResponse = new CompletableFuture<ChatResponse>();
        model.chat("What is the capital of France?", new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        futureResponse.join();
    }

    @Test
    void json_output_example() {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("orca-mini")
                .build();

        String json = model.chat(
                "Give me a JSON with 2 fields: name and age of a John Doe, 42");

        System.out.println(json);
    }
}

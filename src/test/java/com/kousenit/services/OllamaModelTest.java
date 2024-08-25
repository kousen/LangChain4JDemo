package com.kousenit.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class OllamaModelTest {

    @Test
    void simple_example() {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("orca-mini")
                .build();

        String answer = model.generate(
                "Provide 3 short bullet points explaining why Java is awesome");

        System.out.println(answer);
    }

    @Test
    void streaming_example() {
        var model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("orca-mini")
                .build();

        var futureResponse = new CompletableFuture<Response<AiMessage>>();
        model.generate("What is the capital of France?", new StreamingResponseHandler<>() {
            @Override
            public void onNext(String token) {
                System.out.print(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                System.out.println();
                futureResponse.complete(response);
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
                .format("json")
                .build();

        String json = model.generate(
                "Give me a JSON with 2 fields: name and age of a John Doe, 42");

        System.out.println(json);
    }
}

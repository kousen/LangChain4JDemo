package com.kousenit.services;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GeminiTest {

    @Test
    void sqrtSumLetters_gemini() {
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project("gemini-demos-415920")
                .location("us-central1")
                .modelName("gemini-pro")
                .build();
        Assistant geminiAssistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        String question = "Return the string 3.16";
        String answer = geminiAssistant.chat(question);
        System.out.println(answer);
        assertThat(answer).contains("3.16");
    }
}

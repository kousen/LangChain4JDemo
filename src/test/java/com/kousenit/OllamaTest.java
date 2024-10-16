package com.kousenit;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;

public class OllamaTest {
    @Test
    void ollamaExample() {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .build();

        System.out.println(model.generate("""
                Why is the sky blue?
                """));
    }
}

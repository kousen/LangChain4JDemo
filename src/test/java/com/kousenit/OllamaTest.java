package com.kousenit;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.junit.jupiter.api.Test;

public class OllamaTest {
    @Test
    void ollamaExample() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("deepseek-r1:70b")
                .build();

        System.out.println(model.chat("""
                How many r's are in the word "strawberry"?
                """));
    }
}

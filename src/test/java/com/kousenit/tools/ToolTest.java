package com.kousenit.tools;

import com.kousenit.AiModels;
import com.kousenit.services.Assistant;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolTest {
    private final Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(AiModels.GPT_4_O)
            .tools(new Calculator())
            //.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

    @Test
    void testFromLangChain4JExampleProject() {
        String question = """
        What is the square root of the sum of the numbers of letters
        in the words "hello" and "world"
        """;
        String answer = assistant.chat(question);
        System.out.println(answer);
        assertThat(answer).contains("3.162");
    }
}

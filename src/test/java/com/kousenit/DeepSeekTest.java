package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

public class DeepSeekTest {
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl("https://api.deepseek.com")
            .apiKey(ApiKeys.DEEPSEEK_API_KEY)
            .modelName("deepseek-chat")
            .build();

    @Test
    public void testGenerateWithMessages() {
        UserMessage userMessage = UserMessage.from("""
            How many r's are in the word "strawberry"?
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        System.out.println(aiMessage);
    }
}

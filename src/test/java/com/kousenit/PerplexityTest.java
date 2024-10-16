package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "PERPLEXITY_API_KEY", matches = ".*")
public class PerplexityTest {

    private final ChatLanguageModel model = AiModels.PERPLEXITY_SONAR;

    @Test
    public void testGenerateWithMessages() {
        UserMessage userMessage = UserMessage.from("""
            What is the difference between the
            Perplexity Sonar models and the
            Perplexity Chat models?
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        System.out.println(aiMessage);
    }

    @Test
    void testGenerateWithTokenUsage() {
        UserMessage userMessage = UserMessage.from("""
            What is the difference between the
            Perplexity Sonar models and the
            Perplexity Chat models?
            """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        TokenUsage tokenUsage = aiMessage.tokenUsage();
        System.out.println("Input tokens: " + tokenUsage.inputTokenCount());
        System.out.println("Output tokens: " + tokenUsage.outputTokenCount());
        System.out.println("Total tokens: " + tokenUsage.totalTokenCount());
    }
}

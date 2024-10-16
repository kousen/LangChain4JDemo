package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;

@SuppressWarnings("unused")
public class HelloPerplexity {
    public static final String PERPLEXITY_SONAR_SMALL = "llama-3.1-sonar-small-128k-online";
    public static final String PERPLEXITY_SONAR_LARGE = "llama-3.1-sonar-large-128k-online";
    public static final String PERPLEXITY_SONAR_HUGE = "llama-3.1-sonar-huge-128k-online";
    public static final String PERPLEXITY_CHAT_SMALL = "llama-3.1-sonar-small-128k-chat";
    public static final String PERPLEXITY_CHAT_LARGE = "llama-3.1-sonar-large-128k-chat";

    public static final String PERPLEXITY_BASE_URL = "https://api.perplexity.ai";

    // Perplexity uses the OpenAI model, but with its
    // own API key and base URL
    private static final ChatLanguageModel perplexity = OpenAiChatModel.builder()
            .apiKey(ApiKeys.PERPLEXITY_API_KEY)
            .baseUrl(PERPLEXITY_BASE_URL)
            .modelName(PERPLEXITY_CHAT_SMALL)
            .build();

    public static void main(String[] args) {
        var userMessage = UserMessage.from("""
                What is the difference between the
                Perplexity Sonar models and the
                Perplexity Chat models?
                """);

        Response<AiMessage> aiMessage = perplexity.generate(userMessage);
        System.out.println(aiMessage);
    }
}

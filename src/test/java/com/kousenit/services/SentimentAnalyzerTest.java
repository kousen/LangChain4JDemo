package com.kousenit.services;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SentimentAnalyzerTest {
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            //.logRequests(true)
            //.logResponses(true)
            .build();

    private final SentimentAnalyzer sentimentAnalyzer = AiServices.create(
            SentimentAnalyzer.class, model);

    @Test
    void analyzeSentimentOf() {
        assertThat(sentimentAnalyzer.analyzeSentimentOf("I love Java"))
                .isEqualTo(Sentiment.POSITIVE);
    }

    @Test
    void isPositive() {
        assertThat(sentimentAnalyzer.isPositive("I love Java")).isTrue();
    }
}
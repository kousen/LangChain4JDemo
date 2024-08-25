package com.kousenit.services;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SummarizerTest {

    private final Summarizer summarizer = AiServices.create(Summarizer.class,
            OpenAiChatModel.withApiKey(ApiKeys.OPENAI_API_KEY));

    @Test
    void summarize() {
        String text = """
                AI, or artificial intelligence, is a branch of computer science that aims to create
                machines that mimic human intelligence. This can range from simple tasks such as recognizing
                patterns or speech to more complex tasks like making decisions or predictions.
                """;
        List<String> bulletPoints = summarizer.summarize(text, 3);
        assertEquals(3, bulletPoints.size());
        bulletPoints.forEach(System.out::println);
    }
}
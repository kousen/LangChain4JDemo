package com.kousenit.services;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeExtractorTest {
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .logRequests(true)
            .logResponses(true)
            .build();

    private final DateTimeExtractor extractor = AiServices.create(DateTimeExtractor.class,
            model);

    private final String text = """
            The tranquility pervaded the evening of 1968,
            just fifteen minutes shy of midnight,
            following the celebrations of Independence Day.""";

    @Test
    void extractDateFrom() {
        LocalDate date = extractor.extractDateFrom(text);
        assertEquals(LocalDate.of(1968, 7, 4), date);
    }

    @Test
    void extractTimeFrom() {
        LocalTime time = extractor.extractTimeFrom(text);
        assertEquals(LocalTime.of(23, 45), time);
    }

    @Test
    void extractDateTimeFrom() {
        LocalDateTime dateTime = extractor.extractDateTimeFrom(text);
        assertEquals(LocalDateTime.of(1968, 7, 4, 23, 45), dateTime);
    }
}
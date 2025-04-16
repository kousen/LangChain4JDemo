package com.kousenit.extraction;

import com.kousenit.services.DateTimeExtractor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for extracting date and time information from text using LangChain4j
 */
public class DateTimeExtraction {
    private final DateTimeExtractor extractor;
    private final Map<String, DateTimeFormatter> formatters;

    /**
     * Creates a new DateTimeExtraction instance with the given chat model
     *
     * @param model The chat language model to use for extraction
     */
    public DateTimeExtraction(ChatLanguageModel model) {
        this.extractor = AiServices.builder(DateTimeExtractor.class)
                .chatLanguageModel(model)
                .build();
        
        // Initialize formatters
        this.formatters = new HashMap<>();
        this.formatters.put("date", DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        this.formatters.put("time", DateTimeFormatter.ofPattern("h:mm a"));
        this.formatters.put("dateTime", DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a"));
    }

    /**
     * Extracts a date from the given text
     *
     * @param text The text to extract a date from
     * @return The extracted date
     */
    public LocalDate extractDateFrom(String text) {
        return extractor.extractDateFrom(text);
    }

    /**
     * Extracts a time from the given text
     *
     * @param text The text to extract a time from
     * @return The extracted time
     */
    public LocalTime extractTimeFrom(String text) {
        return extractor.extractTimeFrom(text);
    }

    /**
     * Extracts a date and time from the given text
     *
     * @param text The text to extract a date and time from
     * @return The extracted date and time
     */
    public LocalDateTime extractDateTimeFrom(String text) {
        return extractor.extractDateTimeFrom(text);
    }

    /**
     * Formats a date as a string using the default date formatter
     *
     * @param date The date to format
     * @return A formatted string representation of the date
     */
    public String formatDate(LocalDate date) {
        return date.format(formatters.get("date"));
    }

    /**
     * Formats a time as a string using the default time formatter
     *
     * @param time The time to format
     * @return A formatted string representation of the time
     */
    public String formatTime(LocalTime time) {
        return time.format(formatters.get("time"));
    }

    /**
     * Formats a date and time as a string using the default date-time formatter
     *
     * @param dateTime The date and time to format
     * @return A formatted string representation of the date and time
     */
    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatters.get("dateTime"));
    }

    /**
     * Extracts and formats all date and time information from the given text
     *
     * @param text The text to extract date and time information from
     * @return A formatted string with all extracted date and time information
     */
    public String extractAndFormatAll(String text) {
        StringBuilder result = new StringBuilder();
        result.append("Original text: ").append(text).append("\n");
        
        try {
            LocalDate date = extractDateFrom(text);
            result.append("Extracted date: ").append(formatDate(date)).append("\n");
        } catch (Exception e) {
            result.append("No date found or could not extract date.\n");
        }
        
        try {
            LocalTime time = extractTimeFrom(text);
            result.append("Extracted time: ").append(formatTime(time)).append("\n");
        } catch (Exception e) {
            result.append("No time found or could not extract time.\n");
        }
        
        try {
            LocalDateTime dateTime = extractDateTimeFrom(text);
            result.append("Extracted date and time: ").append(formatDateTime(dateTime)).append("\n");
        } catch (Exception e) {
            result.append("No date and time found or could not extract date and time.\n");
        }
        
        return result.toString();
    }
}
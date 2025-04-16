package com.kousenit.extraction;

import com.kousenit.AiModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DateTimeExtractionTest {

    private DateTimeExtraction dateTimeExtraction;

    @BeforeEach
    void setUp() {
        dateTimeExtraction = new DateTimeExtraction(AiModels.GPT_4_O);
    }

    @Test
    void extractDateFromText() {
        String text = "The meeting is scheduled for March 15, 2023 at 2:30 PM.";
        
        LocalDate date = dateTimeExtraction.extractDateFrom(text);
        String formatted = dateTimeExtraction.formatDate(date);
        
        System.out.println("Extracted date: " + formatted);
        
        assertThat(date).isEqualTo(LocalDate.of(2023, 3, 15));
    }

    @Test
    void extractTimeFromText() {
        String text = "The meeting is scheduled for March 15, 2023 at 2:30 PM.";
        
        LocalTime time = dateTimeExtraction.extractTimeFrom(text);
        String formatted = dateTimeExtraction.formatTime(time);
        
        System.out.println("Extracted time: " + formatted);
        
        assertThat(time).isEqualTo(LocalTime.of(14, 30));
    }

    @Test
    void extractDateTimeFromText() {
        String text = "The meeting is scheduled for March 15, 2023 at 2:30 PM.";
        
        LocalDateTime dateTime = dateTimeExtraction.extractDateTimeFrom(text);
        String formatted = dateTimeExtraction.formatDateTime(dateTime);
        
        System.out.println("Extracted date and time: " + formatted);
        
        assertThat(dateTime).isEqualTo(LocalDateTime.of(2023, 3, 15, 14, 30));
    }

    @Test
    void extractAndFormatAllTest() {
        String text = "The concert will be held on December 31, 2023 at 8:00 PM.";
        
        String result = dateTimeExtraction.extractAndFormatAll(text);
        
        System.out.println(result);
        
        assertThat(result)
                .contains("December 31, 2023")
                .contains("8:00 PM")
                .contains("December 31, 2023 at 8:00 PM");
    }

    @Test
    void handleMissingDateOrTime() {
        String textWithoutTime = "Please submit your report by next Friday, April 5.";
        String textWithoutDate = "The store opens at 9:00 AM and closes at 9:00 PM.";
        
        String resultWithoutTime = dateTimeExtraction.extractAndFormatAll(textWithoutTime);
        String resultWithoutDate = dateTimeExtraction.extractAndFormatAll(textWithoutDate);
        
        System.out.println("Text without time:\n" + resultWithoutTime);
        System.out.println("Text without date:\n" + resultWithoutDate);
        
        assertThat(resultWithoutTime).contains("April 5");
        assertThat(resultWithoutDate).contains("9:00 AM");
    }

    @Test
    void formattersTest() {
        LocalDate date = LocalDate.of(2023, 12, 31);
        LocalTime time = LocalTime.of(20, 0);
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 31, 20, 0);
        
        assertAll(
                () -> assertThat(dateTimeExtraction.formatDate(date)).isEqualTo("December 31, 2023"),
                () -> assertThat(dateTimeExtraction.formatTime(time)).isEqualTo("8:00 PM"),
                () -> assertThat(dateTimeExtraction.formatDateTime(dateTime)).isEqualTo("December 31, 2023 at 8:00 PM")
        );
    }
}
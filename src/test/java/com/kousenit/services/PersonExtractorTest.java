package com.kousenit.services;

import com.kousenit.AiModels;
import com.kousenit.tools.DateTimeTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersonExtractorTest {

    private final PersonExtractor extractor = AiServices.builder(PersonExtractor.class)
            .chatLanguageModel(AiModels.OPENAI_CHAT_MODEL)
            .tools(new DateTimeTool())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

    // From the langchain4j-examples project
    @Test
    void extractPersonFromText() {
        String text = """
                In 1968, amidst the fading echoes of Independence Day,
                a child named John arrived under the calm evening sky.
                This newborn, bearing the surname Doe, marked the start of a new journey.
                He was welcomed into the world at 345 Whispering Pines Avenue
                a quaint street nestled in the heart of Springfield
                an abode that echoed with the gentle hum of suburban
                dreams and aspirations.
                """;

        Person person = extractor.extractPersonFrom(text);
        System.out.println(person);

        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("John"),
                () -> assertThat(person.lastName()).isEqualTo("Doe"),
                () -> assertThat(person.birthDate().toString()).isEqualTo("1968-07-04"),
                () -> assertThat(person.address().street()).contains("Whispering Pines"),
                () -> assertThat(person.address().streetNumber()).isEqualTo(345),
                () -> assertThat(person.address().city()).isEqualTo("Springfield")
        );
    }

    @Test
    void picard() {
        int yearsFromNow = 2305 - LocalDate.now().getYear();
        String text = """
                Captain Picard was born in La Barre, France on Earth
                on the 13th of juillet, %d years in the future.
                His given name, Jean-Luc, is of French origin. He and his brother
                Robert were raised on the family vineyard, Chateau Picard,
                located in Saint-Estephe, in Burgundy.
                """.formatted(yearsFromNow);

        Person person = extractor.extractPersonFrom(text);
        System.out.println(person);

        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("Jean-Luc"),
                () -> assertThat(person.lastName()).isEqualTo("Picard"),
                () -> assertThat(person.birthDate()
                        .getDayOfMonth()).isEqualTo(13),
                () -> assertThat(person.birthDate()
                        .getMonth()).isEqualTo(Month.JULY),
                () -> assertThat(person.birthDate()
                        .getYear()).isCloseTo(2305, offset(5))
        );
    }
}
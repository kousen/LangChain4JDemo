package com.kousenit.extraction;

import com.kousenit.AiModels;
import com.kousenit.services.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersonExtractionTest {

    private PersonExtraction personExtraction;

    @BeforeEach
    void setUp() {
        personExtraction = new PersonExtraction(AiModels.GPT_4_O);
    }

    @Test
    void extractPersonFromText() {
        String text = """
                John Smith was born on May 15, 1985. He lives at 123 Main Street,
                apartment 4B, in New York City.
                """;

        Person person = personExtraction.extractPersonFrom(text);
        String formatted = personExtraction.formatPerson(person);
        
        System.out.println(formatted);

        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("John"),
                () -> assertThat(person.lastName()).isEqualTo("Smith"),
                () -> assertThat(person.birthDate()).isEqualTo(LocalDate.of(1985, 5, 15)),
                () -> assertThat(person.address().street()).contains("Main Street"),
                () -> assertThat(person.address().streetNumber()).isEqualTo(123),
                () -> assertThat(person.address().city()).isEqualTo("New York City")
        );
    }

    @Test
    void extractAnotherPersonFromText() {
        String text = """
                Sarah Johnson is a software engineer born on December 3, 1990.
                She recently moved to 456 Oak Avenue in San Francisco.
                """;

        Person person = personExtraction.extractPersonFrom(text);
        String formatted = personExtraction.formatPerson(person);
        
        System.out.println(formatted);

        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("Sarah"),
                () -> assertThat(person.lastName()).isEqualTo("Johnson"),
                () -> assertThat(person.birthDate()).isEqualTo(LocalDate.of(1990, 12, 3)),
                () -> assertThat(person.address().street()).contains("Oak Avenue"),
                () -> assertThat(person.address().streetNumber()).isEqualTo(456),
                () -> assertThat(person.address().city()).isEqualTo("San Francisco")
        );
    }

    @Test
    void formatPersonTest() {
        Person person = new Person(
                "John",
                "Smith",
                new Person.Address("Main Street", 123, "New York City"),
                LocalDate.of(1985, 5, 15)
        );

        String formatted = personExtraction.formatPerson(person);
        
        assertThat(formatted).contains("John")
                .contains("Smith")
                .contains("1985-05-15")
                .contains("123 Main Street, New York City");
    }
}
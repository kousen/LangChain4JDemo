package com.kousenit.extraction;

import com.kousenit.services.Person;
import com.kousenit.services.PersonExtractor;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

/**
 * Class for extracting Person records from text using LangChain4j
 */
public class PersonExtraction {
    private final PersonExtractor extractor;

    /**
     * Creates a new PersonExtraction instance with the given chat model
     *
     * @param model The chat language model to use for extraction
     */
    public PersonExtraction(ChatLanguageModel model) {
        this.extractor = AiServices.builder(PersonExtractor.class)
                .chatLanguageModel(model)
                .build();
    }

    /**
     * Extracts a Person record from the given text
     *
     * @param text The text to extract a Person from
     * @return The extracted Person record
     */
    public Person extractPersonFrom(String text) {
        return extractor.extractPersonFrom(text);
    }

    /**
     * Formats a Person record as a string for display
     *
     * @param person The Person record to format
     * @return A formatted string representation of the Person
     */
    public String formatPerson(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append("Extracted Person:\n");
        sb.append("First Name: ").append(person.firstName()).append("\n");
        sb.append("Last Name: ").append(person.lastName()).append("\n");
        sb.append("Birth Date: ").append(person.birthDate()).append("\n");
        
        if (person.address() != null) {
            sb.append("Address: ");
            if (person.address().streetNumber() != null) {
                sb.append(person.address().streetNumber()).append(" ");
            }
            if (person.address().street() != null) {
                sb.append(person.address().street());
            }
            if (person.address().city() != null) {
                sb.append(", ").append(person.address().city());
            }
        }
        
        return sb.toString();
    }
}
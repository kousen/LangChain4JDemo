package com.kousenit.services.openai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenAiSearchTest {

    private final OpenAiSearch service = new OpenAiSearch();

    @Test
    void search() {
        var documentContext = service.search("""
                What are some good EV models
                available in Connecticut?
                """);
        assertNotNull(documentContext);
        //System.out.println(documentContext.jsonString());

        String evText = documentContext.read("$.output[1].content[0].text");
        List<String> urls = documentContext.read("$.output[1].content[0].annotations[*].url");

        System.out.println("EV Suggestions: " + evText);
        System.out.println("URLs: " + urls);
    }
}
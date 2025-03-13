package com.kousenit.services.openai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenAiResponsesServiceTest {

    private final OpenAiResponsesService service = new OpenAiResponsesService();

    @Test
    void search() {
        var searchResult = service.search("""
                What are some good EV models
                available in Connecticut?
                """);
        assertNotNull(searchResult);
        System.out.println("EV Suggestions: " + searchResult.mainContent());
        System.out.println("URLs: " + searchResult.urls());
        if (!searchResult.outputText().equals(searchResult.mainContent())) {
            System.out.println("All output_text: " + searchResult.outputText());
        }
    }


    @Test
    void callOpenAI() {
        var jsonNode = service.callOpenAI("""
                What are some good EV models
                available in Connecticut?
                """);
        assertNotNull(jsonNode);

        String evText = service.extractMainContent(jsonNode);
        List<String> urls = service.extractUrls(jsonNode);

        System.out.println("EV Suggestions: " + evText);
        System.out.println("URLs: " + urls);
    }

    @Test
    void searchWithOutputText() {
        var jsonNode = service.callOpenAI("""
                What are some good EV models
                available in Connecticut?
                """);
        assertNotNull(jsonNode);

        String allOutputText = service.getOutputText(jsonNode);
        System.out.println("All output_text: " + allOutputText);
    }
}
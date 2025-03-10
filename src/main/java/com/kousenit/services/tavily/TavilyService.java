package com.kousenit.services.tavily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.kousenit.services.tavily.TavilyRecords.*;

public class TavilyService {

    public static final String TAVILY_API_KEY = System.getenv("TAVILY_API_KEY");
    public static final String EXTRACT_URL = "https://api.tavily.com/extract";
    public static final String SEARCH_URL = "https://api.tavily.com/search";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TavilyService() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public SearchResponse search(SearchQuery query) {
        String payload = serialize(query);
        HttpRequest request = createRequest(SEARCH_URL, payload);
        return sendRequest(request, SearchResponse.class);
    }

    public SearchResponse search(String query) {
        return search(
                new SearchQuery(query, "general", "basic", 5,
                        "day", 3, true, false,
                        false, false,
                        null, null));
    }

    public ExtractResponse extract(ExtractRequest request) {
        String payload = serialize(request);
        HttpRequest httpRequest = createRequest(EXTRACT_URL, payload);
        return sendRequest(httpRequest, ExtractResponse.class);
    }

    public String extract(String url) {
        ExtractResponse response = extract(new ExtractRequest(url, false, "basic"));
        return response.results().getFirst().rawContent();
    }

    private HttpRequest createRequest(String url, String payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + TAVILY_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }

    private <T> T sendRequest(HttpRequest request, Class<T> responseType) {
        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }
}

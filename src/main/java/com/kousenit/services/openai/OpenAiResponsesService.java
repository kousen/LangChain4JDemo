package com.kousenit.services.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OpenAiResponsesService {

    private static final String URL = "https://api.openai.com/v1/responses";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode search(String query) {
        String json = """
                {
                    "model": "gpt-4o",
                    "tools": [{"type": "web_search_preview"}],
                    "input": "%s"
                }
                """.formatted(query).replaceAll("\\s+", " ");

        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return mapper.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts and aggregates all output_text content blocks from an OpenAI response.
     * Mimics the behavior of the Python `output_text` property.
     *
     * @param rootNode The JsonNode representing the response
     * @return A string containing all concatenated output_text blocks, or an empty string if none exist
     */
    public String getOutputText(JsonNode rootNode) {
        return Optional.ofNullable(rootNode.path("output"))
                .filter(JsonNode::isArray)
                .map(outputArray -> StreamSupport.stream(outputArray.spliterator(), false)
                        .filter(output -> "message".equals(output.path("type").asText()))
                        .flatMap(message -> StreamSupport.stream(message.path("content").spliterator(), false))
                        .filter(content -> "output_text".equals(content.path("type").asText()))
                        .map(outputText -> outputText.path("text").asText())
                        .collect(Collectors.joining()))
                .orElse("");
    }

    /**
     * Extracts the primary text content from the OpenAI response.
     *
     * @param rootNode The JsonNode representing the response
     * @return The main text content, or an empty string if not found
     */
    public String extractMainContent(JsonNode rootNode) {
        JsonNode textNode = rootNode.at("/output/1/content/0/text");
        return textNode.isMissingNode() ? "" : textNode.asText();
    }

    /**
     * Extracts all URLs from the annotations in the OpenAI response.
     *
     * @param rootNode The JsonNode representing the response
     * @return A list of URLs from the annotations, or an empty list if none exist
     */
    public List<String> extractUrls(JsonNode rootNode) {
        JsonNode annotationsNode = rootNode.at("/output/1/content/0/annotations");

        if (annotationsNode.isMissingNode() || !annotationsNode.isArray()) {
            return List.of(); // Return empty list if no annotations found
        }

        return StreamSupport.stream(annotationsNode.spliterator(), false)
                .map(annotation -> annotation.path("url").asText())
                .filter(url -> !url.isEmpty()) // Filter out empty URLs
                .toList();
    }
}

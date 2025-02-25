package com.kousenit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class DeepSeekJackson {
    private static final String URL = "https://api.deepseek.com/chat/completions";
    private static final String KEY = System.getenv("DEEPSEEK_API_KEY");
    private boolean debug = false;

    public record DeepSeekRequest(
            String model,
            List<Message> messages,
            boolean stream
    ) {
    }

    public record Message(String role, String content) {
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode chat(String question, String model) {
        var request = new DeepSeekRequest(model,
                List.of(new Message("system", "You are a helpful assistant."),
                        new Message("user", question)),
                false);

        try (var client = HttpClient.newBuilder().build()) {
            String json = mapper.writeValueAsString(request);

            if (debug) {
                System.out.println("Transmitting...");
                System.out.println(json);
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String jsonBody = response.body();

            if (debug) {
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Received...");
                System.out.println(jsonBody);
            }

            return mapper.readTree(jsonBody);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

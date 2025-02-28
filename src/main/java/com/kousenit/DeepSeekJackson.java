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
    private static final String BASE_URL = "https://api.deepseek.com/chat";
    private static final String MODELS_URL = "https://api.deepseek.com/models";
    private static final String BETA_URL = "https://api.deepseek.com/beta";

    private static final String KEY = System.getenv("DEEPSEEK_API_KEY");
    private boolean debug = false;

    public record DeepSeekRequest(
            String model,
            List<Message> messages,
            boolean stream
    ) {
    }

    public record DeepSeekFIMRequest(
            String model,
            String prompt,
            String suffix
    ) {
    }

    public record Message(String role, String content) {
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    // Argument is JSON data that conforms to the API input requirements
    public JsonNode chat(String jsonRequest) {
        try (var client = HttpClient.newBuilder().build()) {

            if (debug) {
                System.out.println("Transmitting...");
                System.out.println(jsonRequest);
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/completions"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
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

    public JsonNode fim(String jsonRequest) {
        try (var client = HttpClient.newBuilder().build()) {

            if (debug) {
                System.out.println("Transmitting...");
                System.out.println(jsonRequest);
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BETA_URL + "/completions"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
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

    public JsonNode chat(String question, String model) {
        var request = new DeepSeekRequest(model,
                List.of(new Message("user", question)), false);
        try {
            return chat(mapper.writeValueAsString(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode fim(String model, String question, String suffix) {
        var request = new DeepSeekFIMRequest(model, question, suffix);
        try {
            return fim(mapper.writeValueAsString(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode models() {
        try (var client = HttpClient.newBuilder().build()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MODELS_URL))
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + KEY)
                    .build();
            HttpResponse<String> response =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (debug) {
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Received...");
                System.out.println(response.body());
            }

            return mapper.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

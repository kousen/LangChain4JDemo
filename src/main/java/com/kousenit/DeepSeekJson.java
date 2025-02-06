package com.kousenit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DeepSeekJson {
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

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public Object chat(String question, String model) {
        DeepSeekRequest request = new DeepSeekRequest(model,
                List.of(new Message("system", "You are a helpful assistant."),
                        new Message("user", question)),
                false);
        String json = gson.toJson(request);

        if (debug) {
            System.out.println("Transmitting...");
            System.out.println(json);
        }

        HttpClient.Version version = HttpClient.Version.HTTP_2;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try (HttpClient client = HttpClient.newBuilder()
                .version(version)
                .build()) {
            HttpResponse<String> response =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            String jsonBody = response.body();

            if (debug) {
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Received...");
                System.out.println(jsonBody);
            }
            return Configuration.defaultConfiguration().jsonProvider().parse(jsonBody);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

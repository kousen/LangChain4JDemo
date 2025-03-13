package com.kousenit.services.openai;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiSearch {

    private static final String URL = "https://api.openai.com/v1/responses";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    public DocumentContext search(String query) {
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
            Configuration conf = Configuration.builder()
                    .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                    .build();
            return JsonPath.using(conf).parse(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.kousenit.tools;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.kousenit.tools.OpenWeatherMapRecords.*;

// See https://openweathermap.org/current#data for weather details
public class OpenWeatherMap {
    private static final String API_KEY = System.getenv("OPENWEATHERMAP_API_KEY");
    private static final String ZIP_URL = "https://api.openweathermap.org/geo/1.0/zip";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final Logger logger = LoggerFactory.getLogger(OpenWeatherMap.class);

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public Coordinates getCoordinates(String zip) {
        String url = "%s?zip=%s,US&appid=%s".formatted(ZIP_URL, zip, API_KEY);
        try (var httpClient = HttpClient.newHttpClient()) {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(httpResponse.body(), Coordinates.class);
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("Get current weather for a given latitude and longitude")
    public WeatherResponse getWeather(double lat, double lon) {
        // units=imperial, metric, standard
        String url = "%s?lat=%f&lon=%f&appid=%s&units=imperial".formatted(WEATHER_URL, lat, lon, API_KEY);
        try (var httpClient = HttpClient.newHttpClient()) {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Error: " + httpResponse.body());
            }
            logger.info(httpResponse.body());
            return gson.fromJson(httpResponse.body(), WeatherResponse.class);
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ShortWeather getShortWeather(double lat, double lon) {
        var response = getWeather(lat, lon);
        var weather = response.weather().getFirst();
        var main = response.main();
        return new ShortWeather(weather.description(), main.tempMin(), main.tempMax(), main.temp());
    }
}

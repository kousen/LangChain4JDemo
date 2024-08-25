package com.kousenit.tools;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.kousenit.tools.OpenExchangeRatesRecords.*;

public class OpenExchangeRates {
    private static final String API_KEY = System.getenv("OPENEXCHANGERATES_API_KEY");
    private static final String BASE_URL = "https://openexchangerates.org/api/latest.json";
    private static final String CURRENCIES_URL = "https://openexchangerates.org/api/currencies.json";

    private final Logger logger = LoggerFactory.getLogger(OpenExchangeRates.class);

    private final Map<String, Double> rates;
    private final Map<String, String> currencies;

    public OpenExchangeRates() {
        this.rates = getLatestRates().rates();
        this.currencies = getCurrencies().currencies();
    }

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    public OpenExchangeRatesResponse getLatestRates() {
        try (var httpClient = HttpClient.newHttpClient()) {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Accept", "application/json")
                    .header("Authorization", "Token " + API_KEY)
                    .build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Error getting exchange rates: " + httpResponse.body());
            }
            return gson.fromJson(httpResponse.body(), OpenExchangeRatesResponse.class);
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CurrenciesResponse getCurrencies() {
        try (var httpClient = HttpClient.newHttpClient()) {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(CURRENCIES_URL))
                    .header("Accept", "application/json")
                    .header("Authorization", "Token " + API_KEY)
                    .build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = gson.fromJson(httpResponse.body(), mapType);
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Error getting currencies: " + httpResponse.body());
            }
            return new CurrenciesResponse(map);
        } catch (java.io.IOException | java.lang.InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("Convert a monetary amount from one currency to another")
    public double convert(double amount, String from, String to) {
        logger.info("Converting {} from {} to {}", amount, from, to);
        return amount * rates.get(to) / rates.get(from);
    }

    @Tool("Get the full name of a currency given its abbreviation")
    public String getCurrency(String abbreviation) {
        return currencies.get(abbreviation);
    }

}

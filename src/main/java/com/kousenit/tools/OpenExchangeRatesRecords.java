package com.kousenit.tools;

import java.util.Map;

public class OpenExchangeRatesRecords {
    public record OpenExchangeRatesResponse(
            String disclaimer,
            String license,
            long timestamp,
            String base,
            Map<String, Double> rates
    ) {}

    public record CurrenciesResponse(Map<String, String> currencies) {}


}

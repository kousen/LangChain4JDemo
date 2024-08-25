package com.kousenit.tools;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OpenExchangeRatesTest {
    private final OpenExchangeRates openExchangeRates = new OpenExchangeRates();

    @Test
    void getExchangeRates() {
        var rates = openExchangeRates.getLatestRates();
        assertAll(
                () -> assertThat(rates.disclaimer()).isEqualTo("Usage subject to terms: " +
                        "https://openexchangerates.org/terms"),
                () -> assertThat(rates.license()).isEqualTo(
                        "https://openexchangerates.org/license"),
                () -> assertThat(rates.timestamp()).isPositive(),
                () -> assertThat(rates.base()).isEqualTo("USD"),
                () -> assertThat(rates.rates().get("USD")).isOne()
        );
    }

    @Test
    void convert() {
        double euros = openExchangeRates.convert(100, "USD", "EUR");
        assertThat(euros).isPositive();
        System.out.println("100 USD is " + euros + " euros");
    }

    @Test
    void getCurrencies() {
        assertAll(
                () -> assertThat(openExchangeRates.getCurrency("USD")).isEqualTo("United States Dollar"),
                () -> assertThat(openExchangeRates.getCurrency("GBP")).isEqualTo("British Pound Sterling"),
                () -> assertThat(openExchangeRates.getCurrency("INR")).isEqualTo("Indian Rupee"),
                () -> assertThat(openExchangeRates.getCurrency("JPY")).isEqualTo("Japanese Yen"),
                () -> assertThat(openExchangeRates.getCurrency("EUR")).isEqualTo("Euro")
        );
    }

}
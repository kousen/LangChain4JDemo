package com.kousenit.tools;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OpenWeatherMapTest {
    private final OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    @Test
    void getCoordinates() {
        var coordinates = openWeatherMap.getCoordinates("78701");
        assertAll(
                () -> assertEquals("Austin", coordinates.name()),
                () -> assertEquals("US", coordinates.country()),
                () -> assertThat(coordinates.lat()).isBetween(30.0, 31.0),
                () -> assertThat(coordinates.lon()).isBetween(-98.0, -97.0),
                () -> assertEquals("78701", coordinates.zip())
        );
    }

    @Test
    void getWeather() {
        var weather = openWeatherMap.getWeather(30.2713, -97.7426);
        assertAll(
                () -> assertThat(weather.coord().lat()).isBetween(30.0, 31.0),
                () -> assertThat(weather.coord().lon()).isBetween(-98.0, -97.0),
                () -> assertThat(weather.weather().getFirst().id()).isPositive(),
                () -> assertThat(weather.weather().getFirst().main()).isNotEmpty(),
                () -> assertThat(weather.weather().getFirst().description()).isNotEmpty(),
                () -> assertThat(weather.weather().getFirst().icon()).isNotEmpty(),
                () -> assertThat(weather.main().temp()).isPositive(),
                () -> assertThat(weather.main().feelsLike()).isPositive(),
                () -> assertThat(weather.main().tempMin()).isPositive(),
                () -> assertThat(weather.main().tempMax()).isPositive(),
                () -> assertThat(weather.main().pressure()).isPositive(),
                () -> assertThat(weather.main().humidity()).isPositive(),
                () -> assertThat(weather.visibility()).isPositive(),
                () -> assertThat(weather.wind().speed()).isPositive(),
                () -> assertThat(weather.wind().deg()).isPositive(),
                () -> assertThat(weather.dt()).isPositive(),
                () -> assertThat(weather.sys().sunrise()).isPositive(),
                () -> assertThat(weather.sys().sunset()).isPositive(),
                // () -> assertThat(weather.timezone()).isPositive(),
                () -> assertThat(weather.id()).isPositive(),
                () -> assertThat(weather.name()).isNotEmpty(),
                () -> assertThat(weather.cod()).isPositive()
        );
    }

    @Test
    void getShortWeather() {
        // Boston, MA
        var shortWeather = openWeatherMap.getShortWeather(42.3601, -71.0589);
        assertAll(
                () -> assertThat(shortWeather.description()).isNotEmpty(),
                () -> assertThat(shortWeather.minTemp()).isPositive(),
                () -> assertThat(shortWeather.maxTemp()).isPositive(),
                () -> assertThat(shortWeather.temp()).isPositive()
        );
        System.out.println(shortWeather);
    }
}
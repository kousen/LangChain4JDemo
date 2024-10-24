package com.kousenit.tools;

import java.util.List;

public class OpenWeatherMapRecords {
    // For endpoint that returns lat/lng for a zip code
    public record Coordinates(String name, String country, double lat, double lon, String zip) {
    }

    // For endpoint that returns weather for a lat/lng
    public record WeatherResponse(
            Coord coord, List<Weather> weather, String base, Main main,
            int visibility, Wind wind, Rain rain, Clouds clouds,
            long dt, Sys sys, int timezone, int id, String name, int cod) {

        public record Coord(double lon, double lat) { }

        public record Weather(int id, String main, String description, String icon) { }

        public record Main(
                double temp, double feelsLike, double tempMin, double tempMax,
                int pressure, int humidity, Integer seaLevel, Integer grndLevel) {
        }

        public record Wind(double speed, int deg, double gust) { }

        public record Rain(double _1h) { }

        public record Clouds(int all) { }

        public record Sys(int type, int id, String country, long sunrise, long sunset) { }
    }

    public record ShortWeather(String description, double minTemp, double maxTemp, double temp) {}
}

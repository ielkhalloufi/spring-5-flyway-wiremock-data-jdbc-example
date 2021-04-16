package com.assignment.spring.fixtures;

import com.assignment.spring.services.weather.repository.WeatherEntity;

import java.time.LocalDateTime;

public class WeatherEntityFixtures {

    public static WeatherEntity create(final String city,
                                       final String country,
                                       final double temperature,
                                       final LocalDateTime lastUpdated) {
        final var weather = new WeatherEntity();
        weather.setCity(city);
        weather.setCountry(country);
        weather.setTemperature(temperature);
        weather.setLastUpdated(lastUpdated);
        return weather;
    }
}

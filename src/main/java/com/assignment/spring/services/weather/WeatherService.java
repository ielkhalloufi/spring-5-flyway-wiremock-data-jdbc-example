package com.assignment.spring.services.weather;

import io.vavr.control.Try;

import java.util.Optional;
import java.util.Set;

public interface WeatherService {
    Try<Optional<WeatherData>> getWeatherDataByCityName(final String cityName);

    Set<WeatherData> updateWeatherData(final Integer limit);
}

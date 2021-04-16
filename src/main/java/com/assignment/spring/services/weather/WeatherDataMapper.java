package com.assignment.spring.services.weather;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.services.weather.repository.WeatherEntity;

public class WeatherDataMapper {

    public static WeatherData toWeatherData(final WeatherResponse response) {
        return new WeatherData(response.getName(), response.getSys().getCountry(), response.getMain().getTemp());
    }

    public static WeatherData toWeatherData(final WeatherEntity entity) {
        return new WeatherData(entity.getCity(), entity.getCountry(), entity.getTemperature());
    }
}

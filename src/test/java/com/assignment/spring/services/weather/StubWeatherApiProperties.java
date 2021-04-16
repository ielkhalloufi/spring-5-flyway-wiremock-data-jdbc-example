package com.assignment.spring.services.weather;

import com.assignment.spring.WeatherApiProperties;

class StubWeatherApiProperties extends WeatherApiProperties {

    @Override
    public String getUrl() {
        return "http://localhost:9090/weather";
    }

    @Override
    public String getAppid() {
        return "dummy";
    }
}
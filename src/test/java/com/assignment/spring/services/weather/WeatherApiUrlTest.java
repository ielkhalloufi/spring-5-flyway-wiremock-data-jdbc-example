package com.assignment.spring.services.weather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherApiUrlTest {

    @Test
    public void testCreateCityName() {
        final String url = WeatherApiUrl.createUrl("http://api.openweathermap.org/data/2.5/weather", "appId", "amsterdam");
        assertEquals("http://api.openweathermap.org/data/2.5/weather?q=amsterdam&APPID=appId", url);
    }
}
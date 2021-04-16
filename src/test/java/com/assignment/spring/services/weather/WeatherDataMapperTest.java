package com.assignment.spring.services.weather;

import com.assignment.spring.StubClock;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.fixtures.WeatherEntityFixtures;
import com.assignment.spring.services.weather.repository.WeatherEntity;
import org.junit.jupiter.api.Test;

import static com.assignment.spring.fixtures.WeatherResponseFixture.create;
import static com.assignment.spring.services.weather.WeatherDataMapper.toWeatherData;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherDataMapperTest {

    @Test
    void testResponseToWeatherData() {
        final WeatherResponse weatherResponse = create("Amsterdam", "NL", 277.14);
        assertEquals(new WeatherData("Amsterdam", "NL", 277.14), toWeatherData(weatherResponse));
    }

    @Test
    void testEntityToWeatherData() {
        final WeatherEntity entity = WeatherEntityFixtures.create("Amsterdam", "NL", 277.14, new StubClock().nowDateTime());
        assertEquals(new WeatherData("Amsterdam", "NL", 277.14), toWeatherData(entity));
    }
}
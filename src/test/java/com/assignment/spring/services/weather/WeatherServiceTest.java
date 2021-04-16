package com.assignment.spring.services.weather;

import com.assignment.spring.StubClock;
import com.assignment.spring.WeatherApiProperties;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.fixtures.WeatherEntityFixtures;
import com.assignment.spring.fixtures.WeatherResponseFixture;
import com.assignment.spring.services.weather.impl.WeatherServiceImpl;
import com.assignment.spring.services.weather.repository.WeatherEntity;
import com.assignment.spring.services.weather.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WeatherServiceTest {

    private WeatherRepository repository;
    private final WeatherApiProperties apiProperties = new StubWeatherApiProperties();
    private RestTemplate restTemplate;
    private WeatherService readService;
    private StubClock stubClock = new StubClock();

    @BeforeEach
    public void afterEach() {
        this.repository = mock(WeatherRepository.class);
        this.restTemplate = mock(RestTemplate.class);
        this.readService = new WeatherServiceImpl(repository, apiProperties, restTemplate, stubClock);
    }

    @Test
    void testGetWeatherDataByCityNameExists() {
        // given
        final var cityName = "Amsterdam";
        final var entity = weatherEntity(cityName);

        // when
        when(repository.findByCity(cityName)).thenReturn(Optional.of(entity));
        final var result = readService.getWeatherDataByCityName(cityName);

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isPresent());
        verify(restTemplate, never()).getForObject(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void testGetWeatherDataByCityNameRepositoryNotExisting() {
        // given
        final var cityName = "Amsterdam";
        final var response = weatherResponse(cityName);

        // when
        when(repository.findByCity(cityName)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), ArgumentMatchers.<Class<WeatherResponse>>any())).thenReturn(response);
        when(repository.save(any())).thenReturn(weatherEntity(cityName));

        final var result = readService.getWeatherDataByCityName(cityName);

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isPresent());
        verify(repository, times(1)).save(any());
    }

    @Test
    void testGetWeatherDataByCityNameWithException() {
        // given
        final var cityName = "Amsterdam";

        // when
        when(repository.findByCity(cityName)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), ArgumentMatchers.<Class<WeatherResponse>>any())).thenThrow(new RestClientException(""));
        final var result = readService.getWeatherDataByCityName(cityName);

        // then
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void testGetWeatherDataByCityNameWithNotFoundException() {
        // given
        final var cityName = "Amsterdam";

        // when
        when(repository.findByCity(cityName)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), ArgumentMatchers.<Class<WeatherResponse>>any())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final var result = readService.getWeatherDataByCityName(cityName);

        // then
        assertTrue(result.isSuccess());
        assertTrue(result.get().isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateWeatherData() {
        // given
        final var cityName = "Amsterdam";
        final var responseTemperature = 266.3;
        final var response = weatherResponse(cityName);
        final var entity = weatherEntity(cityName);

        // when
        when(repository.getWeatherDataAsc(any())).thenReturn(Set.of(entity));
        when(restTemplate.getForObject(anyString(), ArgumentMatchers.<Class<WeatherResponse>>any())).thenReturn(response);
        when(repository.save(any())).thenReturn(entity);

        final var result = readService.updateWeatherData(1);

        // then
        assertFalse(result.isEmpty());
        assertEquals(responseTemperature, result.stream().findFirst().get().getTemperature());
        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateWeatherDataWithException() {
        // given
        final var cityName = "Amsterdam";
        final var temperature = 277.2;
        final var entity = weatherEntity(cityName);

        // when
        when(repository.getWeatherDataAsc(any())).thenReturn(Set.of(entity));
        when(restTemplate.getForObject(anyString(), ArgumentMatchers.<Class<WeatherResponse>>any())).thenThrow(new RestClientException(""));
        final var result = readService.updateWeatherData(1);

        // then
        assertFalse(result.isEmpty());
        assertEquals(temperature, result.stream().findFirst().get().getTemperature());
        verify(repository, never()).save(any());
    }

    private WeatherResponse weatherResponse(String cityName) {
        return WeatherResponseFixture.create(cityName, "NL", 266.3);
    }

    private WeatherEntity weatherEntity(String cityName) {
        return WeatherEntityFixtures.create(cityName, "NL", 277.2, stubClock.nowDateTime());
    }
}

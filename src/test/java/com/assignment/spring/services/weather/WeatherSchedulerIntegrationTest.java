package com.assignment.spring.services.weather;

import com.assignment.spring.StubClock;
import com.assignment.spring.WeatherApiProperties;
import com.assignment.spring.WireMockInitializer;
import com.assignment.spring.services.weather.repository.WeatherRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;
import java.util.stream.Collectors;

import static com.assignment.spring.fixtures.WeatherEntityFixtures.create;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Import({StubClock.class})
@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherSchedulerIntegrationTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private StubClock clock;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private WeatherApiProperties properties;

    @BeforeEach
    public void setup() {
        properties.setUrl("http://localhost:" + wireMockServer.port() + "/weather");
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
        this.weatherRepository.deleteAll();
    }

    @Test
    void testUpdateWeatherRecords() {
        // given
        final var temperature = 255.3;
        prepareRepositoryData(temperature);

        prepareGetWeatherDataDenhaag();
        prepareGetWeatherDataZaandam();
        prepareGetWeatherDataRotterdam();

        // when
        Set<WeatherData> resultWeatherData = weatherService.updateWeatherData(3);

        final var result = resultWeatherData.stream()
                .filter(weatherData -> weatherData.getTemperature().equals(temperature))
                .collect(Collectors.toSet());

        // then
        // empty as all temperatures were updated
        assertTrue(result.isEmpty());
    }

    @Test
    void testMissingUpdateWeatherRecords() {
        // given
        final var temperature = 255.3;
        prepareRepositoryData(temperature);
        prepareGetWeatherDataDenhaag();
        prepareGetWeatherDataZaandam();
        // no preparation for rotterdam

        // when
        Set<WeatherData> resultWeatherData = weatherService.updateWeatherData(3);

        final var result = resultWeatherData.stream()
                .filter(weatherData -> weatherData.getTemperature().equals(temperature))
                .collect(Collectors.toSet());

        // then
        // not empty as the temperature of rotterdam is not changed
        assertEquals(1, result.size());
    }

    private void prepareRepositoryData(final double temperature) {
        weatherRepository.save(create("Den Haag", "NL", temperature, clock.nowDateTime().minusSeconds(4)));
        weatherRepository.save(create("Zaandam", "NL", temperature, clock.nowDateTime().minusSeconds(3)));
        weatherRepository.save(create("Rotterdam", "NL", temperature, clock.nowDateTime().minusSeconds(2)));
    }

    private void prepareGetWeatherDataDenhaag() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=Den%20Haag&APPID=dummy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBodyFile("weather_response_denhaag.json")));
    }

    private void prepareGetWeatherDataZaandam() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=Zaandam&APPID=dummy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBodyFile("weather_response_zaandam.json")));
    }

    private void prepareGetWeatherDataRotterdam() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=Rotterdam&APPID=dummy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBodyFile("weather_response_rotterdam.json")));
    }
}
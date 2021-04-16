package com.assignment.spring;

import com.assignment.spring.services.weather.WeatherData;
import com.assignment.spring.services.weather.repository.WeatherRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherIntegrationTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private WeatherRepository weatherRepository;

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
    public void testGetWeatherData() {
        prepareGetWeatherData();

        final WeatherData weatherData = webClient.get()
                .uri("/weather?city=Amsterdam")
                .exchange()
                .expectBody(WeatherData.class)
                .returnResult()
                .getResponseBody();

        assertEquals("Amsterdam", weatherData.getCity());
        assertEquals("NL", weatherData.getCountry());
        assertEquals(277.14, weatherData.getTemperature());
    }

    @Test
    public void testGetWeatherDataNotFound() {
        prepareGetWeatherDataCityNotFound();

        webClient.get()
                .uri("/weather?city=not_found")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{\"message\":\"city could not be found\"}");
    }

    @Test
    public void testUnauthorizedWeatherData() {
        prepareGetWeatherDataUnauthorized();

        webClient.get()
                .uri("/weather?city=Amsterdam")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    private void prepareGetWeatherDataUnauthorized() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=Amsterdam&APPID=dummy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withStatus(401)));
    }

    private void prepareGetWeatherData() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=Amsterdam&APPID=dummy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBodyFile("weather_response_amsterdam.json")));
    }

    private void prepareGetWeatherDataCityNotFound() {
        wireMockServer.stubFor(get(urlEqualTo("/weather?q=not_found&APPID=dummy"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBodyFile("weather_response_city_not_found.json")));
    }

}

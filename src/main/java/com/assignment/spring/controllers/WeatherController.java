package com.assignment.spring.controllers;

import com.assignment.spring.services.weather.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    public WeatherController(final WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getWeatherData(@RequestParam("city") String city) {
        return weatherService.getWeatherDataByCityName(city)
                .map(weatherData -> {
                    if (weatherData.isPresent()) {
                        logger.info("Retrieved succesfully city data of {} from API", city);
                        return new ResponseEntity<>(weatherData.get(), HttpStatus.OK);
                    } else {
                        logger.info("Could not find city with cityName {}", city);
                        return new ResponseEntity<>(
                                new WeatherDataDataResponse("city could not be found"),
                                HttpStatus.OK
                        );
                    }
                }).getOrElseGet(ex ->
                        new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
                );
    }


    public class WeatherDataDataResponse {
        private String message;

        public WeatherDataDataResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

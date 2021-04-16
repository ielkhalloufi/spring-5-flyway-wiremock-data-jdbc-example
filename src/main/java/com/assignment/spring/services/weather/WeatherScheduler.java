package com.assignment.spring.services.weather;

import com.assignment.spring.WeatherSchedulerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WeatherScheduler {

    final Logger logger = LoggerFactory.getLogger(WeatherScheduler.class);

    final private WeatherSchedulerProperties weatherSchedulerProperties;
    final private WeatherService weatherService;

    public WeatherScheduler(WeatherSchedulerProperties weatherSchedulerProperties,
                            WeatherService weatherService) {
        this.weatherSchedulerProperties = weatherSchedulerProperties;
        this.weatherService = weatherService;
    }

    @Scheduled(
            fixedDelayString = "#{@weatherSchedulerProperties.delayMillis}",
            initialDelayString = "#{@weatherSchedulerProperties.initialDelayMillis}"
    )
    public void updateWeatherRecords() {
        if (weatherSchedulerProperties.isEnabled()) {
            logger.info("Updating weather data...");
            weatherService.updateWeatherData(weatherSchedulerProperties.getLimit());
            logger.info("Done updating");
        } else {
            logger.info("WeatherScheduler is disabled, NOT running.");
        }

    }

}

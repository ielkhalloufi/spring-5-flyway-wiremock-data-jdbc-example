package com.assignment.spring.services.weather.impl;

import com.assignment.spring.Clock;
import com.assignment.spring.WeatherApiProperties;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.services.weather.WeatherData;
import com.assignment.spring.services.weather.WeatherDataMapper;
import com.assignment.spring.services.weather.WeatherService;
import com.assignment.spring.services.weather.repository.WeatherEntity;
import com.assignment.spring.services.weather.repository.WeatherRepository;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.assignment.spring.services.weather.WeatherApiUrl.createUrl;
import static com.assignment.spring.services.weather.WeatherDataMapper.toWeatherData;

@Service
public class WeatherServiceImpl implements WeatherService {

    final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private final WeatherRepository weatherRepository;
    private final WeatherApiProperties weatherApiProperties;
    private final RestTemplate restTemplate;
    private final Clock clock;

    public WeatherServiceImpl(WeatherRepository weatherRepository,
                              WeatherApiProperties weatherApiProperties,
                              RestTemplate restTemplate,
                              Clock clock) {
        this.weatherRepository = weatherRepository;
        this.weatherApiProperties = weatherApiProperties;
        this.restTemplate = restTemplate;
        this.clock = clock;
    }

    @Override
    public Try<Optional<WeatherData>> getWeatherDataByCityName(final String cityName) {
        final var resultOpt = weatherRepository.findByCity(cityName)
                .map(WeatherDataMapper::toWeatherData);

        if (resultOpt.isPresent()) {
            return Try.success(resultOpt);
        } else {
            final var result = getWeatherData(cityName);
            return result.map(weatherDataOptional -> weatherDataOptional.map(w -> weatherRepository.save(newEntity(w.getCity(), w.getCountry(), w.getTemperature(), clock.nowDateTime())))
                    .map(WeatherDataMapper::toWeatherData));
        }
    }

    @Override
    public Set<WeatherData> updateWeatherData(final Integer limit) {
        return weatherRepository.getWeatherDataAsc(limit)
                .stream()
                .map(this::updateApiWeatherData)
                .collect(Collectors.toSet());
    }

    private Try<Optional<WeatherData>> getWeatherData(String cityName) {
        final String url = createUrl(weatherApiProperties.getUrl(), weatherApiProperties.getAppid(), cityName);

        final var result = Try.of(() ->
                Optional.ofNullable(restTemplate.getForObject(url, WeatherResponse.class))
                        .map(WeatherDataMapper::toWeatherData)
        );

        if (result.isSuccess()) return Try.success(result.get());
        else {
            if (result.getCause() instanceof HttpClientErrorException) {
                var cause = (HttpClientErrorException) result.getCause();
                if (cause.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    logger.error("City with city name {} could not be found", cityName, result.getCause());
                    return Try.success(Optional.empty());
                } else if (cause.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                    logger.error("Call to weather API is unauthorized, check your API key", result.getCause());
                    return Try.failure(result.getCause());
                }
            }
            logger.error("Something went wrong when calling the weather API with city parameter {}", cityName, result.getCause());
            return Try.failure(result.getCause());
        }
    }

    private WeatherData updateApiWeatherData(final WeatherEntity entity) {
        final var result = getWeatherData(entity.getCity());

        var weatherDataTry = result.map(weatherDataOptional -> weatherDataOptional
                .map(w -> toWeatherData(updateEntity(entity, w.getTemperature(), clock.nowDateTime())))
                .orElseGet(() -> {
                    logger.error("Could not find weather data for an already existing city {}, returning old data..", entity.getCity());
                    return toWeatherData(entity);
                }));

        return weatherDataTry.isSuccess() ? weatherDataTry.get() : toWeatherData(entity);
    }

    private WeatherEntity newEntity(final String city,
                                    final String countryCode,
                                    final double temperature,
                                    final LocalDateTime lastUpdated) {
        final var entity = new WeatherEntity();
        entity.setCity(city);
        entity.setCountry(countryCode);
        entity.setTemperature(temperature);
        entity.setLastUpdated(lastUpdated);
        return entity;
    }

    private WeatherEntity updateEntity(final WeatherEntity entity,
                                       final double temperature,
                                       final LocalDateTime lastUpdated) {
        entity.setTemperature(temperature);
        entity.setLastUpdated(lastUpdated);
        return weatherRepository.save(entity);
    }
}

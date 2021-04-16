package com.assignment.spring.services.weather.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {

    Optional<WeatherEntity> findByCity(final String cityName);

    @Query(nativeQuery = true, value = "select * from weather order by last_updated asc limit :size")
    Set<WeatherEntity> getWeatherDataAsc(@Param("size") Integer size);
}

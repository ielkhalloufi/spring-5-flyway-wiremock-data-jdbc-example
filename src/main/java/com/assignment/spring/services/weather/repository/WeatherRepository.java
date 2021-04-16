package com.assignment.spring.services.weather.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface WeatherRepository extends CrudRepository<WeatherEntity, Integer> {

    Optional<WeatherEntity> findByCity(final String cityName);

    @Query("select * from weather order by last_updated asc limit :size")
    Set<WeatherEntity> getWeatherDataAsc(@Param("size") Integer size);
}

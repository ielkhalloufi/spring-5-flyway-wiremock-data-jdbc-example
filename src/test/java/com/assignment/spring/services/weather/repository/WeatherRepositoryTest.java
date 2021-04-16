package com.assignment.spring.services.weather.repository;

import com.assignment.spring.StubClock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.assignment.spring.fixtures.WeatherEntityFixtures.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJdbcTest
@Import({StubClock.class})
class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private StubClock clock;

    @Test
    void testFindByCity() {
        weatherRepository.save(create("Amsterdam", "NL", 266.2, clock.nowDateTime()));
        final var result = weatherRepository.findByCity("Amsterdam");
        assertTrue(result.isPresent());
    }

    @Test
    void testGetWeatherDataAsc() {
        final var amsterdam = weatherRepository.save(create("Amsterdam", "NL", 266.2, clock.nowDateTime()));
        final var rotterdam = weatherRepository.save(create("Rotterdam", "NL", 255.3, clock.nowDateTime().plusMinutes(1)));
        weatherRepository.save(create("Den Haag", "NL", 255.3, clock.nowDateTime().plusHours(1)));

        final var result = weatherRepository.getWeatherDataAsc(2);

        assertEquals(result.size(), 2);
        assertTrue(result.contains(amsterdam));
        assertTrue(result.contains(rotterdam));
    }
}
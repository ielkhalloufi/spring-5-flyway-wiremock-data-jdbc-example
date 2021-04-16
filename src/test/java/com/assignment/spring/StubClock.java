package com.assignment.spring;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Profile("test")
@Component
public class StubClock extends Clock {

    private final LocalDate nowDate = LocalDate.of(2020, 4, 1);
    private final LocalTime nowTime = LocalTime.of(0, 0);
    private LocalDateTime nowDateTime = LocalDateTime.of(nowDate, nowTime);

    @Override
    public LocalDateTime nowDateTime() {
        return nowDateTime;
    }

}

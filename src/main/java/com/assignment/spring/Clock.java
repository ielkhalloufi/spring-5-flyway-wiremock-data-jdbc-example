package com.assignment.spring;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class Clock {

    public static final ZoneOffset ZONE = ZoneOffset.UTC;

    public LocalDateTime nowDateTime() {
        return LocalDateTime.now(ZONE);
    }

}

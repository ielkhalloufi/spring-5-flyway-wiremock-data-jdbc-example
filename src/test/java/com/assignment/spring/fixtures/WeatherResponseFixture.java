package com.assignment.spring.fixtures;

import com.assignment.spring.api.Main;
import com.assignment.spring.api.Sys;
import com.assignment.spring.api.WeatherResponse;

public class WeatherResponseFixture {

    public static WeatherResponse create(final String city,
                                         final String countryCode,
                                         final double temperature) {
        final Sys sys = new Sys();
        sys.setCountry(countryCode);
        final Main main = new Main();
        main.setTemp(temperature);
        final WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setName(city);
        weatherResponse.setSys(sys);
        weatherResponse.setMain(main);
        return weatherResponse;
    }
}

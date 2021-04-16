package com.assignment.spring.services.weather;

public class WeatherApiUrl {

    public static String createUrl(final String url, final String appId, final String city) {
        return url + "?q=" + city + "&APPID=" + appId;
    }
}

package com.assignment.spring.services.weather;

import java.util.Objects;

public class WeatherData {

    private String city;

    private String country;

    private Double temperature;

    public WeatherData(String city, String country, Double temperature) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public Double getTemperature() {
        return temperature;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Objects.equals(city, that.city) && Objects.equals(country, that.country) && Objects.equals(temperature, that.temperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country, temperature);
    }
}

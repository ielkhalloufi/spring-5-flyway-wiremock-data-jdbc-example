Spring Boot 2 + Spring 5 + Flyway + Wiremock + Data JDBC
---

A simple webapp which connects to the [openweathermap](https://openweathermap.org/) to retrieve weather data. You can get an API key from the website.

## How to run

Add your own api key.

`mvnw spring-boot:run -Dspring-boot.run.arguments=--weather-api.appid=xxxxxxxxxxxxxx`

## Get weather by city name

Execute GET request to retrieve city weather data:

`http://localhost:8080/weather?city=Amsterdam`

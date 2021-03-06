CREATE TABLE weather
(
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    city         VARCHAR(255) NOT NULL,
    country      VARCHAR(255) NOT NULL,
    temperature  NUMERIC(5, 2),
    last_updated TIMESTAMP    NOT NULL
);

CREATE INDEX weather_city_idx ON weather (city);
CREATE INDEX weather_last_updated_idx ON weather (last_updated);
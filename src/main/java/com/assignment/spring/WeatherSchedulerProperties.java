package com.assignment.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(prefix = "weather-scheduler")
public class WeatherSchedulerProperties {

    @NotNull
    private Boolean enabled;

    @NotNull
    private String delayMillis;

    @NotNull
    private String initialDelayMillis;

    @NotNull
    private Integer limit;

    public Boolean isEnabled() {
        return enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(String delayMillis) {
        this.delayMillis = delayMillis;
    }

    public String getInitialDelayMillis() {
        return initialDelayMillis;
    }

    public void setInitialDelayMillis(String initialDelayMillis) {
        this.initialDelayMillis = initialDelayMillis;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
package com.theduc.weatherapplication;

public class WeatherInfo {
    private String title;
    private String value;

    public WeatherInfo(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
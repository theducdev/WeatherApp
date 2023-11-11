package com.theduc.weatherapplication;

public class WeatherRVModal {
    private String time, temperature, icon, wingSpeed;

    public WeatherRVModal(String time, String temperature, String icon, String wingSpeed) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.wingSpeed = wingSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWingSpeed() {
        return wingSpeed;
    }

    public void setWingSpeed(String wingSpeed) {
        this.wingSpeed = wingSpeed;
    }
}

package com.example.alee7.soft254_weather_app.backend;

import com.example.alee7.soft254_weather_app.enumerator.WeatherType;
import com.example.alee7.soft254_weather_app.enumerator.WindDirection;

public class RecordItem {
    private double feelsLike;
    private WeatherType weatherType;
    private WindDirection windDirection;
    private double windSpeed, localPressure;
    private double latitude, longitude;


    private RecordItem(){
        //Blank Constructor to serialise
    }

    //Constructor
    public RecordItem(double feelsLike, WeatherType weatherType, WindDirection windDirection, double windSpeed, double localPressure, double latitude, double longitude) {
        this.feelsLike = feelsLike;
        this.weatherType = weatherType;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.localPressure = localPressure;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    //Getters
    public double getFeelsLike() {
        return feelsLike;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public WindDirection getWindDirection() {
        return windDirection;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getLocalPressure() {
        return localPressure;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
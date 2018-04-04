package com.example.alee7.soft254_weather_app.backend;

import com.example.alee7.soft254_weather_app.enumerator.WeatherType;
import com.example.alee7.soft254_weather_app.enumerator.WindDirection;

public class RecordItem {
    private double feelsLike;
    private WeatherType weatherType;
    private WindDirection windDirection;
    private double windSpeed;
    private double localPressure;
    private double recordedTemp;

    private RecordItem(){
        //Blank Constructor to serialise
    }

    //Constructor
    private RecordItem(double feelsLike, WeatherType weatherType, WindDirection windDirection, double windSpeed, double localPressure, double recordedTemp) {
        this.feelsLike = feelsLike;
        this.weatherType = weatherType;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.localPressure = localPressure;
        this.recordedTemp = recordedTemp;
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

    public double getRecordedTemp() {
        return recordedTemp;
    }
}
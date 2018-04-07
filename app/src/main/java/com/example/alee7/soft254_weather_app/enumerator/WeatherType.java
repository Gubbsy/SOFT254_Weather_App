package com.example.alee7.soft254_weather_app.enumerator;

public enum WeatherType {
    SUNNY("Sunny", 0),
    OVERCAST ("Overcast", 1),
    LIGHT_RAIN("Light Rain", 2),
    HEAVY_RAIN("Heavy Rain", 3),
    THUNDER_STORM("Thunder Storm", 4),
    SNOW("Snow", 5),
    SLEET("Sleet", 6),
    HAIL("Hail", 7);

    private String weatherTypeString;
    private int position;

    private WeatherType(String weatherTypeString, int position){
        this.weatherTypeString = weatherTypeString;
        this.position = position;
    }

    @Override public String toString(){
        return weatherTypeString;
    }

    public static WeatherType getEnumByIndex(int code){
        for(WeatherType wt : WeatherType.values()){
            if(code == wt.position)
                return wt;
        }
        return null;
    }
}



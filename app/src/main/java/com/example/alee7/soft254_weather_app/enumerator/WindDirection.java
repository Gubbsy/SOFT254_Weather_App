package com.example.alee7.soft254_weather_app.enumerator;

public enum WindDirection {
    N("N", 0),
    NE("NE", 1),
    E("E", 2),
    SE("SE", 3),
    S("S", 4),
    SW("SW", 5),
    W("W", 6),
    NW("NW", 7);

    private String windDirectionString;
    private int position;

    private WindDirection(String weatherTypeString, int position){
        this.windDirectionString = weatherTypeString;
        this.position = position;
    }

    @Override public String toString(){
        return windDirectionString;
    }

    public static WindDirection getEnumByIndex(int code){
        for(WindDirection wd : WindDirection.values()){
            if(code == wd.position)
                return wd;
        }
        return null;
    }

    public int getPosition() {
        return position;
    }
}

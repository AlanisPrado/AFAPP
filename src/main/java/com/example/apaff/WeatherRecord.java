package com.example.apaff;

public class WeatherRecord {

    public String id;
    public double latitude;
    public double longitude;
    public double temperature;
    public double windspeed;
    public int weathercode;
    public String note; 

    public WeatherRecord() {}

    public WeatherRecord(double latitude, double longitude,
                         double temperature, double windspeed,
                         int weathercode) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.weathercode = weathercode;
        this.note = ""; 
    }
}
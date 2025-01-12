package com.example.hospitals.data;

public class Hospitals {
    private String name;  // Name of the hospital
    private double lat;   // Latitude
    private double lang;  // Longitude
    private Department airDap;  // Department class (referencing another class)

    // Constructor
    public Hospitals(String name, double lat, double lang, Department airDap) {
        this.name = name;
        this.lat = lat;
        this.lang = lang;
        this.airDap = airDap;
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public Department getAirDap() {
        return airDap;
    }

    public void setAirDap(Department airDap) {
        this.airDap = airDap;
    }
}

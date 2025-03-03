package com.example.hospitals.data;

import java.util.List;

public class Hospitals {
    private String name;        // Name of the hospital
    private double lat;         // Latitude
    private double lang;        // Longitude
    private String description;
    private String subtitle;
    private String address;     // Changed from "Address" to "address" for consistency
    private String image;
    private List<Department> airDap; // Changed from Department[] to List<Department>

    // No-argument constructor required for Firebase
    public Hospitals() { }

    public Hospitals(String name, double lat, double lang, String description, String subtitle, String address, String image, List<Department> airDap) {
        this.name = name;
        this.lat = lat;
        this.lang = lang;
        this.description = description;
        this.subtitle = subtitle;
        this.address = address;
        this.image = image;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Department> getAirDap() {
        return airDap;
    }

    public void setAirDap(List<Department> airDap) {
        this.airDap = airDap;
    }
}

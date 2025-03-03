package com.example.hospitals.data;

import java.util.List;

public class Department {
    private String name;
    private List<Appointment> arrAp; // Changed from Appointment[] to List<Appointment>

    // No-argument constructor
    public Department() { }

    public Department(String name, List<Appointment> arrAp) {
        this.name = name;
        this.arrAp = arrAp;
    }

    // Getter and setter methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Appointment> getArrAp() {
        return arrAp;
    }

    public void setArrAp(List<Appointment> arrAp) {
        this.arrAp = arrAp;
    }
}

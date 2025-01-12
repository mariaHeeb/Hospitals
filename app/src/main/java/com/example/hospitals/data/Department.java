package com.example.hospitals.data;

public class Department {
    private String name;  // Name of the department
    private Appointment arrAp;  // Appointment class (referencing another class)

    // Constructor
    public Department(String name, Appointment arrAp) {
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

    public Appointment getArrAp() {
        return arrAp;
    }

    public void setArrAp(Appointment arrAp) {
        this.arrAp = arrAp;
    }
}

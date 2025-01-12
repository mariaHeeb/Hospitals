package com.example.hospitals.data;
import java.time.LocalDate;

public class Appointment {
    private String userId;  // ID of the user (patient)
    private LocalDate date;      // Date of the appointment

    // Constructor
    public Appointment(String userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    // Getter and setter methods
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}


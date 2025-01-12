package com.example.hospitals.data;

public class MedicalHistory {
    private String condition;
    private String treatment;

    // Constructor
    public MedicalHistory(String condition, String treatment) {
        this.condition = condition;
        this.treatment = treatment;
    }

    // Getter and setter methods
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

}

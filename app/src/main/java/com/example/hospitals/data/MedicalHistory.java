package com.example.hospitals.data;

public class MedicalHistory {
    private String treatment;  // Treatment for the condition
    private String illnesses;  // List of illnesses (you can also consider using an array or list)
    private String allergies;  // List of allergies (you can also consider using an array or list)

    // Constructor

    public MedicalHistory(){}
    public MedicalHistory(String treatment, String illnesses, String allergies) {
        this.treatment = treatment;
        this.illnesses = illnesses;
        this.allergies = allergies;
    }

    // Getter and setter methods
    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getIllnesses() {
        return illnesses;
    }

    public void setIllnesses(String illnesses) {
        this.illnesses = illnesses;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}

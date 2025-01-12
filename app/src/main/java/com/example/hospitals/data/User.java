package com.example.hospitals.data;

public class User {
    private String id;
    private String name;
    private String phoneNumber;
    private String bloodType;
    private int age;
    private String gmail;
    private MedicalHistory mH;  // MedicalHistory is a separate class

    // Constructor
    public User(String id, String name, String phoneNumber, String bloodType, String gmail, MedicalHistory mH) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.gmail = gmail;
        this.mH = mH;
        this.age = calculateAge();  // Assuming calculateAge() is a method that calculates the user's age based on their birthdate
    }

    // Getter and setter methods for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public MedicalHistory getMH() {
        return mH;
    }

    public void setMH(MedicalHistory mH) {
        this.mH = mH;
    }

    // Method to calculate age (assuming birthdate is provided or calculated in another way)
    private int calculateAge() {
        // Logic to calculate age based on the birth date (needs birthdate to work)
        // For now, returning a placeholder value
        return 25;  // Placeholder, replace with actual age calculation
    }
}

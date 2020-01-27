package com.example.hyperionapp;

import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

public class PatientDetails extends ViewModel {

    //Personal Details
    String name;
    String email;
    Date dateOfBirth;
    String address;
    String address2;
    String city;
    String postCode;
    String PPSNumber;
    String insurance;

    //Medical Details
    String bloodType;
    String allergies;
    String otherConditions;
    String medications;
    float height;
    float weight;
    String registeredGP;

    //Medical Conditions Checkboxes
    Boolean tubercolosis;
    Boolean diabetes;
    Boolean heartCondition;
    Boolean gloucoma;
    Boolean epilepsy;
    Boolean drugAlcoholAbuse;
    Boolean smoker;
    Boolean cancer;


    //Patient Records
    List<PatientRecord> patientRecords;

    public PatientDetails() { }

    public void setPersonalDetails(
            String name, String email, Date dateOfBirth, String address, String address2,
            String city, String postCode, String PPSNumber, String insurance
    ){
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.postCode = postCode;
        this.PPSNumber = PPSNumber;
        this. insurance = insurance;
    }

    public void setMedicalDetails(
            String bloodType, String allergies, String otherConditions, String medications,
            float height, float weight, String registeredGP
    ){
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.otherConditions = otherConditions;
        this.medications = medications;
        this.height = height;
        this.weight = weight;
        this.registeredGP = registeredGP;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getOtherConditions() {
        return otherConditions;
    }

    public void setOtherConditions(String otherConditions) {
        this.otherConditions = otherConditions;
    }

    public void setPatientRecords(List<PatientRecord> patientRecords) {
        this.patientRecords = patientRecords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPPSNumber() {
        return PPSNumber;
    }

    public void setPPSNumber(String PPSNumber) {
        this.PPSNumber = PPSNumber;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getRegisteredGP() {
        return registeredGP;
    }

    public void setRegisteredGP(String registeredGP) {
        this.registeredGP = registeredGP;
    }

    public List<PatientRecord> getPatientRecords() {
        return patientRecords;
    }
}

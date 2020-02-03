package com.example.hyperionapp;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

public class PatientDetailsCopy extends ViewModel {

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
    String strHeight;
    String strWidth;
    double height;
    double weight;
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

    public PatientDetailsCopy() {
        //Personal Details
        name = "";
        email = "";
        dateOfBirth = null;
        address = "";
        address2 = "";
        city = "";
        postCode = "";
        PPSNumber = "";
        insurance = "";
        strHeight = "0.0";
        strWidth = "0.0";
        //Medical Details
        bloodType = "";
        allergies = "";
        otherConditions = "";
        medications = "";
        height = 0.0;
        weight = 0.0;
        registeredGP = "";

        //Medical Conditions Checkboxes
        tubercolosis = Boolean.FALSE;
        diabetes = Boolean.FALSE;
        heartCondition = Boolean.FALSE;
        gloucoma = Boolean.FALSE;
        epilepsy = Boolean.FALSE;
        drugAlcoholAbuse = Boolean.FALSE;
        smoker = Boolean.FALSE;
        cancer = Boolean.FALSE;
        
    }

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
            String bloodType, String allergies,
            Boolean tubercolosis, Boolean diabetes, Boolean heartCondition, Boolean gloucoma,
            Boolean epilepsy, Boolean drugAlcoholAbuse, Boolean smoker, Boolean cancer,
            String otherConditions, String medications, double height, double weight,
            String registeredGP
    ){
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.tubercolosis = tubercolosis;
        this.diabetes = diabetes;
        this.heartCondition = heartCondition;
        this.gloucoma = gloucoma;
        this.epilepsy = epilepsy;
        this.drugAlcoholAbuse = drugAlcoholAbuse;
        this.smoker = smoker;
        this.cancer = cancer;
        this.otherConditions = otherConditions;
        this.medications = medications;
        this.height = height;
        this.weight = weight;
        this.registeredGP = registeredGP;
    }

    public String getName(){
        return name;
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


    public void setName(String name) {
        this.name  = name;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
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

    public Boolean getTubercolosis() {
        return tubercolosis;
    }

    public void setTubercolosis(Boolean tubercolosis) {
        this.tubercolosis = tubercolosis;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes = diabetes;
    }

    public Boolean getHeartCondition() {
        return heartCondition;
    }

    public void setHeartCondition(Boolean heartCondition) {
        this.heartCondition = heartCondition;
    }

    public Boolean getGloucoma() {
        return gloucoma;
    }

    public void setGloucoma(Boolean gloucoma) {
        this.gloucoma = gloucoma;
    }

    public Boolean getEpilepsy() {
        return epilepsy;
    }

    public void setEpilepsy(Boolean epilepsy) {
        this.epilepsy = epilepsy;
    }

    public Boolean getDrugAlcoholAbuse() {
        return drugAlcoholAbuse;
    }

    public void setDrugAlcoholAbuse(Boolean drugAlcoholAbuse) {
        this.drugAlcoholAbuse = drugAlcoholAbuse;
    }

    public Boolean getSmoker() {
        return smoker;
    }

    public void setSmoker(Boolean smoker) {
        this.smoker = smoker;
    }

    public Boolean getCancer() {
        return cancer;
    }

    public void setCancer(Boolean cancer) {
        this.cancer = cancer;
    }

    public String getStrHeight() {
        return strHeight;
    }

    public void setStrHeight(String strHeight) {
        this.strHeight = strHeight;
    }

    public String getStrWidth() {
        return strWidth;
    }

    public void setStrWidth(String strWidth) {
        this.strWidth = strWidth;
    }
}

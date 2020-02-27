package com.example.hyperionapp;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.List;

public class PatientDetails extends ViewModel {

    //Personal Details
    ObservableField<String> name;
    ObservableField<String> email;
    Date dateOfBirth;
    ObservableField<String> address;
    ObservableField<String> address2;
    ObservableField<String> city;
    ObservableField<String> postCode;
    ObservableField<String> PPSNumber;
    ObservableField<String> insurance;

    //Medical Details
    String bloodType;
    ObservableField<String> allergies;
    ObservableField<String> otherConditions;
    ObservableField<String> medications;
    ObservableField<String> height;
    ObservableField<String> weight;
    ObservableField<String> registeredGP;

    //Medical Conditions Checkboxes
    ObservableField<Boolean> tubercolosis;
    ObservableField<Boolean> diabetes;
    ObservableField<Boolean> heartCondition;
    ObservableField<Boolean> gloucoma;
    ObservableField<Boolean> epilepsy;
    ObservableField<Boolean> drugAlcoholAbuse;
    ObservableField<Boolean> smoker;
    ObservableField<Boolean> cancer;


    //Patient Records
    List<PatientRecord> patientRecords;

    public PatientDetails() {
        //Personal Details
        name = new ObservableField<>();
        email = new ObservableField<>();
        dateOfBirth = null;
        address = new ObservableField<>();
        address2 = new ObservableField<>();
        city = new ObservableField<>();
        postCode = new ObservableField<>();
        PPSNumber = new ObservableField<>();
        insurance = new ObservableField<>();
        //Medical Details
        bloodType = "";
        allergies = new ObservableField<>();
        otherConditions = new ObservableField<>();
        medications = new ObservableField<>();
        height = new ObservableField<>();
        weight = new ObservableField<>();
        registeredGP = new ObservableField<>();

        //Medical Conditions Checkboxes
        tubercolosis = new ObservableField<>();
        diabetes = new ObservableField<>();
        heartCondition = new ObservableField<>();
        gloucoma = new ObservableField<>();
        epilepsy = new ObservableField<>();
        drugAlcoholAbuse = new ObservableField<>();
        smoker = new ObservableField<>();
        cancer = new ObservableField<>();
        
    }

    public void setPersonalDetails(
            String name, String email, Date dateOfBirth, String address, String address2,
            String city, String postCode, String PPSNumber, String insurance
    ){
        this.name.set(name);
        this.email.set(email);
        this.dateOfBirth = dateOfBirth;
        this.address.set(address);
        this.address2 .set(address2);
        this.city.set(city);
        this.postCode.set(postCode);
        this.PPSNumber.set(PPSNumber);
        this. insurance.set(insurance);
    }

    public void setMedicalDetails(
            String bloodType, String allergies,
            Boolean tubercolosis, Boolean diabetes, Boolean heartCondition, Boolean gloucoma,
            Boolean epilepsy, Boolean drugAlcoholAbuse, Boolean smoker, Boolean cancer,
            String otherConditions, String medications, String height, String weight,
            String registeredGP
    ){
        this.bloodType = bloodType;
        this.allergies.set(allergies);
        this.tubercolosis.set(tubercolosis);
        this.diabetes.set(diabetes);
        this.heartCondition.set(heartCondition);
        this.gloucoma.set(gloucoma);
        this.epilepsy.set(epilepsy);
        this.drugAlcoholAbuse.set(drugAlcoholAbuse);
        this.smoker.set(smoker);
        this.cancer.set(cancer);
        this.otherConditions.set(otherConditions);
        this.medications.set(medications);
        this.height.set(height);
        this.weight.set(weight);
        this.registeredGP.set(registeredGP);
    }

    public String getName(){
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getOtherConditions() {
        return otherConditions.get();
    }

    public void setOtherConditions(String otherConditions) {
        this.otherConditions.set(otherConditions);
    }

    public void setPatientRecords(List<PatientRecord> patientRecords) {
        this.patientRecords = patientRecords;
    }

    /*public String getName() {
        return name;
    }*/

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public String getPostCode() {
        return postCode.get();
    }

    public void setPostCode(String postCode) {
        this.postCode.set(postCode);
    }

    public String getPPSNumber() {
        return PPSNumber.get();
    }

    public void setPPSNumber(String PPSNumber) {
        this.PPSNumber.set(PPSNumber);
    }

    public String getInsurance() {
        return insurance.get();
    }

    public void setInsurance(String insurance) {
        this.insurance.set(insurance);
    }

    public String getAllergies() {
        return allergies.get();
    }

    public void setAllergies(String allergies) {
        this.allergies.set(allergies);
    }

    public String getMedications() {
        return medications.get();
    }

    public void setMedications(String medications) {
        this.medications.set(medications);
    }

    public String getHeight() {
        return height.get();
    }

    public void setHeight(String height) {
        this.height.set(height);
    }

    public String getWeight() {
        return weight.get();
    }

    public void setWeight(String weight) {
        this.weight.set(weight);
    }

    public String getRegisteredGP() {
        return registeredGP.get();
    }

    public void setRegisteredGP(String registeredGP) {
        this.registeredGP.set(registeredGP);
    }

    public List<PatientRecord> getPatientRecords() {
        return patientRecords;
    }

    public Boolean getTubercolosis() {
        return tubercolosis.get();
    }

    public void setTubercolosis(Boolean tubercolosis) {
        this.tubercolosis.set(tubercolosis);
    }

    public Boolean getDiabetes() {
        return diabetes.get();
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes.set(diabetes);
    }

    public Boolean getHeartCondition() {
        return heartCondition.get();
    }

    public void setHeartCondition(Boolean heartCondition) {
        this.heartCondition.set(heartCondition);
    }

    public Boolean getGloucoma() {
        return gloucoma.get();
    }

    public void setGloucoma(Boolean gloucoma) {
        this.gloucoma.set(gloucoma);
    }

    public Boolean getEpilepsy() {
        return epilepsy.get();
    }

    public void setEpilepsy(Boolean epilepsy) {
        this.epilepsy.set(epilepsy);
    }

    public Boolean getDrugAlcoholAbuse() {
        return drugAlcoholAbuse.get();
    }

    public void setDrugAlcoholAbuse(Boolean drugAlcoholAbuse) {
        this.drugAlcoholAbuse.set(drugAlcoholAbuse);
    }

    public Boolean getSmoker() {
        return smoker.get();
    }

    public void setSmoker(Boolean smoker) {
        this.smoker.set(smoker);
    }

    public Boolean getCancer() {
        return cancer.get();
    }

    public void setCancer(Boolean cancer) {
        this.cancer.set(cancer);
    }

}

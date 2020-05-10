package com.example.hyperionapp;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientDetails extends ViewModel {

    // Declare class attributes
    // Personal Details
    ObservableField<String> name;
    ObservableField<String> email;
    Date dateOfBirth;
    ObservableField<String> address;
    ObservableField<String> address2;
    ObservableField<String> city;
    ObservableField<String> postCode;
    ObservableField<String> PPSNumber;
    ObservableField<String> insurance;

    // Medical Details
    String bloodType;
    ObservableField<String> allergies;
    ObservableField<String> otherConditions;
    ObservableField<String> medications;
    ObservableField<String> height;
    ObservableField<String> weight;
    ObservableField<String> registeredGP;

    // Medical Conditions Checkboxes
    ObservableField<Boolean> tuberculosis;
    ObservableField<Boolean> diabetes;
    ObservableField<Boolean> heartCondition;
    ObservableField<Boolean> glaucoma;
    ObservableField<Boolean> epilepsy;
    ObservableField<Boolean> drugAlcoholAbuse;
    ObservableField<Boolean> smoker;
    ObservableField<Boolean> cancer;

    // Patient Records
    List<Checkin> patientSessions;

    // Ongoing session details
    String currentSessionID;
    Checkin latestSnapshot;

    // Hash validation
    Date lastValidated;
    List<String> validatedHashes;

    public PatientDetails() {
        //Personal Details
        this.name = new ObservableField<>();
        this.email = new ObservableField<>();
        this.dateOfBirth = null;
        this.address = new ObservableField<>();
        this.address2 = new ObservableField<>();
        this.city = new ObservableField<>();
        this.postCode = new ObservableField<>();
        this.PPSNumber = new ObservableField<>();
        this.insurance = new ObservableField<>();
        //Medical Details
        this.bloodType = "";
        this.allergies = new ObservableField<>();
        this.otherConditions = new ObservableField<>();
        this.medications = new ObservableField<>();
        this.height = new ObservableField<>();
        this.weight = new ObservableField<>();
        this.registeredGP = new ObservableField<>();

        //Medical Conditions Checkboxes
        this.tuberculosis = new ObservableField<>();
        this.diabetes = new ObservableField<>();
        this.heartCondition = new ObservableField<>();
        this.glaucoma = new ObservableField<>();
        this.epilepsy = new ObservableField<>();
        this.drugAlcoholAbuse = new ObservableField<>();
        this.smoker = new ObservableField<>();
        this.cancer = new ObservableField<>();

        this.patientSessions = new ArrayList<Checkin>();
        this.currentSessionID = "";
        this.latestSnapshot = null;
        this.lastValidated = null;
        this.validatedHashes = new ArrayList<>();
    }

    public Checkin findSessionById(String sessionID){
        /* Class method to find a session by its ID
         * @param String sessionID The ID to find the session by
         * @return Checkin or null
         */
        for (int i = 0; i < this.patientSessions.size(); i++){
            Log.d("INTERNAL ID", this.patientSessions.get(i).getSession_id());
            Log.d("EXTERNAL ID",sessionID);
            Log.d("INT EXT CHECK", ""+this.patientSessions.get(i).getSession_id().equals(sessionID));
            if(this.patientSessions.get(i).getSession_id().equals(sessionID)) {
                Checkin c = this.patientSessions.get(i);
                return c;
            }
        }
        return null;
    }

    public String getPreconditions(){
        /* Class method create a concatenated String of the allergies and other condition text
         * fields and all preCondition tick boxes for the session checkin
         * @param String sessionID The ID to find the session by
         * @return String
         */
        String preConditions = "";
        if(this.allergies.get() != null && !"".equals(this.allergies.get())){ preConditions += this.allergies.get() + ", "; }
        if(this.tuberculosis.get() != null  && this.tuberculosis.get() == true){ preConditions += "Tuberculosis, "; }
        if(this.heartCondition.get() != null  && this.heartCondition.get() == true){ preConditions += "Heart Condition, "; }
        if(this.glaucoma.get() != null  && this.glaucoma.get() == true){ preConditions += "Gloucoma, "; }
        if(this.drugAlcoholAbuse.get() != null  && this.drugAlcoholAbuse.get() == true){ preConditions += "Drug/Alcohol Abuse, "; }
        if(this.smoker.get() != null  && this.smoker.get() == true){ preConditions += "Smoker, "; }
        if(this.cancer.get() != null  && this.cancer.get() == true){ preConditions += "Cancer, "; }
        if(this.otherConditions.get() != null && !"".equals(this.otherConditions.get())){ preConditions += this.otherConditions.get() + ","; }
        Log.d("preConditions", preConditions);
        if(preConditions.length() > 0 && ",".equals(preConditions.charAt(preConditions.length() - 1))) {
            preConditions = preConditions.substring(0, preConditions.length() - 2);
        }
        return preConditions;
    }


    public Checkin updateSessionById(Checkin newSession){
        /* Class method to update a session with new data by its ID
         * @param String sessionID The ID to find the session by
         * @return Checkin or null
         */
        for (int i = 0; i < this.patientSessions.size(); i++){
            if(this.patientSessions.get(i).getSession_id().equals(newSession.getSession_id())) {
                this.patientSessions.set(i, newSession);
                return this.patientSessions.get(i);
            }
        }
        return null;
    }

    public void setPersonalDetails(
            String name, String email, Date dateOfBirth, String address, String address2,
            String city, String postCode, String PPSNumber, String insurance, String currentSessionID
    ){
        this.name.set(name);
        this.email.set(email);
        this.dateOfBirth = dateOfBirth;
        this.address.set(address);
        this.address2 .set(address2);
        this.city.set(city);
        this.postCode.set(postCode);
        this.PPSNumber.set(PPSNumber);
        this.insurance.set(insurance);
        this.currentSessionID = currentSessionID;
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
        this.tuberculosis.set(tubercolosis);
        this.diabetes.set(diabetes);
        this.heartCondition.set(heartCondition);
        this.glaucoma.set(gloucoma);
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

    public void setPatientSessions(List<Checkin> patientSessions) {
        this.patientSessions = patientSessions;
    }

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

    public List<Checkin> getPatientSessions() {
        return patientSessions;
    }

    public Boolean getTuberculosis() {
        return tuberculosis.get();
    }

    public void setTuberculosis(Boolean tuberculosis) {
        this.tuberculosis.set(tuberculosis);
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

    public Boolean getGlaucoma() {
        return glaucoma.get();
    }

    public void setGlaucoma(Boolean glaucoma) {
        this.glaucoma.set(glaucoma);
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

    public String getCurrentSessionID() { return currentSessionID; }

    public void setCurrentSessionID(String currentSessionID) { this.currentSessionID = currentSessionID; }

    public Checkin getLatestSnapshot() { return latestSnapshot; }

    public void setLatestSnapshot(Checkin latestSnapshot) { this.latestSnapshot = latestSnapshot; }

    public Date getLastValidated() { return lastValidated; }

    public void setLastValidated(Date lastValidated) { this.lastValidated = lastValidated; }

    public List<String> getValidatedHashes() { return validatedHashes; }

    public void setValidatedHashes(List<String> validatedHashes) { this.validatedHashes = validatedHashes; }
}

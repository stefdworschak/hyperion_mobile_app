package com.example.hyperionapp;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class Checkin {
    public String session_id;
    public Date session_checkin;
    public HashMap<String, String> session_details;


    public Checkin(String session_id, Date session_checkin, String symptoms, String symptoms_duration, String pain_scale, String pre_conditions){
        this.session_id = session_id;
        this.session_checkin = session_checkin;
        HashMap<String, String> temp =new HashMap<>();
        temp.put("symptoms", symptoms);
        temp.put("symptoms_duration", symptoms_duration);
        temp.put("pain_scale", pain_scale);
        temp.put("pre_conditions", pre_conditions);
        this.session_details = temp;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public Date getSession_checkin() {
        return session_checkin;
    }

    public void setSession_checkin(Date session_checkin) {
        this.session_checkin = session_checkin;
    }

    public HashMap<String, String> getSession_details() {
        return session_details;
    }

    public void setSession_details(String symptoms, String symptoms_duration, String pain_scale, String pre_conditions) {
        HashMap<String, String> temp =new HashMap<>();
        temp.put("symptoms", symptoms);
        temp.put("symptoms_duration", symptoms_duration);
        temp.put("pain_scale", pain_scale);
        temp.put("pre_conditions", pre_conditions);
        this.session_details = temp;
    }
}

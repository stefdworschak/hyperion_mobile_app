package com.example.hyperionapp;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Checkin {
    private String session_id;
    private Date session_checkin;
    private HashMap<String, String> session_details;
    private int session_shared;
    private List<SessionDocument> session_documents;


    public Checkin(String session_id, String symptoms, String symptoms_duration, String pain_scale, String pre_conditions, int session_shared,
                    List<SessionDocument> session_documents){
        this.session_id = session_id;
        this.session_checkin = new Date();
        HashMap<String, String> temp =new HashMap<>();
        temp.put("symptoms", symptoms);
        temp.put("symptoms_duration", symptoms_duration);
        temp.put("pain_scale", pain_scale);
        temp.put("pre_conditions", pre_conditions);
        this.session_details = temp;
        this.session_shared = session_shared;
        this.session_documents = session_documents;
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

    public int getSession_shared() { return session_shared; }

    public void setSession_shared(int session_shared) { this.session_shared = session_shared; }

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

    public void setSession_documents(List<SessionDocument> session_documents){
        this.session_documents = session_documents;
    }

    public List<SessionDocument> getSession_documents() {
        return session_documents;
    }
}

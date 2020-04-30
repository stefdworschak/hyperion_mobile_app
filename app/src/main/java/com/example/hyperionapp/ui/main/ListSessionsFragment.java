package com.example.hyperionapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hyperionapp.Checkin;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.SingleSession;

import java.util.ArrayList;
import java.util.List;


public class ListSessionsFragment extends Fragment {
    private PatientDetails patientModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_list_sessions,container,false);

        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        List<Checkin> sessions = patientModel.getPatientSessions();
        ArrayList<String> sessionsArray = new ArrayList<String>();
        if(sessions.size() > 0){
            for(int i = 0;  i < sessions.size(); i++){
                Checkin s = sessions.get(i);
                int status = s.getSession_shared();
                if(status == 0) {
                    sessionsArray.add("Session: " + s.getSession_id() + "\nDate: " + s.getSession_checkin() + "\nStatus: Started");
                } else if(status == 1){
                    sessionsArray.add("Session: " + s.getSession_id() + "\nDate: " + s.getSession_checkin() + "\nStatus: Waiting for Triage");
                } else if(status == 2){
                    sessionsArray.add("Session: " + s.getSession_id() + "\nDate: " + s.getSession_checkin() + "\nStatus: In Session");
                } else {
                    sessionsArray.add("Session: " + s.getSession_id() + "\nDate: " + s.getSession_checkin() + "\nStatus: Complete");
                }
            }
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.fragment_listview, sessionsArray);

        ListView listView = (ListView) v.findViewById(R.id.session_list);
        listView.setAdapter(adapter);
        // Reference: https://stackoverflow.com/a/17851698
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String item = parent.getItemAtPosition(position).toString();
                String[] sessionDetails = item.split("\n");
                String sessionID_lbl = sessionDetails[0];
                String status_lbl = sessionDetails[2];
                String sessionID = sessionID_lbl.split(" ")[1];
                String status = status_lbl.split(" ")[1];
                Log.d("LIST ITEM", sessionID + "");
                Intent intent = new Intent(getActivity(), SingleSession.class);
                intent.putExtra("session_id", sessionID);
                intent.putExtra("session_status", status);
                startActivity(intent);
            }
        });

        return v;
    }
}

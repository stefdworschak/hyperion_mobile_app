package com.example.hyperionapp.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hyperionapp.Checkin;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.SingleSessionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ListSessionsFragment extends Fragment {
    // Declare class variables;
    private PatientDetails patientModel;

    // Declare and instantiate class constants
    final private List<String> valiateDocuments = new ArrayList<>();
    final private Gson gson = new Gson();
    final private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final private String user_id = user.getUid();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        // Declare and instantiate view
        View v = inflater.inflate(R.layout.fragment_list_sessions,container,false);
        // Instantiate viewModel
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);

        // Retrieve the sessions from the viewModel
        List<Checkin> sessions = patientModel.getPatientSessions();
        // Declare and instantiate a new array that will be the source of the ListView
        ArrayList<String> sessionsArray = new ArrayList<>();
        // If there are any sessions
        if(sessions.size() > 0){
            // Loop through the sessions (and populate the sessionsArray
            for(int i = 0;  i < sessions.size(); i++){

                Checkin s = sessions.get(i);
                int status = s.getSession_shared();
                // Declare a new String status and dynamically assign it a status
                String str_status;
                if(status == 0) {
                    if(s.getSession_checkin().compareTo(new Date()) == 1){
                        str_status = "Scheduled";
                    } else {
                        str_status = "Started";
                    }
                } else if(status == 1){
                    str_status = "Data Access requested";
                } else if(status == 2){
                    str_status = "Data shared";
                } else {
                    str_status = "Complete";
                }
                // Add session details to array for the ListView
                sessionsArray.add("Session: " + s.getSession_id() + "\nDate: "
                                  + s.getSession_checkin() + "\nStatus: " + str_status);
            }
        }
        // Reference: https://www.tutorialspoint.com/how-to-make-a-listview-in-android
        // Declare and instantiate ArrayAdapter in view
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                R.layout.fragment_listview, sessionsArray);
        // Set the created adapter on ListView
        ListView listView = v.findViewById(R.id.session_list);
        listView.setAdapter(adapter);

        // Reference: https://stackoverflow.com/a/17851698
        // Set event listener for when user clicks on one of the items
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
        long id) -> {
            // Get selected item
            String item = parent.getItemAtPosition(position).toString();
            // Split string and retrieve session details from label
            String[] sessionDetails = item.split("\n");
            String sessionID_lbl = sessionDetails[0];
            String status_lbl = sessionDetails[2];
            String sessionID = sessionID_lbl.split(" ")[1];
            String status = status_lbl.split(" ")[1];
            // Redirect to SingleSessionActivity and add extra data to display the information
            Intent intent = new Intent(getActivity(), SingleSessionActivity.class);
            intent.putExtra("session_id", sessionID);
            intent.putExtra("session_status", status);
            startActivity(intent);
        });
        // Render view
        return v;
    }


}

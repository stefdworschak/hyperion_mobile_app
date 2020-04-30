/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hyperionapp.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.Checkin;
import com.example.hyperionapp.SessionDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;


public class CheckinFragment extends Fragment {

    EditText etToken;
   // private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private PatientDetails patientModel;
    private Checkin snapShotPayload = null;

    private final String COLLECTION_NAME = "checkins";
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "MainActivity";
    private String session_documents = "";
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    private EncryptionClass encryption = new EncryptionClass();
    Spinner pain_scale_spinner;
    Spinner duration_spinner;

    String painScale;
    String duration;
    String symptoms;
    String preConditions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_checkin, container, false);

        final EditText etSymptoms = (EditText) v.findViewById(R.id.symptoms_text);
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        snapShotPayload = patientModel.getLatestSnapshot();
        if(!"".equals(patientModel.getCurrentSessionID())){
            Log.d("SHOW CHECKED IN", "True");
            hideCheckin(v);
            showCheckedIn(v);
            if(snapShotPayload != null && snapShotPayload.getSession_shared() == 1){
                showShareData(v);
            }
        }


        pain_scale_spinner = (Spinner) v.findViewById(R.id.pain_scale_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.pain_scale_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pain_scale_spinner.setAdapter(adapter);
        pain_scale_spinner.setPrompt("How much pain do you have?");

        duration_spinner = (Spinner) v.findViewById(R.id.duration_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.duration_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duration_spinner.setAdapter(adapter2);
        duration_spinner.setPrompt("How long has this been going on?");

        pain_scale_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                painScale = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                duration = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button bSubscribe = v.findViewById(R.id.subscribeButton);
        final Button bEndSession = v.findViewById(R.id.endSession);
        etToken = v.findViewById(R.id.editText3);
        db = FirebaseFirestore.getInstance();
        session_documents = getActivity().getIntent().getStringExtra("session_documents");

        bSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                symptoms = etSymptoms.getText().toString();
                if ("".equals(symptoms) || "Select".equals(painScale) || "Select".equals(duration)){
                    Toast.makeText(getContext(), "Please fill in all of the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    final String sessionID = genRandom(10);
                    Log.d(TAG, "Subscribing to weather topic");
                    // [START subscribe_topics]
                    FirebaseMessaging.getInstance().subscribeToTopic(sessionID)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                    String msg = getString(R.string.msg_subscribed);
                                    if (!task.isSuccessful()) {
                                        msg = getString(R.string.msg_subscribe_failed);
                                    } else {
                                        Log.d(TAG, msg);
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                        // Write to Firebase Database
                                        //https://firebase.google.com/docs/database/android/read-and-write
                                        String pre_conditions = patientModel.getPreconditions();

                                        int session_shared = 0;
                                        Log.d("Session ID", sessionID);
                                        List<SessionDocument> session_documents = new ArrayList<>();
                                        Checkin checkin = new Checkin(sessionID, symptoms, duration, painScale, pre_conditions, session_shared, session_documents);
                                        setDocument(db, checkin);
                                        // Add to session to user
                                        List<Checkin> sessions = patientModel.getPatientSessions();
                                        sessions.add(checkin);
                                        patientModel.setPatientSessions(sessions);
                                        patientModel.setCurrentSessionID(sessionID);
                                        patientModel.setLatestSnapshot(checkin);
                                        String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);

                                        Log.d("Checkin Message", saveMsg);
                                        hideCheckin(v);
                                        showCheckedIn(v);
                                        updateFromSnapshot(db, patientModel, sessionID, v);

                                    }
                                }
                            });
                }
                // [END subscribe_topics]
            }
        });

        bEndSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String session_id = patientModel.getCurrentSessionID();
                patientModel.setCurrentSessionID("");
                patientModel.setLatestSnapshot(null);
                String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);
                db = FirebaseFirestore.getInstance();
                Map<String, Object> sharing = new HashMap<>();
                sharing.put("session_shared", "3");
                DocumentReference document = db.collection(COLLECTION_NAME).document(session_id);
                document.update(sharing).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideCheckedIn(v);
                        showCheckin(v);
                        hideShareData(v);
                    }
                });
            }
        });

        return v;
    }

    public static void hideCheckin(View v){
        ScrollView svCheckin = v.findViewById(R.id.checkin_view);
        svCheckin.setVisibility(View.GONE);
    }

    public static void showCheckedIn(View v){
        ScrollView svCheckedIn = v.findViewById(R.id.checkedin_view);
        svCheckedIn.setVisibility(View.VISIBLE);
    }

    public static void showCheckin(View v){
        ScrollView svCheckin = v.findViewById(R.id.checkin_view);
        svCheckin.setVisibility(View.VISIBLE);
    }

    public static void hideCheckedIn(View v){
        ScrollView svCheckedIn = v.findViewById(R.id.checkedin_view);
        svCheckedIn.setVisibility(View.GONE);
    }

    public static void hideShareData(View v){
        LinearLayout lyShareData = v.findViewById(R.id.share_data);
        lyShareData.setVisibility(View.GONE);
    }

    public static void showShareData(View v){
        LinearLayout lyShareData = v.findViewById(R.id.share_data);
        lyShareData.setVisibility(View.VISIBLE);
    }

    public static String genRandom(int num){
        String randID = "";
        String[] num_choices = {"1","2","3","4","5","6","7","8","9","0"};
        Date dt = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"));
        cal.setTime(dt);
        Random random = new Random();

        for(int i = 0; i < num; i++){
            int rand = random.nextInt(10);
            randID += num_choices[rand];
        }
        randID += (cal.get(Calendar.YEAR)
                + cal.get(Calendar.MONTH)
                + cal.get(Calendar.DAY_OF_MONTH)
                + cal.get(Calendar.HOUR_OF_DAY)
                + cal.get(Calendar.MINUTE)
                + cal.get(Calendar.SECOND));

        return randID;
    }

    public static void setDocument(FirebaseFirestore db, Checkin checkin) {
        // [START set_document]
        db.collection("checkins").document(checkin.getSession_id())
                .set(checkin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        // [END set_document]
    }

    public void updateFromSnapshot(FirebaseFirestore db, PatientDetails patientModel, String sessionID, View v){
        // Reference: https://firebase.google.com/docs/firestore/query-data/listen
        final DocumentReference document = db.collection("checkins").document(sessionID);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> newSnapshot = snapshot.getData();
                    JSONObject jsonSnapshot = new JSONObject(newSnapshot);
                    Checkin snapShotPayload = patientModel.getLatestSnapshot();
                    if(snapShotPayload != newSnapshot){
                        Log.d(TAG, "Current data: " + newSnapshot);
                        Gson gson = new Gson();
                        Checkin c = gson.fromJson(jsonSnapshot.toString(), Checkin.class);
                        snapShotPayload = c;
                        Log.d("UPDATED DOCUMENTS", c.getSession_id());
                        if(c.getSession_shared() != 2) {
                            patientModel.setLatestSnapshot(c);
                            // Save latest snapshot in patient model
                            //Nobody, but the user should be able to change the session shared to 2
                            // 2 = data shared
                            if (c.getSession_shared() != 2) {
                                Log.d("CHNG SESSION STATUS", "" + c.getSession_shared());
                                if (c.getSession_shared() == 1) {
                                    showShareData(v);
                                }
                                if (c.getSession_shared() == 3) {
                                    showCheckin(v);
                                    hideCheckedIn(v);
                                }
                            }

                            patientModel.setCurrentSessionID("");
                            patientModel.setLatestSnapshot(null);
                            String msg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);
                        }
                        // Handle status changes with de registration registration.remove()


                        // Set snapshot on App restart for the last session
                        // Set snapshot on code activity
                        // add to patient model
                        // Save patientModel

                    } else {
                        if(snapShotPayload.getSession_shared() == 2){
                            hideShareData(v);
                        }
                        Log.d("PAYLOAD: ", "NO CHANGES");
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

}
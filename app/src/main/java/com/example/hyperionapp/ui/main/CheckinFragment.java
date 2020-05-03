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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.Checkin;
import com.example.hyperionapp.RegisterActivity;
import com.example.hyperionapp.SessionDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    /* Fragment to handle all Check-in logic */

    // Declare and instantiate class variables
    String painScale;
    String duration;
    String symptoms;
    String preConditions;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PatientDetails patientModel;
    private Checkin snapShotPayload = null;

    // Declare and instatiate class constants
    final private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    final private EncryptionClass encryption = new EncryptionClass();
    private final String COLLECTION_NAME = "checkins";
    private static final String TAG = "MainActivity";
    private RegisterActivity reg = new RegisterActivity();
    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Declare and instantiate view
        final View v = inflater.inflate(R.layout.fragment_checkin, container, false);
        // Instantiate viewModel
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);

        // snapShotPayload stores the latest data snapshot of the current session
        // to listen for updates from the FirebaseFirestore for the ongoing session
        // Retrieving the latest snapshot from the viewModel
        snapShotPayload = patientModel.getLatestSnapshot();

        // If there is an ongoing session
        if(!"".equals(patientModel.getCurrentSessionID())){
            //Hide the checkin form and show the checked in message
            hideCheckin(v);
            showCheckedIn(v);
            // If there the payload is not empty and session_shared is in status 1
            // Show a message that it was requested to share the data
            // 1 = Share data requested, but not shared yet
            if(snapShotPayload != null && snapShotPayload.getSession_shared() == 1){
                showShareData(v);
            }
        }

        //Declare and instantiate the EditText and the Spinners
        EditText etSymptoms = v.findViewById(R.id.symptoms_text);
        Spinner pain_scale_spinner = v.findViewById(R.id.pain_scale_spinner);
        Spinner duration_spinner = v.findViewById(R.id.duration_spinner);
        Button bSubscribe = v.findViewById(R.id.subscribeButton);
        Button bEndSession = v.findViewById(R.id.endSession);

        // Add dropdown options from array and render spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.pain_scale_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pain_scale_spinner.setAdapter(adapter);

        // Add dropdown options from array and render spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.duration_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duration_spinner.setAdapter(adapter2);

        // Reference: https://developer.android.com/guide/topics/ui/controls/spinner#SelectListener
        // Define the functionality that is triggered when an item is selected from
        // the spinner
        pain_scale_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Reference: https://stackoverflow.com/a/10332288
                // Add the selected option to a variable
                painScale = parent.getItemAtPosition(position).toString();
            }
            // Optional method override if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Define the functionality that is triggered when an item is selected from
        // the spinner
        duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Add the selected option to a variable
                duration = parent.getItemAtPosition(position).toString();
            }
            // Optional method override if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Subscribe to FirebaseMessaging topic if the user clicks the "Check In" button
        bSubscribe.setOnClickListener((View view) -> {
            // Get value from the symptoms text field
            symptoms = etSymptoms.getText().toString();
            // If any of the required fields are empty
            if ("".equals(symptoms) || "Select".equals(painScale) || "Select".equals(duration)){
                // Show error message
                reg.showTopToast(getContext(), "Please fill in all of the fields.");
            } else {
                // Create semi-random session ID and write it to the log
                final String sessionID = genRandom(10);
                Log.d("Session ID", sessionID);

                // Reference: https://firebase.google.com/docs/cloud-messaging/android/topic-messaging#subscribe_the_client_app_to_a_topic
                // Use FirebaseMessaging to subscribe to a topic with the session ID as its value
                FirebaseMessaging.getInstance().subscribeToTopic(sessionID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Reference: https://stackoverflow.com/a/17789187
                                // Hide the soft keyboard
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                // If the subscription was not successful
                                if (!task.isSuccessful()) {
                                    // Write error to log
                                    Log.d("CHECKIN ERROR ", getString(R.string.msg_subscribed));
                                } else {

                                    // Get any pre-existing conditions entered by the user
                                    preConditions = patientModel.getPreconditions();
                                    // Default setting for session_shared
                                    int session_shared = 0;
                                    // Declare and instantiate empty list of SessionDocuments
                                    List<SessionDocument> session_documents = new ArrayList<>();
                                    // Declare and instantiate new Checkin
                                    Checkin checkin = new Checkin(sessionID, symptoms, duration, painScale, preConditions, session_shared, session_documents);
                                    Log.d("CHECKIN DATA", checkin.getSession_checkin() + "");
                                    // Add session to FirebaseFirestore
                                    //https://firebase.google.com/docs/database/android/read-and-write
                                    setDocument(db, checkin);
                                    // Add new Checkin to existing Sessions from model
                                    List<Checkin> sessions = patientModel.getPatientSessions();
                                    sessions.add(checkin);

                                    // Update viewModel with new patientSessions,
                                    // currentSessionID and latestSnapshot and save
                                    // the updated viewModel to the encrypted local file
                                    patientModel.setPatientSessions(sessions);
                                    patientModel.setCurrentSessionID(sessionID);
                                    patientModel.setLatestSnapshot(checkin);
                                    String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);

                                    // Hide the checkin form and show the checked-in view
                                    hideCheckin(v);
                                    showCheckedIn(v);
                                    // Call class method to listen for data changes in
                                    // FirebaseFirestore for the ongoing session
                                    updateFromSnapshot(db, patientModel, sessionID, v);
                                }
                            }
                        });
            }
        });

        // Change the session_shared value to 3 and end the session
        bEndSession.setOnClickListener((View view) -> {
            // Get the currentSessionID from the viewModel
            String session_id = patientModel.getCurrentSessionID();
            // Delete the currentSessionID and latestSnapshot from the viewModel
            patientModel.setCurrentSessionID("");
            patientModel.setLatestSnapshot(null);
            // Update the session in the list of sessions in the viewModel
            Checkin current_session = patientModel.findSessionById(session_id);
            patientModel.updateSessionById(current_session);
            // Save the viewModel to the encrypted local file
            String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);

            // Create new HashMap to update the session_shared value in the FirebaseFirestore
            Map<String, Object> sharing = new HashMap<>();
            sharing.put("session_shared", "3");
            // Update the session with the new session_shared HashMap
            DocumentReference document = db.collection(COLLECTION_NAME).document(session_id);
            document.update(sharing).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Reset the view to the original by hiding the Checked-in and Share Data
                    // views and showing the Checkin form
                    hideCheckedIn(v);
                    showCheckin(v);
                    hideShareData(v);
                }
            });
        });

        // Render the Fragment
        return v;
    }

    public static void hideCheckin(View v){
        /* Method to hide the checkin form
         * @param View v The view which should be hidden
         * @return void
         */
        // Select view by id and set visibility to gone
        ScrollView svCheckin = v.findViewById(R.id.checkin_view);
        svCheckin.setVisibility(View.GONE);
    }

    public static void showCheckin(View v){
        /* Method to show the checkin form
         * @param View v The view which should be shown
         * @return void
         */
        // Select view by id and set visibility to visible
        ScrollView svCheckin = v.findViewById(R.id.checkin_view);
        svCheckin.setVisibility(View.VISIBLE);
    }

    public static void hideCheckedIn(View v){
        /* Method to hide the checked-in view
         * @param View v The view which should be hidden
         * @return void
         */
        // Select view by id and set visibility to gone
        ScrollView svCheckedIn = v.findViewById(R.id.checkedin_view);
        svCheckedIn.setVisibility(View.GONE);
    }

    public static void showCheckedIn(View v){
        /* Method to show the checked-in form
         * @param View v The view which should be shown
         * @return void
         */
        // Select view by id and set visibility to visible
        ScrollView svCheckedIn = v.findViewById(R.id.checkedin_view);
        svCheckedIn.setVisibility(View.VISIBLE);
    }

    public static void hideShareData(View v){
        /* Method hide the share data view
         * @param View v The view which should be hidden
         * @return void
         */
        // Select view by id and set visibility to gone
        LinearLayout lyShareData = v.findViewById(R.id.share_data);
        lyShareData.setVisibility(View.GONE);
    }

    public static void showShareData(View v){
        /* Method to show the share data view
         * @param View v The view which should be shown
         * @return void
         */
        // Select view by id and set visibility to visible
        LinearLayout lyShareData = v.findViewById(R.id.share_data);
        lyShareData.setVisibility(View.VISIBLE);
    }

    public static String genRandom(int num){
        /* Method generate a semi-random number for the sessionID from a combination of
         * the current time and a randomly generated number
         * @param int num The length of the random number to be added to the timestamp
         * @return String
         */
        // Declare and instantiate the random ID
        String randID = "";
        // Declare and instantiate a String array to select from to generate the random number
        String[] num_choices = {"1","2","3","4","5","6","7","8","9","0"};
        // Get the current date and set the Calendar instance to it
        Date dt = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Dublin"));
        cal.setTime(dt);
        // Declare and instantiate a new Random class to help generate random numbers
        Random random = new Random();

        // Iterate as many times as specified in the method param
        for(int i = 0; i < num; i++){
            // Create a new random number between 0 and 9
            int rand = random.nextInt(10);
            // Append the element at the index of the random number to the random ID
            randID += num_choices[rand];
        }
        // Append some of the timestamp details
        randID += (cal.get(Calendar.YEAR)
                + cal.get(Calendar.MONTH)
                + cal.get(Calendar.DAY_OF_MONTH)
                + cal.get(Calendar.HOUR_OF_DAY)
                + cal.get(Calendar.MINUTE)
                + cal.get(Calendar.SECOND));

        return randID;
    }

    public static void setDocument(FirebaseFirestore db, Checkin checkin) {
        /* Method to handle saving a new Checkin to the FireStore database
         * @param FirebaseFirestore db The FirebaseFirestore instance
         * @param Checkin checkin The new Checkin to be saved
         * @return void
         */

        // Reference: https://firebase.google.com/docs/firestore/manage-data/add-data#android
        // Retrieve the checkins collection, create a new document with the new sessionID
        // as its name and the Checkin its value
        db.collection("checkins").document(checkin.getSession_id())
                .set(checkin)
                .addOnSuccessListener((Void aVoid) -> {
                        // On success write message to log
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                        // On error write error message to log
                        Log.w(TAG, "Error writing document", e);
                });
    }

    public void updateFromSnapshot(FirebaseFirestore db, PatientDetails patientModel, String sessionID, View v){
        /* Method to handle listening for updates from FirebaseFirestore for a specific session
         * @param FirebaseFirestore db The FirebaseFirestore instance
         * @param PatientDetails patientModel The current state of the viewModel
         * @param String sessionID The session's sessionID
         * @param View v The current view
         * @return void
         */

        // Reference: https://firebase.google.com/docs/firestore/query-data/listen
        // Add a snapshotListener to the document with the specified sessionID
        final DocumentReference document = db.collection("checkins").document(sessionID);
        document.addSnapshotListener((@Nullable DocumentSnapshot snapshot,
                    @Nullable FirebaseFirestoreException e) -> {
                // If there are any errors
                if (e != null) {
                    // Write them to the log and return
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // If the snapshot is not null and the snapshot exists
                if (snapshot != null && snapshot.exists()) {
                    // Save the snapshot in a variable
                    Map<String, Object> newSnapshot = snapshot.getData();
                    // Reference: https://stackoverflow.com/a/56259511
                    // The timestamp returned in snapshot cannot be parsed parsed properly
                    // It has to be manually converted and set
                    Timestamp session_timestamp = (Timestamp) snapshot.getData().get("session_checkin");
                    Date session_checkin = session_timestamp.toDate();
                    // Convert the snapshot to a JSON Object
                    JSONObject jsonSnapshot = new JSONObject(newSnapshot);
                    // Retrieve the latestSnapshot from the viewModel
                    Checkin snapShotPayload = patientModel.getLatestSnapshot();
                    // If there was any update in the snapshot
                    if(snapShotPayload != newSnapshot){
                        // Reference: https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/
                        // Parse the new snapshot from JSON to Checkin with GSON.fromJson
                        Checkin c = gson.fromJson(jsonSnapshot.toString(), Checkin.class);
                        // Manually setting session_checkin
                        c.setSession_checkin(session_checkin);
                        // Update latestSnapshot in the viewModel
                        patientModel.setLatestSnapshot(c);
                        // Nobody, but the user should be able to change the session shared to 2 (=shared)
                        // Therefore, if the session_shared from the

                        if(c.getSession_shared() == 2) {
                            Checkin originalSession = patientModel.findSessionById(sessionID);
                            c.setSession_shared(originalSession.getSession_shared());
                        }
                        // Update the viewModel with the new information from the snapshot and
                        // save it to the encrypted local file
                        Checkin news =  patientModel.updateSessionById(c);
                        encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);

                        // Update the view based on the status
                        if (c.getSession_shared() == 0 || c.getSession_shared() == 2) {
                            hideCheckin(v);
                            showCheckedIn(v);
                        } else if (c.getSession_shared() == 1) {
                            showShareData(v);
                            hideCheckin(v);
                            showCheckedIn(v);
                        } else {
                            showCheckin(v);
                            hideCheckedIn(v);
                        }

                        // If the session ended
                        if(c.getSession_shared() == 3){
                            // Clear currentSessionID and latestSnapshot
                            // TODO: remove the registration
                            patientModel.setCurrentSessionID("");
                            patientModel.setLatestSnapshot(null);
                        }
                    }
                } else {
                    // If the snapshot is null or does not exist write error to log
                    Log.e(TAG, "Empty Snapshot received");
                }
        });
    }

}
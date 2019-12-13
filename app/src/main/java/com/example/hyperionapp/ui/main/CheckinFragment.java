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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hyperionapp.R;
import com.example.hyperionapp.Checkin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


public class CheckinFragment extends Fragment {

    EditText etToken;
   // private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    private static final String TAG = "MainActivity";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_checkin, container, false);
        /*WebView mWebView = (WebView) v.findViewById(R.id.webView);
        mWebView.loadUrl("https://hyperion-app.herokuapp.com/checkin/");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());*/
        Button bSubscribe = v.findViewById(R.id.subscribeButton);
        Button bLogToken = v.findViewById(R.id.logTokenButton);
        etToken = v.findViewById(R.id.editText3);
       // mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        bLogToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    //Log.w(TAG, "getInstanceId failed", task.getException());
                                    Toast.makeText(getActivity().getApplicationContext(), "getInstance failed", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                // Log and toast
                                //String msg = getString(R.string.msg_token_fmt, token);
                                //Log.d(TAG, msg);
                                //Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();
                                etToken.setText(""+token);
                            }
                        });
            }
        });
        bSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sessionID = genRandom(10);
                Log.d(TAG, "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic(sessionID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);

                                } else {
                                    Log.d(TAG, msg);
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                    // Write to Firebase Database
                                    //https://firebase.google.com/docs/database/android/read-and-write

                                    String symptoms = "";
                                    String symptoms_duration = "";
                                    String pain_scale = "";
                                    String pre_conditions = "";
                                    Log.d("Session ID", sessionID);
                                    Checkin checkin = new Checkin(sessionID, symptoms, symptoms_duration, pain_scale, pre_conditions);
                                    setDocument(db, checkin);
                                    //mDatabase.child("checkins").child(sessionID).setValue(checkin);
                                }
                            }
                        });

                // [END subscribe_topics]
            }
        });

        return v;
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

}
package com.example.hyperionapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.android.annotations.NonNull;
import com.example.hyperionapp.ui.main.CheckedInFragment;
import com.example.hyperionapp.ui.main.CheckinFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.FirestoreGrpc;

import java.util.HashMap;
import java.util.Map;

public class CodeActivity extends AppCompatActivity {
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String CODE = "1234";
    EditText etCode;
    private final int NOTIFICATION_ID = 606;
    private final String CHANNEL_ID = "fcm_notification";
    private final String CODE_ALIAS = "user_code_" + user_id;
    private final String CODE_FILENAME = user_id + "code.enc";
    private final String COLLECTION_NAME = "checkins";
    EncryptionClass encryption = new EncryptionClass();
    String session_id = "";
    private static FirebaseFirestore db;
    private static String TAG = "SHARING SETTINGS MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Button btnConfirm = (Button) findViewById(R.id.confirmCodeBtn);
        etCode = (EditText) findViewById(R.id.etConfirmCode_text);
        session_id = getIntent().getStringExtra("session_id");
        Log.d(TAG, "SESSION_ID");
        if(session_id != null){
            Log.d(TAG, session_id);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String encrypted_data = encryption.basicRead(CodeActivity.this, CODE_FILENAME);
                    String userCode = encryption.decryptSymmetrically(encrypted_data, CODE_ALIAS);
                    if(userCode.equalsIgnoreCase(etCode.getText().toString())) {
                        shareUserData(session_id);
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Code Entered. Please try again", Toast.LENGTH_SHORT);
                    }
            }
        });
    }


    // Updating data from the official api reference
    // Reference: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
    public void shareUserData(String session_id){
        Log.d("SHARING", "SHARE DATA!!!!");
        db = FirebaseFirestore.getInstance();
        Map<String, Object> sharing = new HashMap<>();
        sharing.put("session_shared", "2");
        DocumentReference document = db.collection(COLLECTION_NAME).document(session_id);
        document.update(sharing)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Sharing Settings successfully updated!");
                Intent go = new Intent(CodeActivity.this, MainActivity.class);
                // you pass the position you want the viewpager to show in the extra,
                // please don't forget to define and initialize the position variable
                // properly
                go.putExtra("viewpager_position", 2);
                startActivity(go);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating Sharing Settings", e);
                    }
                });


    }
}

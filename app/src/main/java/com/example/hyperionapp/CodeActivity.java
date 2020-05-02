package com.example.hyperionapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class CodeActivity extends AppCompatActivity {
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String CODE = "1234";
    Gson gson = new Gson();
    EditText etCode;
    private final int NOTIFICATION_ID = 606;
    private final String CHANNEL_ID = "fcm_notification";
    private final String CODE_ALIAS = "user_code_" + user_id;
    private final String CODE_FILENAME = user_id + "code.enc";
    private final String COLLECTION_NAME = "checkins";
    EncryptionClass encryption = new EncryptionClass();
    String session_id = "";
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    private static String session_shared = "";
    private static String session_documents = "";
    private static String notification_id;
    private static FirebaseFirestore db;
    private static String TAG = "SHARING SETTINGS MSG";
    private String ASYMMETRIC_ALIAS = "hyperion_asymmetric_" + user_id;
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkLoggedin(FirebaseAuth.getInstance().getCurrentUser());

        setContentView(R.layout.activity_code);
        Button btnConfirm = (Button) findViewById(R.id.confirmCodeBtn);
        etCode = (EditText) findViewById(R.id.etConfirmCode_text);
        session_id = getIntent().getStringExtra("session_id");
        session_shared = getIntent().getStringExtra("session_shared");
        session_documents = getIntent().getStringExtra("session_documents");

        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notification_id = getIntent().getStringExtra("notification_id");
        if (notification_id != null){
            Log.d("NOTIFICATION ID", notification_id);
            notificationManager.cancel(Integer.parseInt(notification_id));
        }

        Log.d(TAG, "SESSION_ID");
        if(session_id != null){
            Log.d(TAG, session_id);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String encrypted_data = encryption.basicRead(CodeActivity.this, CODE_FILENAME);
                    String userCode = encryption.decryptSymmetrically(encrypted_data, CODE_ALIAS);
                    Log.d("USER CODE", userCode);
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
        sharing.put("data_key", user_id);


        // Logic to encrypt symm key and data and send to firebase
        String sKey = "lingyejunAesTest";
        String encrypted_data = encryption.basicRead(CodeActivity.this, DATA_FILENAME);
        String json_data = encryption.decryptSymmetrically(encrypted_data, SYMMETRIC_ALIAS);
        String enc = "";
        try {
            String json_write = "";
            /*for (int i = 0; i < json_data.length(); i++){
                if(json_data.charAt(i) == '\"'){
                    json_write += "'";
                } else if(json_data.charAt(i) == '+' || json_data.charAt(i) == '\\' || json_data.charAt(i) == '-'){

                }
                else {
                    json_write += json_data.charAt(i);
                }
            }
            Log.d("OUTPUT JSON", json_write);*/
            json_write = json_data;
            enc = encryption.encryptUsingPassword(session_id, json_write);
            Log.d("ENCRYPTED DATA", enc);
        } catch(Exception e){
            e.printStackTrace();
        }
        MDB mongo = new MDB();
        mongo.updatePubKey(""+user_id, enc);
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
                go.putExtra("session_documents", session_documents);
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
    private void checkLoggedin(FirebaseUser user){
        if(user == null){
            Intent redirect = new Intent(CodeActivity.this, LoginActivity.class);
            startActivity(redirect);
        }
    }
}

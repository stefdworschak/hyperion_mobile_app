package com.example.hyperionapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class CodeActivity extends AppCompatActivity {
    /* Class to handle what happens when the 2-factor authentication is entered */

    // Declare and instantiate class constants
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String user_id = user.getUid();
    private final String CODE_ALIAS = "user_code_" + user_id;
    private final String CODE_FILENAME = user_id + "code.enc";
    private final String COLLECTION_NAME = "checkins";
    private final EncryptionService encryption = new EncryptionService();
    private final String DATA_FILENAME = user_id + "_hyperion.enc";
    private final RegisterActivity reg = new RegisterActivity();
    private final LoginActivity login = new LoginActivity();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MDBService mongo = new MDBService();

    // Declare and instantiate class attributes
    String session_id = "";
    //static String session_shared = "";
    private static String session_documents = "";
    private static String notification_id;

    private static String TAG = "SHARING SETTINGS MSG";
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Check if user is authenticated
        login.isAuthenticated(CodeActivity.this, user, false);
        // Render view
        setContentView(R.layout.activity_code);
        //Declare and instantiate UI elements
        Button btnConfirm = findViewById(R.id.confirmCodeBtn);
        EditText etCode = findViewById(R.id.etConfirmCode_text);

        // Grab information from intent
        session_id = getIntent().getStringExtra("session_id");
        session_documents = getIntent().getStringExtra("session_documents");

        // Declare and instantiate Notification manager
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        // Get current notificationID from intent
        notification_id = getIntent().getStringExtra("notification_id");
        // If the notificationID is not null
        if (notification_id != null){
            // Close the notification with the specified ID
            notificationManager.cancel(Integer.parseInt(notification_id));
        }

        // When the user clicks the confirm button
        btnConfirm.setOnClickListener((View v) -> {
            // Read and decrypt the user code from the encrypted local file
            String encrypted_data = encryption.basicRead(CodeActivity.this, CODE_FILENAME);
            String userCode = encryption.decryptSymmetric(encrypted_data, CODE_ALIAS);

            // If the entered code is the same as the saved code
            if(userCode.equalsIgnoreCase(etCode.getText().toString())) {
                // Call the method to share the user data
                shareUserData(session_id);
            } else {
                // Otherwise show an error message
                reg.showTopToast(getApplicationContext(),
                        "Wrong Code Entered. Please try again");
            }
        });
    }



    public void shareUserData(String session_id){
        /* Method to process sharing the encrypted user data
         * @param String session_id The current session ID
         * @return void
         */

        Log.d("DATA SHARING", "Process started");
        // Updating data from the official api reference
        // Reference: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data

        // Create a new HashMap (= JSON object in FirebaseFirestore) which will be used to
        // update the the existing data in FirebaseFirestore
        Map<String, Object> sharing = new HashMap<>();
        // Change the session_shared to 2 (=shared)
        // This should be the only place where this is possible unless the user has
        // "Share data now" enabled when creating the session
        sharing.put("session_shared", "2");
        // Create the data key field, setting it equal to the id of their FirebaseUser
        sharing.put("data_key", user_id);

        // Read the data from the encrypted local file and decrypt it
        String encrypted_data = encryption.basicRead(CodeActivity.this, DATA_FILENAME);
        String json_data = encryption.decryptSymmetric(encrypted_data, SYMMETRIC_ALIAS);

        // Load patient data from json data using Gson.fromJSON
        PatientDetails p;
        Gson gson = new Gson();
        // Check if the json_data is null to avoid errors
        if(json_data != null) {
            p = gson.fromJson(json_data, PatientDetails.class);
        } else {
            p = new PatientDetails();
        }

        // Update the session_shared on the latest Snapshot and the session in patientSessions
        Checkin check = p.findSessionById(session_id);
        Checkin c = p.getLatestSnapshot();
        c.setSession_shared(2);
        check.setSession_shared(2);
        p.setLatestSnapshot(c);
        p.updateSessionById(check);

        // Declare and instantiate a new variable that will store the re-encrypted data
        String enc = "";
        try {
            // Re-encrypt the data using the the session_id as key
            enc = encryption.encryptUsingPassword(session_id, json_data);
        } catch(Exception e){
            // Print StackTrace if error occurs
            e.printStackTrace();
        }

        // Store the encrypted data under the FirebaseUser id in MongoDB
        mongo.updateData(""+user_id, enc);
        // Reference: https://firebase.google.com/docs/firestore/manage-data/add-data#update-data
        // Update the FirebaseFirestore document with the updated information
        DocumentReference document = db.collection(COLLECTION_NAME).document(session_id);
        document.update(sharing)
                .addOnSuccessListener((Void aVoid) -> {
                    // If FirebaseFirestore document was successfully udpdated
                    // Redirect to the MainActivity
                    Intent go = new Intent(CodeActivity.this, MainActivity.class);
                    //go.putExtra("viewpager_position", 2);
                    startActivity(go);
        })
        .addOnFailureListener((@NonNull Exception e) -> {
            // If an error occurred write and error to the log
            Log.w(TAG, "Error updating Sharing Settings", e);
        });


    }
}

package com.example.hyperionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SingleSessionActivity extends AppCompatActivity {
    EncryptionClass encryption = new EncryptionClass();
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    String session_id;
    String session_status;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkLoggedin(FirebaseAuth.getInstance().getCurrentUser());
        session_id = getIntent().getStringExtra("session_id");
        session_status = getIntent().getStringExtra("session_status");
        setContentView(R.layout.activity_document);

        String encrypted_data = encryption.basicRead(SingleSessionActivity.this, DATA_FILENAME);
        String json_data = encryption.decryptSymmetrically(encrypted_data, SYMMETRIC_ALIAS);
        //String json_data = null;
        PatientDetails p;
        if (json_data != null) {
            p = gson.fromJson(json_data, PatientDetails.class);
        } else {
            System.out.println("JSON EMPTY");
            p = new PatientDetails();
        }
        Log.d("PATIENT NAME", p.getName());
        Log.d("SINGLE SESSION ID", session_id);
        Checkin session = p.findSessionById(session_id);

        TextView tvSessionID = findViewById(R.id.session_id_text);
        TextView tvSessionTime = findViewById(R.id.session_time_text);
        TextView tvSymptoms = findViewById(R.id.symptoms_text);
        TextView tvDiagnosis = findViewById(R.id.diagnosis_text);
        TextView tvRecommendation = findViewById(R.id.recommendation_text);
        TextView tvAttachments = findViewById(R.id.attachments_text);
        tvSessionID.setText(session.getSession_id());
        tvSessionTime.setText(""+session.getSession_checkin());
        tvSymptoms.setText(session.getSession_details().get("symptoms"));


        if(session_status.equals("Complete")) {
            if (session == null) {
                Log.d("SESSION", "IS NULL");
            } else {
                Log.d("SESSION DOC SIZE", "" + session.getSession_documents().size());
            }
            SessionDocument sessionRecord = null;
            List<SessionDocument> attachments = new ArrayList<>();
            for (int i = 0; i < session.getSession_documents().size(); i++) {
                SessionDocument doc = session.getSession_documents().get(i);
                Log.d("SESSION DOC NAME", doc.getDocument_name());
                if (doc.getDocument_name().equals("Session Record")) {
                    sessionRecord = doc;
                } else {
                    attachments.add(doc);
                }
            }
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference gsReference = storage.getReference().child("672f7114a9284273272dda6cf232dd939b7c81b4a001c684014f6e74b0933a04.ipynb");
            if (sessionRecord == null) {
                Log.d("SESSION DOC", "NULLPOINTER EX");
            }
        } else {
            tvDiagnosis.setText("No information yet.");
            tvRecommendation.setText("No information yet.");
            tvAttachments.setText("No information yet.");
        }
    }

    public String getDocumentHash(StorageReference gsReference){
        //StorageReference gsReference = storage.getReferenceFromUrl("gs://hyperion-260715.appspot.com/37440620a23cd3edd5b9389acb4a6ca6203227fe381909673a46b3ea59381085");
        final long ONE_MEGABYTE = 1024 * 1024;
        Log.d("STORAGE REFERENCE", "DONE");
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                try {
                    String x = "672f7114a9284273272dda6cf232dd939b7c81b4a001c684014f6e74b0933a04";
                    String sha256hex = Hashing.sha256()
                            .hashString(new String(bytes, "utf-8"), StandardCharsets.UTF_8)
                            .toString();
                } catch(UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    Log.d("ERROR", ex.getMessage());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                exception.printStackTrace();
                Log.d("ERROR", exception.getMessage());

            }
        });
        return null;
    }
    private void checkLoggedin(FirebaseUser user){
        if(user == null){
            Intent redirect = new Intent(SingleSessionActivity.this, LoginActivity.class);
            startActivity(redirect);
        }
    }
}

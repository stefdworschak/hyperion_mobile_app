package com.example.hyperionapp;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingleSessionActivity extends AppCompatActivity {
    // Declare and instantiate session constants
    final private EncryptionService encryption = new EncryptionService();
    final private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final private FirebaseStorage storage = FirebaseStorage.getInstance();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    final private Gson gson = new Gson();
    // Declare and instantiate session variables
    int validatedDocuments = 0;
    int documentsToValidate;
    int checkedDocuments = 0;
    String session_id;
    String session_status;
    TextView tvDiagnosis;
    TextView tvRecommendation;
    TextView tvAttachments;
    TextView tvVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Retrieve session data from Intent
        session_id = getIntent().getStringExtra("session_id");
        session_status = getIntent().getStringExtra("session_status");
        // Render view
        setContentView(R.layout.activity_document);

        // Load data from encrypted local file
        String encrypted_data = encryption.basicRead(SingleSessionActivity.this, DATA_FILENAME);
        String json_data = encryption.decryptSymmetric(encrypted_data, SYMMETRIC_ALIAS);
        // Convert it to a PatientDetails class instance
        PatientDetails p;
        if (json_data != null) {
            p = gson.fromJson(json_data, PatientDetails.class);
        } else {
            System.out.println("JSON EMPTY");
            p = new PatientDetails();
        }

        // Declare and Instantiate UI elements
        Checkin session = p.findSessionById(session_id);
        TextView tvSessionID = findViewById(R.id.session_id_text);
        TextView tvSessionTime = findViewById(R.id.session_time_text);
        TextView tvSymptoms = findViewById(R.id.symptoms_text);
        tvDiagnosis = findViewById(R.id.diagnosis_text);
        tvRecommendation = findViewById(R.id.recommendation_text);
        tvAttachments = findViewById(R.id.attachments_text);
        tvVerified = findViewById(R.id.verification_display);
        tvSessionID.setText(session.getSession_id());
        tvSessionTime.setText(""+session.getSession_checkin());
        tvSymptoms.setText(session.getSession_details().get("symptoms"));

        // Check if the session is either finished or ongoing
        if(session_status.equals("Complete") || session_status.equals("Data shared")) {
            // Get the number of documents to validate
            documentsToValidate = session.getSession_documents().size();
            // If there are any
            if(documentsToValidate > 0){
                // Loop through them and start the validation process
                for (int i = 0; i < documentsToValidate; i++) {
                    SessionDocument doc = session.getSession_documents().get(i);
                    if(doc.getDocument_name().equals("Session Record")){
                        validateDocument(doc, "session_record");
                    } else {
                        validateDocument(doc, "attachment");
                    }
                }
            } else {
                // If there are no documents show fallbacks and update verification message
                tvDiagnosis.setText("No information yet.");
                tvRecommendation.setText("No information yet.");
                tvAttachments.setText("No information yet.");
                tvVerified.setText("Verified by Hyperion");
                tvVerified.setBackground(getResources().getDrawable(R.drawable.rounded_verifcation_green));
            }

        } else {
            // If the session has not started show fallbacks and update verification message
            tvDiagnosis.setText("No information yet.");
            tvRecommendation.setText("No information yet.");
            tvAttachments.setText("No information yet.");
            tvVerified.setText("Verified by Hyperion");
            tvVerified.setBackground(getResources().getDrawable(R.drawable.rounded_verifcation_green));
        }
    }

    public void validateDocument(SessionDocument doc, String doctype){

        // Create a reference to the document file on FirebaseStorage
        StorageReference gsReference =  storage.getReference().child(doc.getDocument_hash() + doc.getDocument_type());
        try {
            // Declare
            File localFile = File.createTempFile("files", doc.getDocument_type().substring(1));
            gsReference.getFile(localFile).addOnSuccessListener((FileDownloadTask.TaskSnapshot taskSnapshot) -> {
                try {
                    // Reference: https://examples.javacodegeeks.com/core-java/io/fileinputstream/read-file-in-byte-array-with-fileinputstream/
                    // Read downloaded filed into a Bytearray
                    FileInputStream fin;
                    fin = new FileInputStream(localFile);
                    byte[] fileContent = new byte[(int)localFile.length()];
                    fin.read(fileContent);
                    // Create a hash from the file
                    String hash = Hashing.sha256()
                            .hashString(new String(fileContent, "utf-8"), StandardCharsets.UTF_8)
                            .toString();
                    // Check if the document to validate the session record or an attachement
                    // and call the respective methods to display them
                    if(doctype.equals("session_record")){
                        displaySessionRecord(fileContent);
                    } else {
                        appendDownloadLink(gsReference, doc);
                    }
                    // If the hash of the file is the same as the hashed content
                    if(hash.equals(doc.getDocument_hash())){
                        // Validate the hash against the smart contract
                        List<String> hashes = new ArrayList<>();
                        hashes.add(gson.toJson(doc, SessionDocument.class));
                        checkHashes(hashes, hash);
                    } else {
                        Log.d("STORAGE REFERENCE", "WRONG HASH");
                    }
                } catch(UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch(FileNotFoundException fileNotFoundEx) {
                    fileNotFoundEx.printStackTrace();
                } catch(IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }).addOnFailureListener((@NonNull Exception exception) -> {
                // When an error occurs print the stackTrace
                exception.printStackTrace();
            });
        } catch(IOException ioEx) {
            // When an error occurs print the stackTrace
            ioEx.printStackTrace();
        }
    }

    public void checkHashes(List<String> hashes, String validateHash){
        // Increment the number of checked documents
        checkedDocuments ++;
        // Create new HashMap to hold post request data
        HashMap<String, String> postData = new HashMap<>();
        postData.put("data_key", user_id);
        postData.put("hashes", hashes.toString());
        postData.put("action", "validateOne");
        // Retrieve the api URL from the config
        String url = BuildConfig.VALIDATION_API;
        // Create a new RequestQueue with Volley to handle HTTP requests
        RequestQueue queue = Volley.newRequestQueue(SingleSessionActivity.this);
        // Reference: https://developer.android.com/training/volley/requestqueue
        // Reference: https://stackoverflow.com/a/23221373
        // Create a StringRequest to send the POST Request
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                (String response) -> {
                    try {
                        // When a successful response is received
                        // Convert response to JSON objects and extract the validated hash
                        // This will be 0x0 if it is an invalid hash
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));
                        JSONArray jsonArray = new JSONArray(jsonData.getString("data"));
                        String validatedHash = jsonArray.get(0).toString().substring(2);
                        // If the validated and original hash match
                        if(validatedHash.equals(validateHash)) {
                            // Increment the validated documents counter
                            validatedDocuments++;
                            // If all documents have been validated
                            // This had to be implemented because of the async way of the application
                            if(checkedDocuments == documentsToValidate
                                    && documentsToValidate == validatedDocuments){
                                // Change the verification message to be verified
                                tvVerified.setText("Verified by Hyperion");
                                tvVerified.setBackground(getResources().getDrawable(R.drawable.rounded_verifcation_green));
                            }
                        } else {
                            // If any of the hashes are not validated set a negative verification message
                            TextView tvVerified = findViewById(R.id.verification_display);
                            tvVerified.setText("Some documents are not verifiable");
                            tvVerified.setBackground(getResources().getDrawable(R.drawable.rounded_verifcation_red));
                            Log.d("VOLLEY HASH 1", validatedHash);
                            Log.d("VOLLEY HASH 2", validateHash);
                        }
                    } catch(JSONException jsonEx){
                        // If an error occurs print the stackTrace
                        jsonEx.printStackTrace();
                    }
                },
                (VolleyError error) -> {
                    // Write error to log
                    Log.d("VOLLEY ERROR", error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("data", new JSONObject(postData).toString());
                Log.d("VOLLEY PARAMS", params.toString());
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void displaySessionRecord(byte[] document){
        try {
            // Convert the Session document content to a JSONObject
            String jsonString = new String(document, "utf-8");
            JSONObject jsonObj = new JSONObject(jsonString);
            // Set the information on the view
            tvDiagnosis.setText(jsonObj.get("patient_diagnosis").toString());
            tvRecommendation.setText(jsonObj.get("patient_recommendation").toString());
        } catch(Exception e){
            // If an error occurs print the stackTrace and set fallback messages
            e.printStackTrace();
            tvDiagnosis.setText("Information could not be retrieved.");
            tvRecommendation.setText("Information could not be retrieved.");
        }
    }

    // Reference: https://firebase.google.com/docs/storage/android/download-files#download_data_via_url
    @TargetApi(23)
    public void appendDownloadLink(StorageReference storageRef, SessionDocument document){
        // Use the FirebaseStorage reference to generate a download URL
        storageRef.getDownloadUrl().addOnSuccessListener((Uri uri) -> {
            // Create new TextView to store the link to the file
            TextView tvLink = new TextView(SingleSessionActivity.this);
            String linkText = document.getDocument_name() + document.getDocument_type();
            tvLink.setBackground(getResources().getDrawable(R.drawable.download_textview));
            tvLink.setText(linkText);
            tvLink.setGravity(Gravity.CENTER_VERTICAL);
            tvLink.setPadding(20, 10,20,10);
            tvLink.setTextSize(20);
            // Append TextView to the Layout
            LinearLayout lyLinear = findViewById(R.id.document_layout);
            lyLinear.addView(tvLink);

            // Set OnClickListener for the TextView to trigger file download
            tvLink.setOnClickListener((View v)->{
                // Reuse the showTopToast method from the RegisterActivity
                RegisterActivity reg = new RegisterActivity();
                reg.showTopToast(SingleSessionActivity.this, "Download Started");
                // Define the new DownloadManager.Request
                DownloadManager.Request mRqRequest = new DownloadManager.Request(uri);
                mRqRequest.setDescription("This is Test File");
                mRqRequest.setTitle(linkText);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mRqRequest.allowScanningByMediaScanner();
                    mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                mRqRequest.setDestinationInExternalFilesDir(getApplicationContext(), "/Downloads", linkText);

                // Declare and instantiate the DownloadManager
                DownloadManager manager1 = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                // Enqueue/start the download and show a Toast when downloaded or failed
                Objects.requireNonNull(manager1).enqueue(mRqRequest);
                if (DownloadManager.STATUS_SUCCESSFUL == 8) {
                    reg.showTopToast(SingleSessionActivity.this, "Download Completed");
                } else {
                    reg.showTopToast(SingleSessionActivity.this, "A download error occurred");
                }
            });
        }).addOnFailureListener((@NonNull Exception exception) -> {
            // Write errors to the log
            Log.d("DOWNLOAD LINK ERROR", exception.getMessage());
        });
    }
}

package com.example.hyperionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateCodeActivity extends AppCompatActivity {
    /* Class to handle creating a new 2-factor authentication code */

    //Declare and instantiate class variables
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_id = user.getUid();
    private final String CODE_ALIAS = "user_code_" + user_id;
    private final String CODE_FILENAME = user_id + "code.enc";
    EncryptionService encryption = new EncryptionService();
    RegisterActivity reg = new RegisterActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Render view
        setContentView(R.layout.activity_create_code);

        // Declare and instantiate UI elements
        Button btnConfirm = findViewById(R.id.confirmCodeBtn);
        EditText etCode = findViewById(R.id.etCreateCode_text);

        // When the user clicks the button
        btnConfirm.setOnClickListener((View v) -> {
            // Get the value of the TextEdit
            String newUserCode = etCode.getText().toString();
            // Save the value in an encrypted local file, different to the data file
            String saveMsg = encryption.saveString(
                    newUserCode, CODE_ALIAS, getApplicationContext(), CODE_FILENAME);
            // If the code was saved successfully
            if (saveMsg.equals("File saved successfully!")){
                // Declare a new intent
                Intent go;
                // If the user's email is verified
                if(user.isEmailVerified()){
                    // Redirect to MainActivity
                    go = new Intent(CreateCodeActivity.this, MainActivity.class);
                    //go.putExtra("viewpager_position", 2);
                } else {
                    // If it is not verified and the user is not empty
                    if(user != null){
                        // Log the user out
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                    }
                    // Redirect to LoginActivity
                    go = new Intent(CreateCodeActivity.this, LoginActivity.class);
                }
                // Start the intent
                startActivity(go);
            } else {
                // If there was an error saving the code, show an error message
                reg.showTopToast(getApplicationContext(),
                        "Could not save the code. Please try again");
            }
        });

    }
}

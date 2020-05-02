package com.example.hyperionapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

/*
 * References:
 * https://firebase.google.com/docs/auth/android/manage-users
 * https://firebase.google.com/docs/auth/android/password-auth
 */
public class RegisterActivity extends AppCompatActivity {
    /* Class to handle all new user registration logic */
    // Declare class attributes
    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;
    // Declare and instantiate class constants
    final String TAG = "CREATE USER ACCOUNT";
    private String ASYMMETRIC_ALIAS_ROOT = "hyperion_asymmetric_";
    // Declare and instantiate Firebase Instance
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Declare and grab the current user from the Firebase Instance
        FirebaseUser user = mAuth.getCurrentUser();
        // If a user is logged in
        if (user != null) {
            if(user.isEmailVerified()) {
            // Redirect to MainActivity
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            // Otherwise log the user out
            } else {
                mAuth.signOut();
            }
        }

        // Render RegisterActivity
        setContentView(R.layout.activity_create_user);
        // Declare and instatiate button to create new account
        Button btnCreate = (Button) findViewById(R.id.btn_create_user);
        //When button is clicked
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate EditText elements
                etEmail = (EditText) findViewById(R.id.create_user_email);
                etPassword = (EditText) findViewById(R.id.create_password_input);
                etConfirmPassword = (EditText) findViewById(R.id.confirm_password_input);

                //Check that the two passwords entered match
                if(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    // Call class method to create the account
                    createAccount(etEmail.getText().toString(), etPassword.getText().toString());
                } else {
                    // Show message if the passwords not match
                    showTopToast(RegisterActivity.this,
                            "Passwords don't match");
                }
            }
        });
    }

    private void createAccount(String email, String password) {
        /* Create a new Firebase User account
         * @param String email The Email address to be used for the new user account
         * @param String password The Password to be used for the new user account
         * @return void
         */

        // If the form has any errors stop method
        if (!validateForm()) {
            // Shows message at the top of the screen if there are any errors
            showTopToast(RegisterActivity.this,
                    "Please make sure that you have filled in all of the required fields.");
            return;
        }

        // Create a new FirebaseAuth user account from the existing Firebase Instance
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If the account was created successfully
                        if (task.isSuccessful()) {
                            // Grab the new user from the existing FirebaseAuth Instance
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Successfully created user!");
                            // Verify again that the user is signed in
                            // This is to avoid issues with retrieving the user account
                            if(user != null) {
                                // Call class method to send a verification email to the new user
                                sendEmailVerification(user);
                                // Declare and Instantiate the Encryption class which handles
                                // all functionality for encrypting/decrypting data and
                                // saving/reading the data from the local data file
                                EncryptionClass encryption = new EncryptionClass();
                                // Create a new private/public key pair for the user
                                // and store them in the Android Keystore
                                String public_key = encryption.createAndStoreKeys(
                                        getApplicationContext(),
                                        ASYMMETRIC_ALIAS_ROOT + user.getUid());
                                // Redirect the user to the CreateCodeActivity with which the
                                // User will create their 2-factor authentication key
                                Intent createCodeIntent = new Intent(
                                        RegisterActivity.this, CreateCodeActivity.class);
                                startActivity(createCodeIntent);

                            // If the user is not logged in
                            } else {
                                // Redirect to the LoginActivity
                                Intent loginIntent = new Intent(
                                        RegisterActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        } else {
                            // If user creation fails, display a message to the user.
                            Log.e(TAG, "User could not be created", task.getException());
                            showTopToast(RegisterActivity.this,
                                        task.getException().getMessage());
                        }
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        /* Sends a verification email to a newly created Firebase User account
         * @return void
         */

        // Send a verification email to a new FirebaseAuth user account using its
        // sendEmailVerification method
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // If the email was sent successfully
                        if (task.isSuccessful()) {
                            // Show message to user
                            showTopToast(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail());
                        } else {
                            // Else show an err notification
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            // Show error message
                            showTopToast(RegisterActivity.this,
                                    "Failed to send verification email.");
                        }
                    }
                });
    }

    private Boolean validateForm() {
        /* Validates that all form fields are correctly filled in
         * @return Boolean
         */

        // Declare and instantiate variables
        boolean valid = true;
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etConfirmPassword.getText().toString();

        // Check if the email field is empty
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        // Check if the first password field is empty
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        // Check if the second password field is empty
        if (TextUtils.isEmpty(passwordConfirm)) {
            etConfirmPassword.setError("Required.");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        return valid;
    }

    public static void showTopToast(Context context, String message){
        /* Handles showing a
         * @param String message The message to be displayed by the Toast
         * @return void
         */

        // Declare and instantiate Toast using the message passed to the method
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        // Set gravity to display the Toast at the top instead of the bottom in case the
        // keyboard is open which would overwrite it
        toast.setGravity(
                Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
        // Display toast
        toast.show();
    }
}

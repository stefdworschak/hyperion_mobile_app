package com.example.hyperionapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    /* Class to handle all user authentication logic */
    // Declare class attributes
    EditText etEmail;
    EditText etPassword;
    // Declare and instantiate class constants
    final String TAG = "CREATE USER ACCOUNT";
    // Declare and instantiate FirebaseAuth Instance
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Declare and instantiate RegisterActivity to reuse showTopToast method
    RegisterActivity reg = new RegisterActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Declare and grab the current user from the FirebaseAuth Instance
        FirebaseUser user = mAuth.getCurrentUser();
        // Check if user is authenticated already and redirect accordingly
        isAuthenticated(LoginActivity.this, user, false);
        // Render LoginActivity
        setContentView(R.layout.activity_login);
        // Declare and instantiate Button and TextView for the login page
        Button btnSave = findViewById(R.id.btn_login);
        TextView tvCreateUser = findViewById(R.id.link_signup);

        // Set OnClickListener for the TextView to bring the user to the RegisterActivity
        tvCreateUser.setOnClickListener((View v) -> {
            // Redirect to the RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        // Set OnClickListener for the Button to start the login process
        btnSave.setOnClickListener((View v) -> {
            // Instantiate Email and Password text fields
            etEmail = findViewById(R.id.input_login_email);
            etPassword = findViewById(R.id.input_login_password);
            // Call the user authentication method
            authenticateUser(etEmail.getText().toString(), etPassword.getText().toString());
        });

    }

    private void authenticateUser(String email, String password) {
        /* Authenticate the user with email address and password
         * @param String email The Email address to be used for the user authentication
         * @param String password The Password to be used for the user authentication
         * @return void
         */

        // If the form has any errors stop method
        if (!validateForm()) {
            // Shows message at the top of the screen if there are any errors
            reg.showTopToast(LoginActivity.this,
                    getString(R.string.error_fill_in_all_fields));
            return;
        }

        // Reference: https://firebase.google.com/docs/auth/android/password-auth#sign_in_a_user_with_an_email_address_and_password
        // Sign into an existing FirebaseAuth user account using email address and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (@NonNull Task<AuthResult> task) -> {
                    // If the authentication was successful
                    if (task.isSuccessful()) {
                        // Grab the logged-in user from the existing FirebaseAuth Instance
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Validate user is authenticated and redirect accordingly
                        isAuthenticated(getApplicationContext(), user, true);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "Login NOT successful!", task.getException());
                        reg.showTopToast(LoginActivity.this,
                                getString(R.string.error_login_failed));
                    }
                });
    }

    public void signOut() {
        /* Log user out of the existing session
         * @return void
         */

        // Reference: https://firebase.google.com/docs/auth/android/password-auth#next_steps
        // End the current FirebaseAuth user session
        mAuth.signOut();
        // Redirect to the LoginActivity
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
    }

    // Reference: https://www.youtube.com/watch?v=cnD_7qFeZcY
    private boolean validateForm() {
        /* Validates that all form fields are correctly filled in
         * @return Boolean
         */
        //Declare and instantiate variables
        boolean valid = true;
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // Check if the email field is empty
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        // Check if the password field is empty
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    public void isAuthenticated(Context context, FirebaseUser user, boolean redirect){
        /* Method to check if user is authenticated
         * @param Context context The context (activity the method is called from)
         * @param FirebaseUser user The FirebaseUser which should be validated
         * @param boolean redirect True if the user should be redirected to MainActivity after success
         * @return boolean
         */

        // Reference: https://firebase.google.com/docs/auth/android/manage-users#get_the_currently_signed-in_user
        // If the user is authenticated
        if(user != null){
            // Reference: https://firebase.google.com/docs/auth/android/manage-users#get_a_users_profile
            // If the user's email is verified
            if(user.isEmailVerified()){
                if(redirect) {
                    // Redirect to the MainActivity
                    Intent mainActivity = new Intent(context, MainActivity.class);
                    startActivity(mainActivity);
                }
            } else {
                // If the email is not verified logout and redirect to the LoginActivity
                signOut();
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
                // Show error message
                reg.showTopToast(LoginActivity.this,
                        getString(R.string.error_email_not_verified));
            }
        } else {
            if(redirect) {
                // If the user is not authenticated redirect to the LoginActivity
                Intent loginActivity = new Intent(context, LoginActivity.class);
                startActivity(loginActivity);
                // Show error message
                reg.showTopToast(LoginActivity.this,
                        getString(R.string.error_login_failed));
            }
        }
    }

}

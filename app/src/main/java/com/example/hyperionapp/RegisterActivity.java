package com.example.hyperionapp;

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

/*
 * References:
 * https://firebase.google.com/docs/auth/android/manage-users
 * https://firebase.google.com/docs/auth/android/password-auth
 */

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String TAG = "CREATE USER ACCOUNT";
    private String ASYMMETRIC_ALIAS_ROOT = "hyperion_asymmetric_";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d("CURRENT USER", user.toString());
            // User is signed in
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
        }

        setContentView(R.layout.activity_create_user);
        Button btnCreate = (Button) findViewById(R.id.btn_create_user);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail = (EditText) findViewById(R.id.create_user_email);
                etPassword = (EditText) findViewById(R.id.create_password_input);
                etConfirmPassword = (EditText) findViewById(R.id.confirm_password_input);
                if(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    createAccount(etEmail.getText().toString(), etPassword.getText().toString());
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                              "Passwords don't match", Toast.LENGTH_SHORT);
                    toast.setGravity(
                            Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.show();
                }
            }
        });

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Create User success, send user to create a new code
                            sendEmailVerification();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null) {
                                EncryptionClass encryption = new EncryptionClass();
                                String public_key = encryption.createAndStoreKeys(
                                        getApplicationContext(),
                                        ASYMMETRIC_ALIAS_ROOT + user.getUid());
                                MDB mongo = new MDB();
                                mongo.updatePubKey(""+user.getUid(), public_key);
                                Intent createCodeIntent = new Intent(
                                        RegisterActivity.this, CreateCodeActivity.class);
                                startActivity(createCodeIntent);
                            } else {
                                Intent loginIntent = new Intent(
                                        RegisterActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            System.out.println(task.getException().getMessage());
                            Toast toast = Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(
                                    Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
                            toast.show();
                        }
                    }
                });
    }

    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required.");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        String passwordConfirm = etConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(passwordConfirm)) {
            etConfirmPassword.setError("Required.");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        return valid;
    }
}

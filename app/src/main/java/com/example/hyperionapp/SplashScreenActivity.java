package com.example.hyperionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    //Declare and instantiate a new FirebaseAuth Instance
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Render the Splashscreen layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        // Create a Handler with delay to delay starting the next activity by 1000ms
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Declare and grab the current user from the FirebaseAuth Instance
                FirebaseUser user = mAuth.getCurrentUser();

                // If a user is logged in
                if (user != null) {
                    // Redirect to the MainActivity
                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivity);
                } else {
                    // Else redirect to the LoginActivity
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                }

            }

        }, 1000);
    }
}

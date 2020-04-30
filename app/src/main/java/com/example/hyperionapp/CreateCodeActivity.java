package com.example.hyperionapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hyperionapp.ui.main.CheckinFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateCodeActivity extends AppCompatActivity {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_id = user.getUid();
    final String CODE = "1234";
    EditText etCode;
    private final int NOTIFICATION_ID = 606;
    private final String CHANNEL_ID = "fcm_notification";
    private final String CODE_ALIAS = "user_code_" + user_id;
    private final String CODE_FILENAME = user_id + "code.enc";
    String codeUsage;
    EncryptionClass encryption = new EncryptionClass();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_code);
        Button btnConfirm = (Button) findViewById(R.id.confirmCodeBtn);
        etCode = (EditText) findViewById(R.id.etCreateCode_text);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserCode = (String) etCode.getText().toString();
                String saveMsg = encryption.saveString(
                        newUserCode, CODE_ALIAS, getApplicationContext(), CODE_FILENAME);
               if(saveMsg.equals("File saved successfully!")){
                   Intent go;
                   Log.d("EMAIL VERIFICATION", Boolean.toString(user.isEmailVerified()));
                   if(user.isEmailVerified()){
                       go = new Intent(CreateCodeActivity.this, MainActivity.class);
                   } else {
                       if(user != null){
                           FirebaseAuth mAuth = FirebaseAuth.getInstance();
                           mAuth.signOut();
                       }
                       go = new Intent(CreateCodeActivity.this, LoginActivity.class);
                   }
                   // you pass the position you want the viewpager to show in the extra,
                   // please don't forget to define and initialize the position variable
                   // properly
                   go.putExtra("viewpager_position", 2);
                   startActivity(go);
               } else {
                   Toast.makeText(getApplicationContext(),
                             "Could not save the code. Please try again", Toast.LENGTH_SHORT);
               }
            }
        });

    }



    static void shareUserData(){
        Log.d("SHARING", "SHARE DATA!!!!");
    }
}

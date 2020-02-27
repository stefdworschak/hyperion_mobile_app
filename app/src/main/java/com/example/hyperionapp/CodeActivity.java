package com.example.hyperionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.hyperionapp.ui.main.CheckedInFragment;
import com.example.hyperionapp.ui.main.CheckinFragment;

public class CodeActivity extends AppCompatActivity {
    final String CODE = "1234";
    EditText etCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Button btnConfirm = (Button) findViewById(R.id.confirmCodeBtn);
        etCode = (EditText) findViewById(R.id.etConfirmCode);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CODE.equalsIgnoreCase(etCode.getText().toString())){
                    Intent go = new Intent(CodeActivity.this, MainActivity.class);

                    // you pass the position you want the viewpager to show in the extra,
                    // please don't forget to define and initialize the position variable
                    // properly
                    go.putExtra("viewpager_position", 2);

                    startActivity(go);
                }
            }
        });

    }
}

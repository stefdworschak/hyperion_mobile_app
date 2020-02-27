package com.example.hyperionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hyperionapp.ui.main.SectionsPagerAdapter;
import com.google.api.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
    For JSON to Object coversion:
    https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/

 */

public class MainActivity extends AppCompatActivity {

    Gson gson = new Gson();
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_";
    final String ASYMMETRIC_ALIAS = "hyperion_asymmetric_";
    private PatientDetails patientModel;
    private EncryptionClass encryption = new EncryptionClass();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int view_page = intent.getIntExtra("viewpager_position", 1);
        System.out.println("VIEWPAGE: " + view_page);

        // Load application logic
        setContentView(R.layout.activity_main);

        patientModel = ViewModelProviders.of(this).get(PatientDetails.class);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(view_page);

        String encrypted_data = encryption.basicRead(MainActivity.this, "hyperion.enc");
        System.out.println("LOADING USERID DATA");
        System.out.println(user_id);
        System.out.println("Encrypted Data");
        System.out.println(encrypted_data);

        String json_data = encryption.decryptSymmetrically(encrypted_data, SYMMETRIC_ALIAS + user_id);


        PatientDetails p;
        if(json_data != null) {
            System.out.println("FOUND JSON");
            p = gson.fromJson(json_data, PatientDetails.class);
        } else {
            System.out.println("JSON EMPTY");
            p = new PatientDetails();
        }

        System.out.println("DATE OF BIRTH LOADED");
        System.out.println(p.getDateOfBirth());

        patientModel.setPersonalDetails(
                p.getName(), p.getEmail(), p.getDateOfBirth(), p.getAddress(), p.getAddress2(),
                p.getCity(),p.getPostCode(),p.getPPSNumber(),p.getInsurance()
        );
        patientModel.setMedicalDetails(
                p.getBloodType(), p.getAllergies(), p.getTubercolosis(), p.getDiabetes(),
                p.getHeartCondition(), p.getGloucoma(), p.getEpilepsy(), p.getDrugAlcoholAbuse(),
                p.getSmoker(), p.getCancer(), p.getOtherConditions(), p.getMedications(),
                p.getHeight(), p.getWeight(), p.getRegisteredGP()
        );


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.encryption:
                //Do something
                return true;
            case R.id.change_password:
                //Do something
                return true;
            case R.id.logout:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
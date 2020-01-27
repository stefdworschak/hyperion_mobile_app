package com.example.hyperionapp;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hyperionapp.ui.main.SectionsPagerAdapter;
import com.google.api.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private PatientDetails patientModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        File file=new File("hyperion_local.db");
        String str = readFile(this,"hyperion.pem");
        Toast.makeText(this.getApplicationContext(), str ,Toast.LENGTH_SHORT).show();


        // Load application logic
        setContentView(R.layout.activity_main);

        patientModel = ViewModelProviders.of(this).get(PatientDetails.class);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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
            case R.id.logout:
                //Do something
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Code helper:
    // https://stackoverflow.com/questions/12421814/how-can-i-read-a-text-file-in-android
    public static String readFile(Activity a, String filename){
        String str = "";
        String line;
        try {
            File file = new File(a.getFilesDir(), filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                str += line + "\n";
            }
            br.close();
        } catch(FileNotFoundException fileNotFoundError){
            System.out.println(fileNotFoundError);
        } catch(IOException ioException){
            System.out.println(ioException);
        }
        return str;
    }

}
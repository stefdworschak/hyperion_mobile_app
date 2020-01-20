package com.example.hyperionapp.ui.main;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.hyperionapp.DatePickerFragment;
import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.R;
import com.example.hyperionapp.SqliteFeeder;
import com.example.hyperionapp.SqliteHelper;

import org.w3c.dom.Text;

import java.security.KeyPair;
import java.util.Calendar;


/*
    References:
    SQLITE: https://developer.android.com/training/data-storage/sqlite
    SPINNER: https://developer.android.com/guide/topics/ui/controls/spinner

 */

public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_details, container, false);
        final Spinner spDays = (Spinner) v.findViewById(R.id.spinner);
        Spinner spMonths = (Spinner) v.findViewById(R.id.spinner2);
        Spinner spYears = (Spinner) v.findViewById(R.id.spinner3);
        Button btnSave = (Button) v.findViewById(R.id.button);
        Button btnCreateKeys = (Button) v.findViewById(R.id.button4);

        btnCreateKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EncryptionClass encryption = new EncryptionClass();
                KeyPair keys = encryption.generateKeys();
                String publicKey = keys.getPublic().toString();
                Toast.makeText(getContext(), publicKey, Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gets the data repository in write mode
                SqliteFeeder dbHelper = new SqliteFeeder(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                EditText tvFullname = (EditText) getActivity().findViewById(R.id.editText);
                String fullname = (String) tvFullname.getText().toString();
                EditText tvEmail = (EditText) getActivity().findViewById(R.id.editText2);
                String email = (String) tvEmail.getText().toString();
                EditText tvAddress = (EditText) getActivity().findViewById(R.id.editText4);
                String address = (String) tvAddress.getText().toString();

                //https://stackoverflow.com/questions/1947933/how-to-get-spinner-value
                //Concatenate Date
                Spinner tvSelectedDays = (Spinner) getActivity().findViewById(R.id.spinner);
                String spSelDays = (String) tvSelectedDays.getSelectedItem().toString();
                Spinner tvSelectedMths = (Spinner) getActivity().findViewById(R.id.spinner2);
                String spSelMths = (String) tvSelectedMths.getSelectedItem().toString();
                Spinner tvSelectedYears = (Spinner) getActivity().findViewById(R.id.spinner3);
                String spSelYears = (String) tvSelectedYears.getSelectedItem().toString();
                String dob = spSelYears + '-' + spSelMths + '-' + spSelDays;

                ContentValues values = new ContentValues();
                values.put(SqliteHelper.FeedEntry.COLUMN_NAME_FULLNAME, fullname);
                values.put(SqliteHelper.FeedEntry.COLUMN_NAME_EMAIL, email);
                values.put(SqliteHelper.FeedEntry.COLUMN_NAME_DOB, dob);
                values.put(SqliteHelper.FeedEntry.COLUMN_NAME_ADDRESS, address);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(SqliteHelper.FeedEntry.TABLE_NAME, null, values);
                System.out.println(newRowId);
                Toast.makeText(getContext(),"Data Saved Successfully.",Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.days_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDays.setAdapter(adapter);

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        spDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spDays.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.months_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonths.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.years_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYears.setAdapter(adapter3);

        return v;
    }

}


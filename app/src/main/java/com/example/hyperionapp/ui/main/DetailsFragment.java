package com.example.hyperionapp.ui.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.MainActivity;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
    References:
    SQLITE: https://developer.android.com/training/data-storage/sqlite
    SPINNER: https://developer.android.com/guide/topics/ui/controls/spinner

 */

public class DetailsFragment extends Fragment {
    DatePickerDialog picker;
    EditText etDob;
    TextInputLayout elDob;
    private PatientDetails patientModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_details_new, container, false);
        patientModel = ViewModelProviders.of(this).get(PatientDetails.class);

        elDob = (TextInputLayout) v.findViewById(R.id.dob_text_input);
        etDob = (TextInputEditText) v.findViewById(R.id.dob_edit_text);
        etDob.setInputType(InputType.TYPE_NULL);
        Button btnSave = (Button) v.findViewById(R.id.save_button);
        Button btnCreateKeys = (Button) v.findViewById(R.id.create_keys_button);

        btnCreateKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EncryptionClass encryption = new EncryptionClass();
                String publicKey = encryption.createAndStoreKeys(getContext());
                System.out.println(publicKey);

                String newPk = encryption.readPublicKey("hyperion");
                System.out.println("PUBLIC KEY RETRIEVED");
                System.out.println(newPk);

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a = getActivity();

                EditText etFullname = (EditText) a.findViewById(R.id.name_edit_text);
                EditText etEmail = (EditText) a.findViewById(R.id.email_edit_text);
                EditText etAddress = (EditText) a.findViewById(R.id.address_edit_text);
                EditText etAddress2 = (EditText) a.findViewById(R.id.address2_edit_text);
                EditText etCity = (EditText) a.findViewById(R.id.city_edit_text);
                EditText etPostCode = (EditText) a.findViewById(R.id.post_code_edit_text);
                EditText etDOB = (EditText) a.findViewById(R.id.dob_edit_text);
                EditText etPPSNumber = (EditText) a.findViewById(R.id.pps_edit_text);
                EditText etInsurance = (EditText) a.findViewById(R.id.insurance_edit_text);


                String fullname = (String) etFullname.getText().toString();
                String email = (String) etEmail.getText().toString();
                String address = (String) etAddress.getText().toString();
                String address2 = (String) etAddress2.getText().toString();
                String city = (String) etCity.getText().toString();
                String postcode = (String) etPostCode.getText().toString();
                String strDOB = (String) etDOB.getText().toString();
                Date dob = null;
                try {
                    dob = new SimpleDateFormat("dd/MM/yyyy").parse(strDOB);
                } catch(ParseException e){
                    System.out.println("PARSE EXCEPTION: " + e.getMessage());
                }
                String ppsnumber = (String) etPPSNumber.getText().toString();
                String insurance = (String) etInsurance.getText().toString();

                patientModel.setPersonalDetails(
                        fullname,
                        email,
                        dob,
                        address,
                        address2,
                        city,
                        postcode,
                        ppsnumber,
                        insurance
                );
            }
        });
        //Partially adapted from here:
        //https://www.tutlane.com/tutorial/android/android-datepicker-with-examples

        etDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }
                            }, year, month, day);
                    picker.show();
                }
            }
        });
        //Partially adapted from here:
        //https://www.tutlane.com/tutorial/android/android-datepicker-with-examples
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        return v;
    }
}


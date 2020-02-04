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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.MainActivity;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.PatientRecord;
import com.example.hyperionapp.R;
import com.example.hyperionapp.databinding.FragmentDetailsNewBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/*
    References:
    SQLITE: https://developer.android.com/training/data-storage/sqlite
    SPINNER: https://developer.android.com/guide/topics/ui/controls/spinner
    DATA BINDING: https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3
    DATA BINDING with FRAGMENT: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment

 */

public class DetailsFragment extends Fragment {
    Gson gson = new Gson();
    private PatientDetails patientModel;
    private FragmentDetailsNewBinding fragmentDetailsNewBinding;

    DatePickerDialog picker;
    EditText etDob;
    TextInputLayout elDob;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

    PatientRecord p;
    final String SYMMETRIC_ALIAS = "hyperion_symmetric";
    final String ASYMMETRIC_ALIAS = "hyperion_asymmetric";
    EncryptionClass encryption = new EncryptionClass();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentDetailsNewBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_details_new, container, false);
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        View v = fragmentDetailsNewBinding.getRoot();

        fragmentDetailsNewBinding.setPatientModel(patientModel);

        elDob = (TextInputLayout) v.findViewById(R.id.dob_text_input);
        etDob = (TextInputEditText) v.findViewById(R.id.dob_edit_text);
        etDob.setInputType(InputType.TYPE_NULL);
        Button btnSave = (Button) v.findViewById(R.id.save_button);

        etDob.setText(dateFormat.format(patientModel.getDateOfBirth()));

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
                                    cldr.set(year, monthOfYear, dayOfMonth);
                                    cldr.getTime();
                                    Date dob = (Date) cldr.getTime();
                                    patientModel.setDateOfBirth(dob);
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
                picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                cldr.set(year, monthOfYear, dayOfMonth);
                                cldr.getTime();
                                Date dob = (Date) cldr.getTime();
                                patientModel.setDateOfBirth(dob);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext());
            }
        });

        return v;
    }
}


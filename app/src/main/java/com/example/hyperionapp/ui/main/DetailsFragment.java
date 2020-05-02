package com.example.hyperionapp.ui.main;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hyperionapp.Checkin;
import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.databinding.FragmentDetailsNewBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/*
    References:
    DATA BINDING: https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3
    DATA BINDING with FRAGMENT: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment

 */

public class DetailsFragment extends Fragment {
    /* Fragment UI logic for adding the patient's personal details */

    // Declare class variables
    private PatientDetails patientModel;
    private FragmentDetailsNewBinding fragmentDetailsNewBinding;
    DatePickerDialog picker;

    // Declare and instantiate class constants
    final private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    final EncryptionClass encryption = new EncryptionClass();
    final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Reference1: https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3
        // Reference2: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment
        // Create the data Data Binding model used for capturing the information from the UI
        // without having to write individual onChangeListeners for each field apart from a few exceptions
        fragmentDetailsNewBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_details_new, container, false);
        // Instantiate viewModel
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        // Create view from Binding and set the Binding's data to the patientModel
        View v = fragmentDetailsNewBinding.getRoot();
        fragmentDetailsNewBinding.setPatientModel(patientModel);

        TextInputLayout elDob = (TextInputLayout) v.findViewById(R.id.dob_text_input);
        EditText etDob = (TextInputEditText) v.findViewById(R.id.dob_edit_text);
        etDob.setInputType(InputType.TYPE_NULL);
        Button btnSave = (Button) v.findViewById(R.id.save_button);
        Date dob = patientModel.getDateOfBirth();
        if(dob != null) {
            etDob.setText(dateFormat.format(dob));
        }

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
                            (DatePicker view, int yr, int monthOfYear, int dayOfMonth) -> {
                                etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + yr);
                                cldr.set(yr, monthOfYear, dayOfMonth);
                                cldr.getTime();
                                Date dob = (Date) cldr.getTime();
                                patientModel.setDateOfBirth(dob);
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
                        (DatePicker view, int yr, int monthOfYear, int dayOfMonth) -> {
                            etDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + yr);
                            cldr.set(yr, monthOfYear, dayOfMonth);
                            cldr.getTime();
                            Date dob = (Date) cldr.getTime();
                            patientModel.setDateOfBirth(dob);
                        }, year, month, day);
                picker.show();
            }
        });

        btnSave.setOnClickListener((View view) -> encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME));
        // Render view
        return v;
    }
}


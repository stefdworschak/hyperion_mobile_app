package com.example.hyperionapp.ui.main;

import android.app.Activity;
import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyperionapp.EncryptionClass;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.databinding.FragmentPatientDetailsBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.security.PrivateKey;

import com.example.hyperionapp.databinding.FragmentDetailsNewBinding;

/*
    Reference for data-binding:
    https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3


 */


public class PatientDetailsFragment extends Fragment {

    private Gson gson = new Gson();
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String ASYMMETRIC_ALIAS = "hyperion_asymmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    private EncryptionClass encryption = new EncryptionClass();
    private PatientDetails patientModel;
    private FragmentPatientDetailsBinding fragmentPatientDetailsBinding;
    Button btnSave;
    Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentPatientDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_patient_details, container, false);
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        View v = fragmentPatientDetailsBinding.getRoot();
        fragmentPatientDetailsBinding.setPatientModel(patientModel);

        btnSave = (Button) v.findViewById(R.id.save_button2);
        spinner = (Spinner) v.findViewById(R.id.bloodtype_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.bloodtype_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Select Blood Type");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                patientModel.setBloodType(parent.getItemAtPosition(position).toString());
                System.out.println("SPINNER");
                System.out.println(parent.getItemAtPosition(position).toString() + " selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME);
            System.out.println(saveMsg);
            }
        });

        return v;

    }
}
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;

import java.security.PrivateKey;

import com.example.hyperionapp.databinding.FragmentDetailsNewBinding;

/*
    Reference for data-binding:
    https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3


 */


public class PatientDetailsFragment extends Fragment {


    private Gson gson = new Gson();

    final String SYMMETRIC_ALIAS = "hyperion_symmetric";
    final String ASYMMETRIC_ALIAS = "hyperion_asymmetric";
    private EncryptionClass encryption = new EncryptionClass();
    private PatientDetails patientModel;
    private FragmentPatientDetailsBinding fragmentPatientDetailsBinding;
    Button btnSave;
    Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View v = inflater.inflate(R.layout.fragment_patient_details, container, false);
        //patientModel = ViewModelProviders.of(this).get(PatientDetails.class);

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity a = getActivity();
                double height = 0.0;
                double weight = 0.0;
                //Medical Details
                /*EditText etAllergies = (EditText) a.findViewById(R.id.allergies_edit_text);
                System.out.println("ALLERGIES TEXTINPUTLAYOUT");
                System.out.println(etAllergies);
                EditText etOtherConditions = (EditText) a.findViewById(R.id.other_conditions_edit_text);
                EditText etMedications = (EditText) a.findViewById(R.id.medication_edit_text);
                EditText etHeight = (EditText) a.findViewById(R.id.height_edit_text);
                EditText etWeight = (EditText) a.findViewById(R.id.weight_edit_text);
                EditText etRegisteredGP = (EditText) a.findViewById(R.id.gp_edit_text);

                //Medical Conditions Checkboxes
                CheckBox chTubercolosis = (CheckBox) a.findViewById(R.id.tubercolosis_checkbox);
                CheckBox chDiabetes = (CheckBox) a.findViewById(R.id.diabetes_checkbox);
                CheckBox chHeartCondition = (CheckBox) a.findViewById(R.id.heart_condition_checkbox);
                CheckBox chGloucoma = (CheckBox) a.findViewById(R.id.gloucoma_checkbox);
                CheckBox chEpilepsy = (CheckBox) a.findViewById(R.id.epilepsy_checkbox);
                CheckBox chDrugAlcoholAbuse = (CheckBox) a.findViewById(R.id.alcohol_drug_checkbox);
                CheckBox chSmoker = (CheckBox) a.findViewById(R.id.smoker_checkbox);
                CheckBox chCancer = (CheckBox) a.findViewById(R.id.cancer_checkbox);

                //Medical Details
                String bloodType = (String) spinner.getSelectedItem().toString();
                String allergies = (String) etAllergies.getText().toString();
                String otherConditions = (String) etOtherConditions.getText().toString();
                String medications = (String) etMedications.getText().toString();

                String height_str = (String) etHeight.getText().toString();
                String weight_str = (String) etWeight.getText().toString();
                if(!height_str.equals("")){
                    height = (double) Double.parseDouble(height_str);
                }
                if(!weight_str.equals("")){
                    weight = (double) Double.parseDouble(weight_str);
                }
                String registeredGP = (String) etRegisteredGP.getText().toString();

                //Medical Conditions Checkboxes
                Boolean tubercolosis = (Boolean) chTubercolosis.isChecked();
                Boolean diabetes = (Boolean) chDiabetes.isChecked();
                Boolean heartCondition = (Boolean) chHeartCondition.isChecked();
                Boolean gloucoma = (Boolean) chGloucoma.isChecked();
                Boolean epilepsy = (Boolean) chEpilepsy.isChecked();
                Boolean drugAlcoholAbuse = (Boolean) chDrugAlcoholAbuse.isChecked();
                Boolean smoker = (Boolean) chSmoker.isChecked();
                Boolean cancer = (Boolean) chCancer.isChecked();

                patientModel.setMedicalDetails(
                        bloodType, allergies, tubercolosis, diabetes, heartCondition,
                        gloucoma, epilepsy, drugAlcoholAbuse, smoker, cancer,
                        otherConditions, medications, height, weight, registeredGP
                );
                */
                String saveMsg = encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext());
                System.out.println(saveMsg);



            }
        });

        return v;

    }

    public void saveData(){
        String json = (String) gson.toJson(patientModel);
        String encryptedString = encryption.encryptSymmetric(json, SYMMETRIC_ALIAS);
        //System.out.println(encryptedString);
        //String decryptedString = encryption.decryptSymmetrically(encryptedString, SYMMETRIC_ALIAS);
        //System.out.println(decryptedString);


        String written = encryption.basicWrite(getContext(), encryptedString, "hyperion.enc");
        System.out.println(written);
        String read = encryption.basicRead(getContext(), "hyperion.enc");
        System.out.println(read);

    }
}
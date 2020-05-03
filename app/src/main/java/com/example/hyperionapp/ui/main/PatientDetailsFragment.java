package com.example.hyperionapp.ui.main;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.hyperionapp.EncryptionService;
import com.example.hyperionapp.PatientDetails;
import com.example.hyperionapp.R;
import com.example.hyperionapp.databinding.FragmentPatientDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PatientDetailsFragment extends Fragment {
    /* Fragment UI logic for adding the patient's medical details */

    // Declare class variables
    private PatientDetails patientModel;
    private FragmentPatientDetailsBinding fragmentPatientDetailsBinding;

    // Declare and instantiate class constants
    final private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final String SYMMETRIC_ALIAS = "hyperion_symmetric_" + user_id;
    final String DATA_FILENAME = user_id + "_hyperion.enc";
    final private EncryptionService encryption = new EncryptionService();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Reference1: https://www.youtube.com/watch?v=pRaFlVCB87k&list=PLJJzW__bab3Q8jYR7dJnNUeoGpHN2Ei1n&index=3
        // Reference2: https://stackoverflow.com/questions/34706399/how-to-use-data-binding-with-fragment
        // Create the data Data Binding model used for capturing the information from the UI
        // without having to write individual onChangeListeners for each field apart from a few exceptions
        fragmentPatientDetailsBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_patient_details, container, false);
        // Instantiate viewModel
        patientModel = ViewModelProviders.of(getActivity()).get(PatientDetails.class);
        // Create view from Binding and set the Binding's data to the patientModel
        View v = fragmentPatientDetailsBinding.getRoot();
        fragmentPatientDetailsBinding.setPatientModel(patientModel);

        //Declare and instantiate the Button and the Spinner
        Button btnSave = v.findViewById(R.id.save_button2);
        Spinner spinner = v.findViewById(R.id.bloodtype_spinner);

        // Add dropdown options from array and render spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.bloodtype_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Reference: https://developer.android.com/guide/topics/ui/controls/spinner#SelectListener
        // Define the functionality that is triggered when an item is selected from
        // the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Reference: https://stackoverflow.com/a/10332288
                // Add the selected option to the viewModel
                patientModel.setBloodType(parent.getItemAtPosition(position).toString());
            }
            // Optional method override if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Save the data from the viewModel in the encrypted local file when the users
        // clicks the button
        btnSave.setOnClickListener((View view) -> encryption.saveData(patientModel, SYMMETRIC_ALIAS, getContext(), DATA_FILENAME));
        // Render view
        return v;

    }
}
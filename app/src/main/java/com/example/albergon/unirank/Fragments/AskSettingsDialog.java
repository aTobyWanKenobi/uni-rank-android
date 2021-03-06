package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This fragment defines a dialog which purpose is to ask to the user to input some meaningful information
 * which will be used when he shares rankings with other users to create useful statistics
 */
public class AskSettingsDialog extends DialogFragment {

    // interaction listeners
    private OnAskSettingsInteractionListener interactionListener = null;
    private OnAskSettingsReturn returnListener = null;

    // UI elements
    private Spinner countrySpinner = null;
    private Spinner genderSpinner = null;
    private NumberPicker yearPicker = null;
    private Spinner typeSpinner = null;
    private Button confirmBtn = null;

    // Entries
    private String currentCountry = null;
    private String currentGender = null;
    private int currentYear = 0;
    private String currentType = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_ask_settings, null);

        // UI setup
        setupUI(view);
        addBehavior();

        // Setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // needed here to dismiss dialog
        confirmBtn.setOnClickListener(v -> {

            // retrieves user inputs
            Enums.TypesOfUsers userSelectedType = null;
            for(Enums.TypesOfUsers type : Enums.TypesOfUsers.values()) {
                if(currentType.equals(type.toString())) {
                    userSelectedType = type;
                }
            }

            currentYear = yearPicker.getValue();
            Enums.GenderEnum gender = currentGender.equals(Enums.GenderEnum.MALE.toString())?
                    Enums.GenderEnum.MALE:
                    Enums.GenderEnum.FEMALE;

            Settings selectedSettings = new Settings(currentCountry, gender, currentYear, userSelectedType);

            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
            databaseHelper.saveSettings(selectedSettings, false);
            interactionListener.goToApp();
            returnListener.setSettingsOk();
            dialog.dismiss();
        });

        return dialog;
    }

    /**
     * Receives callback listener from Activity or Fragment and encapsulate it for when the settings
     * are returned.
     *
     * @param returnListener    callback listener from caller
     */
    public void addSettingsListener(OnAskSettingsReturn returnListener) {
        this.returnListener = returnListener;
    }

    private void setupUI(View view) {

        // Inflate elements
        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
        yearPicker = (NumberPicker) view.findViewById(R.id.year_picker);
        confirmBtn = (Button) view.findViewById(R.id.ask_settings_confirm_btn);

        // Set country adapter
        List<String> countryNames = new ArrayList<>(Countries.countryMap.values());
        Collections.sort(countryNames, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                countryNames);
        countrySpinner.setAdapter(countryAdapter);

        // Set gender adapter
        List<String> genderList = new ArrayList<>();
        for(Enums.GenderEnum gender : Enums.GenderEnum.values()) {
            genderList.add(gender.toString());
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                genderList);
        genderSpinner.setAdapter(genderAdapter);

        // Set type adapter
        List<String> typesList = new ArrayList<>();
        for (Enums.TypesOfUsers type : Enums.TypesOfUsers.values()) {
            typesList.add(type.toString());
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                typesList);
        typeSpinner.setAdapter(typeAdapter);

        // Set year picker values
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setValue(currentYear);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setWrapSelectorWheel(true);
    }

    /**
     * Attach correct listeners to each layout element.
     */
    private void addBehavior() {

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentCountryName = (String) parent.getItemAtPosition(position);
                for(Map.Entry<String, String> entry : Countries.countryMap.entrySet()) {
                    if(entry.getValue().equals(currentCountryName)) {
                        currentCountry = entry.getKey();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGender = (String) parent.getItemAtPosition(position);            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAskSettingsInteractionListener) {
            interactionListener = (OnAskSettingsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAskSettingsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * Activity callback interface.
     */
    public interface OnAskSettingsInteractionListener {

        void goToApp();
    }


}

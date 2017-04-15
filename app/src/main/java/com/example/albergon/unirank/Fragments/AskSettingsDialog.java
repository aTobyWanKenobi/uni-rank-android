package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * This fragment defines a dialog which purpose is to ask to the user to input some meaningful information
 * which will be used when he shares rankings with other users to create useful statistics
 */
public class AskSettingsDialog extends DialogFragment {

    // UI elements
    Spinner countrySpinner = null;
    Spinner genderSpinner = null;
    NumberPicker yearPicker = null;
    Spinner typeSpinner = null;
    Button confirmBtn = null;

    // Entries
    String currentCountry = null;
    String currentGender = null;
    int currentYear = 0;
    Settings.TypesOfUsers currentType = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_ask_settings_dialog, null);

        // UI setup
        setupUI(view);

        // Setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        return dialog;
    }

    public void setupUI(View view) {

        // Inflate elements
        countrySpinner = (Spinner) view.findViewById(R.id.country_spinner);
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
        yearPicker = (NumberPicker) view.findViewById(R.id.year_picker);
        confirmBtn = (Button) view.findViewById(R.id.ask_settings_confirm_btn);

        // Set country adapter
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.simple_dropdown_text_cell,
                Settings.countryCodes);
        countrySpinner.setAdapter(countryAdapter);

        // Set gender adapter
        List<String> genderList = Arrays.asList("Male", "Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.simple_dropdown_text_cell,
                genderList);
        genderSpinner.setAdapter(genderAdapter);

        // Set type adapter
        List<String> typesList = new ArrayList<>();
        for(Settings.TypesOfUsers type : Settings.TypesOfUsers.values()) {
            typesList.add(type.toString());
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.simple_dropdown_text_cell,
                typesList);
        typeSpinner.setAdapter(typeAdapter);

        // Set year picker values
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setValue(currentYear);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setWrapSelectorWheel(true);
    }
}

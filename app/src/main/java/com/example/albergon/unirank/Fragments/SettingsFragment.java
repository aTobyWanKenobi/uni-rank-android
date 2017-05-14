package com.example.albergon.unirank.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

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

public class SettingsFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;
    private Settings settings = null;

    // UI elements
    private Spinner countrySpinner = null;
    private Spinner genderSpinner = null;
    private NumberPicker yearPicker = null;
    private Spinner typeSpinner = null;
    private Button confirmButton = null;

    // current choices
    private String currentCountry = null;
    private String currentGender = null;
    private int currentYear = 0;
    private String currentType = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        databaseHelper = DatabaseHelper.getInstance(getContext());
        settings = databaseHelper.retriveSettings(false);

        // setup UI and set current settings
        setupUI(view);

        // set listeners
        setupListeners();

        return view;
    }

    private void setupUI(View view) {

        countrySpinner = (Spinner) view.findViewById(R.id.change_country_spinner);
        genderSpinner = (Spinner) view.findViewById(R.id.change_gender_spinner);
        yearPicker = (NumberPicker) view.findViewById(R.id.change_year_picker);
        typeSpinner = (Spinner) view.findViewById(R.id.change_type_spinner);
        confirmButton = (Button) view.findViewById(R.id.change_settings_confirm_button);

        // disable button until something is modified
        confirmButton.setEnabled(false);

        // Set country adapter
        List<String> countryNames = new ArrayList<>(Countries.countryMap.values());
        Collections.sort(countryNames, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_layout,
                countryNames);
        countryAdapter.setDropDownViewResource(R.layout.cell_simple_dropdown_text);
        countrySpinner.setAdapter(countryAdapter);

        // Set gender adapter
        List<String> genderList = new ArrayList<>();
        for(Enums.GenderEnum gender : Enums.GenderEnum.values()) {
            genderList.add(gender.toString());
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_layout,
                genderList);
        genderAdapter.setDropDownViewResource(R.layout.cell_simple_dropdown_text);
        genderSpinner.setAdapter(genderAdapter);

        // Set type adapter
        List<String> typesList = new ArrayList<>();
        for (Enums.TypesOfUsers type : Enums.TypesOfUsers.values()) {
            typesList.add(type.toString());
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_layout,
                typesList);
        typeAdapter.setDropDownViewResource(R.layout.cell_simple_dropdown_text);
        typeSpinner.setAdapter(typeAdapter);

        // Set year picker values
        int settingsYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setValue(settingsYear);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(settingsYear);
        yearPicker.setWrapSelectorWheel(true);

        // Set current settings
        currentCountry = settings.getCountryName();
        countrySpinner.setSelection(countryNames.indexOf(currentCountry));

        currentGender = settings.getGender().toString();
        genderSpinner.setSelection(genderList.indexOf(currentGender));

        currentYear = settings.getYearOfBirth();
        yearPicker.setValue(currentYear);

        currentType = settings.getType().toString();
        typeSpinner.setSelection(typesList.indexOf(currentType));
    }

    private void setupListeners() {

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCountry = (String) parent.getItemAtPosition(position);
                confirmButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGender = (String) parent.getItemAtPosition(position);
                confirmButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = (String) parent.getItemAtPosition(position);
                confirmButton.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                confirmButton.setEnabled(true);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Settings newSettings = extractNewSettings();
                databaseHelper.saveSettings(newSettings, false);
                confirmButton.setEnabled(false);
                Toast.makeText(getContext(), "New settings saved", Toast.LENGTH_LONG);
            }
        });
    }

    private Settings extractNewSettings() {

        String country = null;
        for(Map.Entry<String, String> entry : Countries.countryMap.entrySet()) {
            if(entry.getValue().equals(currentCountry)) {
                country = entry.getKey();
            }
        }

        Enums.GenderEnum gender = currentGender.equals(Enums.GenderEnum.MALE.toString())?
                Enums.GenderEnum.MALE:
                Enums.GenderEnum.FEMALE;

        Enums.TypesOfUsers type = null;
        for(Enums.TypesOfUsers t : Enums.TypesOfUsers.values()) {
            if(currentType.equals(t.toString())) {
                type = t;
            }
        }

        int year = yearPicker.getValue();

        return new Settings(country, gender, year, type);
    }
}

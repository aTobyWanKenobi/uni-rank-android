package com.example.albergon.unirank.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.R;

public class SettingsFragment extends Fragment {

    private TextView genderTxt = null;
    private TextView countryTxt = null;
    private TextView yearTxt = null;
    private TextView typeTxt = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
        Settings settings = databaseHelper.retriveSettings(false);

        genderTxt = (TextView) view.findViewById(R.id.test_gender_txt);
        countryTxt = (TextView) view.findViewById(R.id.test_country_txt);
        yearTxt = (TextView) view.findViewById(R.id.test_year_txt);
        typeTxt = (TextView) view.findViewById(R.id.test_type_txt);

        genderTxt.setText(settings.getGender().toString());
        countryTxt.setText(settings.getCountryCode());
        yearTxt.setText(Integer.toString(settings.getYearOfBirth()));
        typeTxt.setText(settings.getType().toString());

        return view;
    }

}

package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.SharedRankFilters.BirthyearFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.CountryFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.GenderFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.TimeframeFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.UserTypeFilter;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This fragment defines a dialog which purpose is to ask to the user to input filter parameters
 * to be used in a query to the remote shared pool.
 */
public class AddFilterDialog extends DialogFragment {

    // interaction listener
    private OnAddFilterReturn returnListener = null;

    // UI elements
    private Spinner categorySpinner = null;
    private Spinner parameterSpinner = null;
    private Button addFilterButton = null;
    private Button cancelFilterAddition = null;

    private String category = "not selected";
    private String parameter = "not selected";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_add_filter, null);

        // UI setup
        setupUI(view);

        // Setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // needed here to dismiss dialog
        addFilterButton.setOnClickListener(v -> {

            returnListener.onFilterReady(createFilterFromCurrent());
            dialog.dismiss();
        });
        addFilterButton.setEnabled(false);

        cancelFilterAddition.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    /**
     * Uses the current dialog parameters to build the desired ShareRankFilter object.
     *
     * @return  an aggregation filter tuned according to user inputs
     */
    private ShareRankFilter createFilterFromCurrent() {

        // retrieve current user inputs
        Enums.PopularIndicatorsCategories currentCategory = findCurrentCategory();
        Object currentParameter = findCurrentParameter();
        ShareRankFilter filter;

        // build corresponding filter
        switch(currentCategory) {

            case GENDER:
                filter = new GenderFilter((Enums.GenderEnum) currentParameter, parameter);
                break;
            case TYPE:
                filter = new UserTypeFilter((Enums.TypesOfUsers) currentParameter);
                break;
            case BIRTHYEAR:
                filter = new BirthyearFilter((Range) currentParameter, parameter);
                break;
            case TIMEFRAME:
                filter = new TimeframeFilter((Enums.TimeFrame) currentParameter, parameter);
                break;
            case COUNTRY:
                filter = new CountryFilter((String) currentParameter);
                break;
            default:
                throw new IllegalStateException("Unknown element in PopularIndicatorsCategories enum");
        }

        return filter;
    }

    /**
     * Receives callback listener from Activity or Fragment and encapsulate it for when the filter
     * will be instantiated.
     *
     * @param returnListener    callback listener from caller
     */
    public void setAddFilterCallback(OnAddFilterReturn returnListener) {
        this.returnListener = returnListener;
    }

    /**
     * Setup dialog layout elements
     *
     * @param view  dialog root View
     */
    private void setupUI(View view) {

        // Inflate elements
        categorySpinner = (Spinner) view.findViewById(R.id.filter_type_spinner);
        parameterSpinner = (Spinner) view.findViewById(R.id.filter_parameter_spinner);
        addFilterButton = (Button) view.findViewById(R.id.add_filter_button_dialog);
        cancelFilterAddition = (Button) view.findViewById(R.id.cancel_filter_addition);

        // set category spinner adapter
        List<String> categoriesList = new ArrayList<>();
        for(Enums.PopularIndicatorsCategories category : Enums.PopularIndicatorsCategories.values()) {
            categoriesList.add(category.toString());
        }

        // add no selection item
        categoriesList.add(0, "not selected");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                categoriesList);
        this.categorySpinner.setAdapter(categoryAdapter);

        // disable parameter spinner until category is chosen and set temporary adapter
        ArrayList<String> parameterList = new ArrayList<>();
        parameterList.add("not selected");
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                parameterList);
        this.parameterSpinner.setAdapter(parameterAdapter);
        parameterSpinner.setEnabled(false);

        // add listeners
        categorySpinner.setOnItemSelectedListener(createCategoryListener());
        parameterSpinner.setOnItemSelectedListener(createParameterListener());
    }

    /**
     * Category spinner listener factory method. Updates category attribute on user selections.
     *
     * @return  a OnItemSelectedListener that will be attached to the category spinner
     */
    private AdapterView.OnItemSelectedListener createCategoryListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // disable button until filter is completely created
                addFilterButton.setEnabled(false);
                category = (String) parent.getItemAtPosition(position);

                // update parameter spinner
                updateCurrentParameterSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    /**
     * Parameter spinner listener factory method. Updates parameter attribute on user selections.
     *
     * @return  a OnItemSelectedListener that will be attached to the parameter spinner
     */
    private AdapterView.OnItemSelectedListener createParameterListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parameter = (String) parent.getItemAtPosition(position);

                // enable add button since filter is complete
                if(!parameter.equals("not selected")) {
                    addFilterButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    /**
     * Updates spinner adapter with a parameter list coherent with the current category.
     */
    private void updateCurrentParameterSpinner() {

        Enums.PopularIndicatorsCategories currCategory = findCurrentCategory();

        if(currCategory == null) {
            return;
        }

        // prepare spinner list by category
        List<String> spinnerList = new ArrayList<>();
        switch(currCategory) {
            case GENDER:
                for(Enums.GenderEnum gender : Enums.GenderEnum.values()) {
                    spinnerList.add(gender.toString());
                }
                break;
            case TYPE:
                for(Enums.TypesOfUsers type : Enums.TypesOfUsers.values()) {
                    spinnerList.add(type.toString());
                }
                break;
            case BIRTHYEAR:
                for(Enums.UserAgeCategories age : Enums.UserAgeCategories.values()) {
                    spinnerList.add(age.toString());
                }
                break;
            case TIMEFRAME:
                for(Enums.TimeFrame timeFrame : Enums.TimeFrame.values()) {
                    spinnerList.add(timeFrame.toString());
                }
                break;
            case COUNTRY:
                List<String> countryNames = new ArrayList<>(Countries.countryMap.values());
                Collections.sort(countryNames, String.CASE_INSENSITIVE_ORDER);
                spinnerList = countryNames;
                break;
            default:
                throw new IllegalStateException("Unknown element in categories enumeration");
        }

        // add initial "no selection" entry
        spinnerList.add(0, "not selected");

        // set category spinner adapter and enable it
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                spinnerList);
        this.parameterSpinner.setAdapter(parameterAdapter);
        this.parameterSpinner.setEnabled(true);
    }

    /**
     * Finds category enum representation from string representation.
     *
     * @return  a category enum element
     */
    private Enums.PopularIndicatorsCategories findCurrentCategory() {

        // find current category
        Enums.PopularIndicatorsCategories currCategory = null;
        for(Enums.PopularIndicatorsCategories cat : Enums.PopularIndicatorsCategories.values()) {
            if(cat.toString().equals(category)) {
                currCategory = cat;
            }
        }

        return currCategory;
    }

    /**
     * Creates correct parameter object depending from selected category and parameter string representation.
     *
     * @return a generic parameter Object, since each filter has a different parameter type
     */
    private Object findCurrentParameter() {

        // handle string parameter differently depending on category
        switch(findCurrentCategory()) {

            // parameter is enum GenderEnum
            case GENDER:

                // retrieve gender parameter
                Enums.GenderEnum gender = null;
                for(Enums.GenderEnum g : Enums.GenderEnum.values()) {
                    if(g.toString().equals(parameter)) {
                        gender = g;
                    }
                }

                return gender;

            // parameter is enum TypeOfUsers
            case TYPE:

                // retrieve type parameter
                Enums.TypesOfUsers type = null;
                for(Enums.TypesOfUsers t : Enums.TypesOfUsers.values()) {
                    if(t.toString().equals(parameter)) {
                        type = t;
                    }
                }

                return type;

            // parameter is a range of years
            case BIRTHYEAR:

                // retrieve age parameter
                Enums.UserAgeCategories age = null;
                for(Enums.UserAgeCategories a : Enums.UserAgeCategories.values()) {
                    if(a.toString().equals(parameter)) {
                        age = a;
                    }
                }

                // build correct range depending on current year
                Range yearRange;
                String currentDate = FirebaseHelper.generateDate();
                int currentYear = Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(' ') + 1));
                //noinspection ConstantConditions
                switch(age) {
                    case KIDS:
                        yearRange = new Range(currentYear - 15, currentYear);
                        break;
                    case TEENS:
                        yearRange = new Range(currentYear - 20, currentYear - 15);
                        break;
                    case YOUNGS:
                        yearRange = new Range(currentYear - 27, currentYear - 20);
                        break;
                    case ADULTS:
                        yearRange = new Range(currentYear - 50, currentYear - 27);
                        break;
                    case OLD:
                        yearRange = new Range(1900, currentYear - 50);
                        break;
                    default:
                        throw new IllegalStateException("Unknown element in age enumeration");
                }

                return yearRange;

            // parameter is enum TimeFrame
            case TIMEFRAME:

                // retrieve time frame parameter
                Enums.TimeFrame timeFrame = null;
                for(Enums.TimeFrame t : Enums.TimeFrame.values()) {
                    if(t.toString().equals(parameter)) {
                        timeFrame = t;
                    }
                }

                return timeFrame;

            // parameter is country code string
            case COUNTRY:

                // retrieve country parameter
                String countryCode = null;
                for(Map.Entry<String, String> entry : Countries.countryMap.entrySet()) {
                    if(entry.getValue().equals(parameter)) {
                        countryCode = entry.getKey();
                    }
                }

                return countryCode;

            default:
                throw new IllegalStateException("Unknown element in categories enumeration");
        }
    }

}

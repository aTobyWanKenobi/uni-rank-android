package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class serves as a container for the tuple composed by a filter category and the
 * associated parameters, along with listeners.
 */
public class FilterListCellContent {

    private Context context = null;

    private String category = null;
    private String parameter = null;

    private AdapterView.OnItemSelectedListener categoryListener = null;
    private AdapterView.OnItemSelectedListener parameterListener = null;

    private Spinner categorySpinner = null;
    private Spinner parameterSpinner = null;

    private boolean first = true;

    public FilterListCellContent(Context context) {

        this.context = context;

        categoryListener =new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!first) {
                    // retrieve selected category
                    category = (String) parent.getItemAtPosition(position);

                    ArrayList<String> parameterList = new ArrayList<>();
                    parameterList.add("cambia?");
                    ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                            context,
                            R.layout.cell_simple_dropdown_text,
                            parameterList);
                    parameterSpinner.setAdapter(parameterAdapter);
                    //updateCurrentParameterSpinner();
                }

                first = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };

        parameterListener =new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // retrieve selected parameter
                parameter = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    public void setCategorySpinner(Spinner categorySpinner) {
        this.categorySpinner = categorySpinner;

        // set category spinner adapter
        List<String> categoriesList = new ArrayList<>();
        for(Enums.PopularIndicatorsCategories category : Enums.PopularIndicatorsCategories.values()) {
            categoriesList.add(category.toString());
        }

        // add no selection item
        categoriesList.add(0, "not selected");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                context,
                R.layout.cell_simple_dropdown_text,
                categoriesList);
        this.categorySpinner.setAdapter(categoryAdapter);

        this.categorySpinner.setOnItemSelectedListener(categoryListener);
    }

    public void setParameterSpinner(Spinner parameterSpinner) {
        this.parameterSpinner = parameterSpinner;
        //this.parameterSpinner.setEnabled(false);

        // disable parameter spinner until category is chosen and set temporary adapter
        ArrayList<String> parameterList = new ArrayList<>();
        parameterList.add("not selected");
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                context,
                R.layout.cell_simple_dropdown_text,
                parameterList);
        this.parameterSpinner.setAdapter(parameterAdapter);

        this.parameterSpinner.setOnItemSelectedListener(parameterListener);
    }

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
                context,
                R.layout.cell_simple_dropdown_text,
                spinnerList);
        this.parameterSpinner.setAdapter(parameterAdapter);
        //this.parameterSpinner.setEnabled(true);

    }

    public Enums.PopularIndicatorsCategories findCurrentCategory() {

        // find current category
        Enums.PopularIndicatorsCategories currCategory = null;
        for(Enums.PopularIndicatorsCategories cat : Enums.PopularIndicatorsCategories.values()) {
            if(cat.toString().equals(category)) {
                currCategory = cat;
            }
        }

        return currCategory;
    }

    public Object findCurrentParameter() {

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
                Range yearRange = null;
                String currentDate = FirebaseHelper.generateDate();
                int currentYear = Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(' ') + 1));
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
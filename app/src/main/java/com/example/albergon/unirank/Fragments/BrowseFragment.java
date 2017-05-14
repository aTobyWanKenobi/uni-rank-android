package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnGeneralStatisticsRetrievalListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.LayoutAdapters.PopularIndicatorListAdapter;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.Model.SharedPoolFilter;
import com.example.albergon.unirank.R;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseFragment extends Fragment {

    private OnBrowseFragmentInteractionListener interactionListener;

    private FirebaseHelper firebaseHelper = null;
    private DatabaseHelper databaseHelper = null;

    private Enums.PopularIndicatorsCategories currentCategory = null;

    // General layout
    private RadioButton popularIndicatorRadio = null;
    private RadioButton generalStatisticsRadio = null;
    private FrameLayout queryContainer = null;

    // Popular indicators layout
    private Spinner categorySpinner = null;
    private Spinner parameterSpinner = null;
    private ListView queryResult = null;

    // General statistics layout
    private TextView totalCount = null;
    private TextView monthCount = null;
    private TextView yearCount = null;
    private TextView malePercentage = null;
    private TextView femalePercentage = null;
    private TextView highschoolPercentage = null;
    private TextView universityPercentage = null;
    private TextView parentsPercentage = null;
    private TextView othersPercentage = null;
    private TextView kidsPercentage = null;
    private TextView teensPercentage = null;
    private TextView youngsPercentage = null;
    private TextView adultsPercentage = null;
    private TextView oldPercentage = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        databaseHelper = DatabaseHelper.getInstance(getContext());
        firebaseHelper = new FirebaseHelper(getContext());

        // setup common elements
        setupUI(view);

        // popular indicators selected by default for now
        initializePopularIndicators(view);
        //initializeGeneralStatistics(view);

        //demo(view);

        return view;
    }

    private void setupUI(View view) {

        popularIndicatorRadio = (RadioButton) view.findViewById(R.id.popular_indicators_radio);
        generalStatisticsRadio = (RadioButton) view.findViewById(R.id.general_queries_radio);
        queryContainer = (FrameLayout) view.findViewById(R.id.query_layout_container);

        popularIndicatorRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializePopularIndicators(v);
            }
        });

        generalStatisticsRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeGeneralStatistics(v);
            }
        });
    }

    private void initializeGeneralStatistics(View view) {

        generalStatisticsRadio.setChecked(true);

        // inflate correct layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View generalStatsLayout = inflater.inflate(R.layout.general_statistics_layout, null, false);
        queryContainer.removeAllViews();
        queryContainer.addView(generalStatsLayout);

        //TODO: move above?
        //TODO: use piecharts
        // initialize layout elements
        totalCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_total_count_number);
        monthCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_month_number);
        yearCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_year_number);
        malePercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_male_percentage);
        femalePercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_female_percentage);
        highschoolPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_highschool_percentage);
        universityPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_university_percentage);
        parentsPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_parents_percentage);
        othersPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_others_percentage);
        kidsPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_kids_percentage);
        teensPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_teens_percentage);
        youngsPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_youngs_percentage);
        adultsPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_adults_percentage);
        oldPercentage = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_old_percentage);

        // create error listener
        OnFirebaseErrorListener errorListener = new OnFirebaseErrorListener() {
            @Override
            public void onError(String message) {
                // TODO: evaluate recovery in app? just an error toast?
                throw new DatabaseException(message);
            }
        };

        // create success listener
        OnGeneralStatisticsRetrievalListener successListener = createOnStatsSuccessListener();

        // call firebase retrieval
        firebaseHelper.retrieveGeneralStats(successListener, errorListener);
    }

    private void initializePopularIndicators(View view) {

        popularIndicatorRadio.setChecked(true);

        // inflate correct layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popularIndicatorsLayout = inflater.inflate(R.layout.popular_indicators_query, null, false);
        queryContainer.removeAllViews();
        queryContainer.addView(popularIndicatorsLayout);

        // initialize layout elements
        categorySpinner = (Spinner) popularIndicatorsLayout.findViewById(R.id.query_type_spinner);
        parameterSpinner = (Spinner) popularIndicatorsLayout.findViewById(R.id.query_parameter_spinner);
        queryResult = (ListView) popularIndicatorsLayout.findViewById(R.id.indicators_result_list);

        // disable parameter spinner until category is chosen and set temporary adapter
        parameterSpinner.setEnabled(false);
        List<String> noParametersList = new ArrayList<>();
        noParametersList.add("not selected");
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                noParametersList);
        parameterSpinner.setAdapter(parameterAdapter);

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
        categorySpinner.setAdapter(categoryAdapter);

        // set category spinner listener
        categorySpinner.setOnItemSelectedListener(createCategorySpinnerListener());

    }

    private OnGeneralStatisticsRetrievalListener createOnStatsSuccessListener() {
        return new OnGeneralStatisticsRetrievalListener() {
            @Override
            public void onGeneralStatisticsRetrieved(ShareGeneralStats stats) {
                // call UI update
                setGeneralStatsValues(stats);
            }
        };
    }

    private void setGeneralStatsValues(ShareGeneralStats stats) {
        // upload counts
        totalCount.setText(String.valueOf(stats.totalCount));

        String currentDate = FirebaseHelper.generateDate();
        String currentMonth = currentDate.split(" ", 2)[1];
        String currentYear = currentDate.substring(currentDate.lastIndexOf(' ') + 1);

        monthCount.setText(String.valueOf(stats.byMonth.getOrDefault(currentMonth, 0)));

        int yearSum = 0;
        for(int i = 0; i < 12; i++) {
            yearSum += stats.byMonth.getOrDefault(Enums.MonthsAbbreviations.values()[i].toString() + " " + currentYear, 0);
        }
        yearCount.setText(String.valueOf(yearSum));

        // gender percentages
        malePercentage.setText(String.valueOf((int)(100*stats.byMale/(double) stats.totalCount)) + " %");
        femalePercentage.setText(String.valueOf((int)(100*stats.byFemale/(double) stats.totalCount)) + " %");

        // type percentages
        highschoolPercentage.setText(String.valueOf((int)(100*stats.byHighSchoolStudents/(double) stats.totalCount)) + " %");
        universityPercentage.setText(String.valueOf((int)(100*stats.byUniversityStudents/(double) stats.totalCount)) + " %");
        parentsPercentage.setText(String.valueOf((int)(100*stats.byParents/(double) stats.totalCount)) + " %");
        othersPercentage.setText(String.valueOf((int)(100*stats.byOtherType/(double) stats.totalCount)) + " %");

        // age percentages (delegated to method because it was long)
        setAgePercentages(currentYear, stats);
    }

    private void setAgePercentages(String currentYear, ShareGeneralStats stats) {

        int currIntYear = Integer.valueOf(currentYear);
        Range kidsRange = new Range(currIntYear - 15, currIntYear);
        int kidsCount = 0;
        Range teensRange = new Range(currIntYear - 20, currIntYear - 15);
        int teensCount = 0;
        Range youngsRange = new Range(currIntYear - 27, currIntYear - 20);
        int youngsCount = 0;
        Range adultsRange = new Range(currIntYear - 50, currIntYear - 27);
        int adultsCount = 0;
        Range oldRange = new Range(1900, currIntYear - 50);
        int oldCount = 0;

        for(Map.Entry<String, Integer> entry : stats.byBirthYear.entrySet()) {
            int entryYear = Integer.valueOf(entry.getKey().substring(4));

            if(kidsRange.within(entryYear)) {
                kidsCount += entry.getValue();
            } else if(teensRange.within(entryYear)) {
                teensCount += entry.getValue();
            } else if(youngsRange.within(entryYear)) {
                youngsCount += entry.getValue();
            }else if(adultsRange.within(entryYear)) {
                adultsCount += entry.getValue();
            }else if(oldRange.within(entryYear)) {
                oldCount += entry.getValue();
            } else {
                throw new IllegalStateException("Error in Range creation or in database data");
            }
        }

        kidsPercentage.setText(String.valueOf((int)(100*kidsCount/(double) stats.totalCount)) + " %");
        teensPercentage.setText(String.valueOf((int)(100*teensCount/(double) stats.totalCount)) + " %");
        youngsPercentage.setText(String.valueOf((int)(100*youngsCount/(double) stats.totalCount)) + " %");
        adultsPercentage.setText(String.valueOf((int)(100*adultsCount/(double) stats.totalCount)) + " %");
        oldPercentage.setText(String.valueOf((int)(100*oldCount/(double) stats.totalCount)) + " %");
    }

    private AdapterView.OnItemSelectedListener createCategorySpinnerListener() {

        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // retrieve selected category and call UI updates
                String chosenCategory = (String) parent.getItemAtPosition(position);
                for(Enums.PopularIndicatorsCategories c : Enums.PopularIndicatorsCategories.values()) {
                    // avoids updating category on nothing selected
                    if(c.toString().equals(chosenCategory)) {
                        updateCurrentCategory(c);
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    private AdapterView.OnItemSelectedListener createParameterSpinnerListener() {

        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // retrieve selected parameter and call UI updates
                String chosenParameter = (String) parent.getItemAtPosition(position);

                // safety check, should never happen
                if(currentCategory != null && !chosenParameter.equals("not selected")) {
                    launchQuery(chosenParameter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    private void updateCurrentCategory(Enums.PopularIndicatorsCategories category) {

        // update current category
        currentCategory = category;

        // prepare spinner list by category
        List<String> spinnerList = new ArrayList<>();
        switch(category) {
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
        parameterSpinner.setAdapter(parameterAdapter);
        parameterSpinner.setEnabled(true);

        // set category parameter listener
        parameterSpinner.setOnItemSelectedListener(createParameterSpinnerListener());

    }

    private void launchQuery(String parameter) {

        // Object parameter depending on the category
        final Object param = SharedPoolFilter.parameterDependingOnCategory(currentCategory, parameter);

        // Uniform error callback listener that simply throws an exception
        final OnFirebaseErrorListener errorListener = new OnFirebaseErrorListener() {

            @Override
            public void onError(String message) {
                // TODO: evaluate recovery in app? just an error toast?
                throw new DatabaseException(message);
            }
        };

        // On retrieval of shared pool, process query with apposite method and forward result to display method
        OnSharedPoolRetrievalListener retrievalListener = new OnSharedPoolRetrievalListener() {
            @Override
            public void onSharedPoolRetrieved(List<ShareRank> sharedPool) {
                int[] scores = SharedPoolFilter.popularIndicatorsByCategory(sharedPool,
                        currentCategory,
                        param);
                displayListResult(scores);
            }
        };

        // Launch shared pool retrieval with listeners
        firebaseHelper.retrieveSharedPool(retrievalListener, errorListener);
    }

    private void displayListResult(int[] scores) {

        Map<Integer, Integer> indicatorsWeights = new HashMap<>();
        for(int i = 0; i < scores.length; i++) {
            indicatorsWeights.put(i, scores[i]);
        }

        // Setup ListView adapter
        PopularIndicatorListAdapter adapter = new PopularIndicatorListAdapter(
                getContext(),
                R.layout.cell_popular_indicator,
                indicatorsWeights);
        queryResult.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBrowseFragmentInteractionListener) {
            interactionListener = (OnBrowseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBrowseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * Interaction listener for implementing activity
     */
    public interface OnBrowseFragmentInteractionListener {

    }

}

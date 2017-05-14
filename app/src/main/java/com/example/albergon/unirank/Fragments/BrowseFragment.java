package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
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
import com.example.albergon.unirank.Model.ChartColors;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.Model.SharedPoolFilter;
import com.example.albergon.unirank.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ColorFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    private PieChart genderChart = null;
    private PieChart userTypeChart = null;
    private PieChart userAgeChart = null;

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
        //initializePopularIndicators(view);
        initializeGeneralStatistics(view);

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
        View generalStatsLayout = inflater.inflate(R.layout.other_remote_stats, null, false);
        queryContainer.removeAllViews();
        queryContainer.addView(generalStatsLayout);

        // initialize pie charts
        genderChart = (PieChart) view.findViewById(R.id.gender_pie_chart);
        userTypeChart = (PieChart) view.findViewById(R.id.user_type_pie_chart);
        userAgeChart = (PieChart) view.findViewById(R.id.user_age_pie_chart);

        /*
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
        */

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
        View popularIndicatorsLayout = inflater.inflate(R.layout.other_popular_indicators, null, false);
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
        //totalCount.setText(String.valueOf(stats.totalCount));

        String currentDate = FirebaseHelper.generateDate();
        String currentMonth = currentDate.split(" ", 2)[1];
        String currentYear = currentDate.substring(currentDate.lastIndexOf(' ') + 1);

        /*
        monthCount.setText(String.valueOf(stats.byMonth.getOrDefault(currentMonth, 0)));

        int yearSum = 0;
        for(int i = 0; i < 12; i++) {
            yearSum += stats.byMonth.getOrDefault(Enums.MonthsAbbreviations.values()[i].toString() + " " + currentYear, 0);
        }
        yearCount.setText(String.valueOf(yearSum));
        */

        setAndStyleGender(stats);
        setAndStyleType(stats);
        setAndStyleAge(stats, currentYear);
    }

    private void setAndStyleGender(ShareGeneralStats stats) {

        // gender entries
        PieEntry malePer = new PieEntry((int)(100*stats.byMale/(double) stats.totalCount), "Male");
        PieEntry femalePer = new PieEntry((int)(100*stats.byFemale/(double) stats.totalCount), "Female");
        List<PieEntry> genderEntries = new ArrayList<>();
        genderEntries.add(malePer);
        genderEntries.add(femalePer);

        // gender dataset
        PieDataSet genderDataSet = new PieDataSet(genderEntries, "");
        genderDataSet.setSliceSpace(3);
        genderDataSet.setValueTextColor(ChartColors.BLACK);
        genderDataSet.setColors(ChartColors.MALE_BLUE, ChartColors.FEMALE_PINK);

        // gender data
        PieData genderData = new PieData(genderDataSet);
        stylePieData(genderData);
        genderChart.setData(genderData);

        // gender styling
        stylePieChart(genderChart, "UPLOADS BY GENDER");
        styleLegend(genderChart.getLegend(), true);

        // refresh
        genderChart.invalidate();
    }

    private void setAndStyleType(ShareGeneralStats stats) {

        // type entries
        PieEntry highschoolPer = new PieEntry((int)(100*stats.byHighSchoolStudents/(double) stats.totalCount), "Highschool");
        PieEntry universityPer = new PieEntry((int)(100*stats.byUniversityStudents/(double) stats.totalCount), "University");
        PieEntry parentsPer = new PieEntry((int)(100*stats.byParents/(double) stats.totalCount), "Parents");
        PieEntry othersPer = new PieEntry((int)(100*stats.byOtherType/(double) stats.totalCount), "Other");
        List<PieEntry> typeEntries = new ArrayList<>();
        typeEntries.add(highschoolPer);
        typeEntries.add(universityPer);
        typeEntries.add(parentsPer);
        typeEntries.add(othersPer);

        // type dataset
        PieDataSet typeDataSet = new PieDataSet(typeEntries, "");
        typeDataSet.setSliceSpace(3);
        typeDataSet.setValueTextColor(ChartColors.BLACK);
        typeDataSet.setValueFormatter(new PercentFormatter());
        typeDataSet.setColors(
                ChartColors.HIGHSCHOOL_GREEN,
                ChartColors.UNIVERSITY_BLUE,
                ChartColors.PARENTS_YELLOW,
                ChartColors.OTHERS_VIOLET);

        // type data
        PieData typeData = new PieData(typeDataSet);
        stylePieData(typeData);
        userTypeChart.setData(typeData);

        // type styling
        stylePieChart(userTypeChart, "UPLOADS BY USER TYPE");
        styleLegend(userTypeChart.getLegend(), true);

        // refresh
        userTypeChart.invalidate();
    }

    private void setAndStyleAge(ShareGeneralStats stats, String currentYear) {

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

        // type entries
        PieEntry kidsPer = new PieEntry((int)(100*kidsCount/(double) stats.totalCount), "0-15");
        PieEntry teensPer = new PieEntry((int)(100*teensCount/(double) stats.totalCount), "15-20");
        PieEntry youngsPer = new PieEntry((int)(100*youngsCount/(double) stats.totalCount), "20-27");
        PieEntry adultsPer = new PieEntry((int)(100*adultsCount/(double) stats.totalCount), "27-50");
        PieEntry oldPer = new PieEntry((int)(100*oldCount/(double) stats.totalCount), "50+");
        List<PieEntry> ageEntries = new ArrayList<>();
        ageEntries.add(kidsPer);
        ageEntries.add(teensPer);
        ageEntries.add(youngsPer);
        ageEntries.add(adultsPer);
        ageEntries.add(oldPer);

        // type dataset
        PieDataSet ageDataSet = new PieDataSet(ageEntries, "");
        ageDataSet.setSliceSpace(3);
        ageDataSet.setValueTextColor(ChartColors.BLACK);
        ageDataSet.setValueFormatter(new PercentFormatter());
        ageDataSet.setColors(
                ChartColors.KIDS_ORANGE,
                ChartColors.TEENS_MINT,
                ChartColors.YOUNGS_RED,
                ChartColors.ADULTS_PURPLE,
                ChartColors.OLD_GREY);

        // type data
        PieData ageData = new PieData(ageDataSet);
        stylePieData(ageData);
        userAgeChart.setData(ageData);

        // type styling
        stylePieChart(userAgeChart, "UPLOADS BY USER AGE");
        styleLegend(userAgeChart.getLegend(), true);

        // refresh
        userAgeChart.invalidate();
    }


    private void stylePieChart(PieChart chart, String centerText) {

        // style texts
        chart.setCenterTextColor(ChartColors.PALETTE3);
        chart.setCenterTextSize(10f);
        chart.setCenterText(centerText);
        chart.setUsePercentValues(true);
        chart.setEntryLabelColor(ChartColors.BLACK);
        chart.setDrawEntryLabels(false);

        // set hole and display
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(ChartColors.BLACK);
        chart.setTransparentCircleColor(ChartColors.BLACK);
        chart.setTransparentCircleAlpha(110);
        chart.getDescription().setEnabled(false);
    }

    private void stylePieData(PieData data) {

        data.setValueTextSize(15f);
        data.setValueTextColor(ChartColors.BLACK);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueFormatter(new PercentFormatter());
    }

    private void styleLegend(Legend legend, boolean left) {

        // enable legend
        legend.setEnabled(true);

        // set position
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        if(left) {
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        } else {
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        }


        // set attributes
        legend.setDrawInside(true);
        legend.setTextColor(ChartColors.PALETTE3);
        legend.setTextSize(10f);
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

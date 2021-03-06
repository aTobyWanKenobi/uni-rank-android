package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnGeneralStatisticsRetrievalListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.LayoutAdapters.AsyncFilterListAdd;
import com.example.albergon.unirank.LayoutAdapters.FilterListAdapter;
import com.example.albergon.unirank.LayoutAdapters.PopularIndicatorListAdapter;
import com.example.albergon.unirank.Model.ChartColors;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareGeneralStats;
import com.example.albergon.unirank.Model.SharedRankFilters.FilterManager;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Fragment implements the possible interactions with the remote shared pool of aggregations. It is
 * divided in two distinct parts, that the user can select through a smaller tab selector: General
 * Statistics and Popular Indicators. When the users switches between those two functionality, the
 * corresponding layout is inflated.
 */
@SuppressWarnings("DanglingJavadoc")
public class BrowseFragment extends Fragment {

    // interaction listener
    private OnBrowseFragmentInteractionListener interactionListener;

    // database instances
    private FirebaseHelper firebaseHelper = null;

    // General layout
    private View rootView = null;
    private RadioButton popularIndicatorRadio = null;
    private RadioButton generalStatisticsRadio = null;
    private FrameLayout queryContainer = null;

    // Popular indicators layout
    private ListView queryResult = null;
    private ListView filterList = null;
    private Button addFilter = null;
    private FilterListAdapter filterListAdapter = null;

    // General statistics layout
    private PieChart genderChart = null;
    private PieChart userTypeChart = null;
    private PieChart userAgeChart = null;
    private TextView totalCount = null;
    private TextView monthCount = null;
    private TextView yearCount = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_browse, container, false);

        firebaseHelper = new FirebaseHelper(getContext());

        // setup common elements
        setupUI();

        // general statistics selected by default for now
        initializeGeneralStatistics();

        return rootView;
    }

    /**
     * Sets up common UI for the fragment, independent of the choice in the radio group.
     */
    private void setupUI() {

        // initialize layout
        popularIndicatorRadio = (RadioButton) rootView.findViewById(R.id.popular_indicators_radio);
        generalStatisticsRadio = (RadioButton) rootView.findViewById(R.id.general_queries_radio);
        queryContainer = (FrameLayout) rootView.findViewById(R.id.query_layout_container);

        // add functionality switch listeners
        popularIndicatorRadio.setOnClickListener(v -> initializePopularIndicators());

        generalStatisticsRadio.setOnClickListener(v -> initializeGeneralStatistics());
    }

    /**
     * Sets up the fragment UI when the user chooses to display the "general statistics" part, inflating
     * the correct layout.
     */
    private void initializeGeneralStatistics() {

        generalStatisticsRadio.setChecked(true);

        // inflate correct layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View generalStatsLayout = inflater.inflate(R.layout.other_remote_stats, null, false);
        queryContainer.removeAllViews();
        queryContainer.addView(generalStatsLayout);

        // initialize pie charts
        genderChart = (PieChart) generalStatsLayout.findViewById(R.id.gender_pie_chart);
        userTypeChart = (PieChart) generalStatsLayout.findViewById(R.id.user_type_pie_chart);
        userAgeChart = (PieChart) generalStatsLayout.findViewById(R.id.user_age_pie_chart);

        // initialize layout elements
        totalCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_total_count_number);
        monthCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_last_month_number);
        yearCount = (TextView) generalStatsLayout.findViewById(R.id.gen_stat_last_year_number);

        // create error listener
        OnFirebaseErrorListener errorListener = message -> {
            throw new DatabaseException(message);
        };

        // create success listener
        OnGeneralStatisticsRetrievalListener successListener = createOnStatsSuccessListener();

        // call firebase retrieval
        firebaseHelper.retrieveGeneralStats(successListener, errorListener);
    }

    /**
     * Sets up the fragment UI when the user chooses to display the "popular indicators" part, inflating
     * the correct layout.
     */
    private void initializePopularIndicators() {

        popularIndicatorRadio.setChecked(true);

        // inflate correct layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams") View popularIndicatorsLayout = inflater.inflate(R.layout.other_popular_indicators, null, false);
        queryContainer.removeAllViews();
        queryContainer.addView(popularIndicatorsLayout);

        // initialize layout elements
        queryResult = (ListView) popularIndicatorsLayout.findViewById(R.id.indicators_result_list);
        filterList = (ListView) popularIndicatorsLayout.findViewById(R.id.filters_list);
        addFilter = (Button) popularIndicatorsLayout.findViewById(R.id.add_filter_button);

        // create callback handler
        OnAddFilterReturn filterListener = new OnAddFilterReturn() {
            @Override
            public void onFilterReady(ShareRankFilter filter) {
                // add indicator to the list
                AsyncFilterListAdd task = new AsyncFilterListAdd(filterListAdapter, getContext());
                task.execute(filter);
            }

            @Override
            public void updateQuery() {
                launchQuery();
            }
        };

        // add behavior to filter building mechanism
        filterListAdapter = new FilterListAdapter(getContext(), R.layout.cell_pop_indicator_filter, filterListener);
        filterList.setAdapter(filterListAdapter);
        addFilter.setOnClickListener(v -> {

            AddFilterDialog dialog = new AddFilterDialog();
            dialog.setAddFilterCallback(filterListener);

            // show filter building dialog
            interactionListener.showFilterDialog(dialog);
        });

        launchQuery();
    }

    /***********************************************************************************************
     *  GENERAL STATISTICS
     **********************************************************************************************/

    /**
     * Creates the callback listener responsible for triggering UI updates when the general statistics
     * are retrieved from the remote database
     *
     * @return  a retrieval listener for the general statistics node
     */
    private OnGeneralStatisticsRetrievalListener createOnStatsSuccessListener() {
        return stats -> {
            // call UI update
            setGeneralStatsValues(stats);
        };
    }

    /**
     * Interface update method, called when new general statistics are retrieved from the database.
     * It is responsible for calling chart styling methods and updating the views with data.
     *
     * @param stats the container objects for the stats to display
     */
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

        // style charts
        setAndStyleGender(stats);
        setAndStyleType(stats);
        setAndStyleAge(stats, currentYear);
    }

    /**
     * Private method responsible for filling and styling the gender distribution pie chart.
     */
    private void setAndStyleGender(ShareGeneralStats stats) {

        // gender entries
        PieEntry malePer = new PieEntry((int)(100*stats.byMale/(double) stats.totalCount), "Male");
        PieEntry femalePer = new PieEntry((int)(100*stats.byFemale/(double) stats.totalCount), "Female");
        List<PieEntry> genderEntries = new ArrayList<>();
        genderEntries.add(malePer);
        genderEntries.add(femalePer);

        // gender data set
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

    /**
     * Private method responsible for filling and styling the user type distribution pie chart.
     */
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

        // type data set
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

    /**
     * Private method responsible for filling and styling the user age distribution pie chart.
     */
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

        // type data set
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

    /**
     * Private method that styles a pie chart uniformly for the whole fragment, called for each pie
     * chart. It is responsible for center text, hole size and color and other parameters.
     *
     * @param chart         pie chart to style
     * @param centerText    text to display in the center
     */
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

    /**
     * Private method responsible for styling the value data displayed on a pie chart.
     *
     * @param data  data to format
     */
    private void stylePieData(PieData data) {

        data.setValueTextSize(15f);
        data.setValueTextColor(ChartColors.BLACK);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueFormatter(new PercentFormatter());
    }

    /**
     * Private method responsible for styling a pie chart legend, principally for what concerns its
     * position relative to the chart and the looks of its text.
     *
     * @param legend    legend to style
     * @param left      specifies whether we desire left or right alignment
     */
    private void styleLegend(Legend legend, boolean left) {

        // enable legend
        legend.setEnabled(true);

        // set position
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        if(left) {
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        } else {
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        }


        // set attributes
        legend.setDrawInside(false);
        legend.setTextColor(ChartColors.PALETTE3);
        legend.setTextSize(10f);
    }

    /***********************************************************************************************
     *  POPULAR INDICATORS
     **********************************************************************************************/

    /**
     * Launches a firebase query using the current filter set.
     */
    private void launchQuery() {

        // Uniform error callback listener that simply throws an exception
        final OnFirebaseErrorListener errorListener = message -> {
            throw new DatabaseException(message);
        };

        // On retrieval of shared pool, process query with apposite method and forward result to display method
        OnSharedPoolRetrievalListener retrievalListener = sharedPool -> {
            // create filter manager with current filter set
            FilterManager filterManager = new FilterManager(filterListAdapter.getCurrentFilters());
            int[] scores = filterManager.filter(sharedPool);
            displayListResult(scores);
        };

        // Launch shared pool retrieval with listeners
        firebaseHelper.retrieveSharedPool(retrievalListener, errorListener);
    }

    /**
     * Display indicator set with associated popularity scores.
     *
     * @param scores    popularity scores just computed from shared pool
     */
    private void displayListResult(int[] scores) {

        // build indicator "settings"
        @SuppressLint("UseSparseArrays") Map<Integer, Integer> indicatorsWeights = new HashMap<>();
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

    /***********************************************************************************************
     *  BOILERPLATE
     **********************************************************************************************/

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

        void showFilterDialog(AddFilterDialog dialog);
    }

}

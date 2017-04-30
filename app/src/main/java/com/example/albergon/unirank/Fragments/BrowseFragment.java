package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.ShareRankFilter;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
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

    // demo
    private  TextView i1 = null;
    private  TextView i2 = null;
    private  TextView i3 = null;
    private  TextView i4 = null;
    private  TextView i5 = null;


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

        //demo(view);

        return view;
    }

    private void setupUI(View view) {

        popularIndicatorRadio = (RadioButton) view.findViewById(R.id.popular_indicators_radio);
        generalStatisticsRadio = (RadioButton) view.findViewById(R.id.general_queries_radio);
        queryResult = (ListView) view.findViewById(R.id.indicators_result_list);
        queryContainer = (FrameLayout) view.findViewById(R.id.query_layout_container);
    }

    private void initializePopularIndicators(View view) {

        popularIndicatorRadio.setChecked(true);

        // inflate correct layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popularIndicatorsLayout = inflater.inflate(R.layout.popular_indicators_query, null, false);
        queryContainer.addView(popularIndicatorsLayout);

        // disable parameter spinner until category is chosen
        parameterSpinner.setEnabled(false);

        // set category spinner adapter
        List<String> categoriesList = new ArrayList<>();
        for(Enums.PopularIndicatorsCategories category : Enums.PopularIndicatorsCategories.values()) {
            categoriesList.add(category.toString());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.simple_dropdown_text_cell,
                categoriesList);
        categorySpinner.setAdapter(categoryAdapter);

        // set category spinner listener
        categorySpinner.setOnItemSelectedListener(createCategorySpinnerListener());
    }

    private void updateCurrentCategory(Enums.PopularIndicatorsCategories category) {

        // update current category


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

        // set category spinner adapter and enable it
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.simple_dropdown_text_cell,
                spinnerList);
        parameterSpinner.setAdapter(parameterAdapter);
        parameterSpinner.setEnabled(true);

        // set category parameter listener
        parameterSpinner.setOnItemSelectedListener(createParameterSpinnerListener());

    }

    private AdapterView.OnItemSelectedListener createCategorySpinnerListener() {

        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // retrieve selected category and call UI updates
                String chosenCategory = (String) parent.getItemAtPosition(position);
                for(Enums.PopularIndicatorsCategories c : Enums.PopularIndicatorsCategories.values()) {
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
                if(currentCategory != null) {
                    launchQuery(chosenParameter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    private void launchQuery(String parameter) {

        final OnFirebaseErrorListener errorListener = createErrorListener();

        switch(currentCategory) {

            case GENDER:

                // retrieve gender parameter
                Enums.GenderEnum gender = null;
                for(Enums.GenderEnum g : Enums.GenderEnum.values()) {
                    if(g.toString().equals(parameter)) {
                        gender = g;
                    }
                }

                pop
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

    }


    private void displayListResult() {

    }

    private OnFirebaseErrorListener createErrorListener() {

        return new OnFirebaseErrorListener() {
            
            @Override
            public void onError(String message) {
                throw new DatabaseException(message);
            }
        };
    }

    private void demo(View view) {


        final OnSharedPoolRetrievalListener listener = new OnSharedPoolRetrievalListener() {
            @Override
            public void onSharedPoolRetrieved(List<ShareRank> sharedPool) {

                int[] scores = popularIndicatorsByCategory(sharedPool,
                        Enums.PopularIndicatorsCategories.COUNTRY,
                        (Object) "CHE");
                i1.setText("Indicator 1 : " + scores[0]);
                i2.setText("Indicator 2 : " + scores[1]);
                i3.setText("Indicator 3 : " + scores[2]);
                i4.setText("Indicator 4 : " + scores[3]);
                i5.setText("Indicator 5 : " + scores[4]);
            }
        };

        final OnFirebaseErrorListener error = new OnFirebaseErrorListener() {
            @Override
            public void onError(String message) {

            }
        };

        Button demoButton = null;
        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.retrieveSharedPool(listener, error);
            }
        });
    }

    /**
     * Retrieves the total scores gathered by indicators in the shared pool in aggregations that
     * belong to a precise category.
     *
     * @param sharedPool    all shared aggregations
     * @param category      category by which to consider aggregations
     * @param parameter     parameter depending on category
     *
     * @return              array containing indicator scores
     */
    private int[] popularIndicatorsByCategory(List<ShareRank> sharedPool, Enums.PopularIndicatorsCategories category, Object parameter) {

        ShareRankFilter categoryFilter = null;

        switch(category) {
            case GENDER:
                categoryFilter = createGenderFilter();
                break;
            case TYPE:
                categoryFilter = createUserTypeFilter();
                break;
            case BIRTHYEAR:
                categoryFilter = createBirthyearFilter();
                break;
            case TIMEFRAME:
                categoryFilter = createTimeFrameFilter();
                break;
            case COUNTRY:
                categoryFilter = createCountryFilter();
                break;
            default:
                throw new IllegalStateException("Unknown element in categories enumeration");
        }

        int[] indicatorsScores = new int[Tables.IndicatorsList.values().length];

        for(ShareRank sharedAggregation : sharedPool) {
            if(categoryFilter.filter(parameter, sharedAggregation)) {
                Map<Integer, Integer> settings = ShareRank.reprocessSettings(sharedAggregation.settings);
                for(Map.Entry<Integer, Integer> entries : settings.entrySet()) {
                    indicatorsScores[entries.getKey()] += entries.getValue();
                }
            }
        }

        return indicatorsScores;
    }

    private ShareRankFilter createGenderFilter() {

        return (param, sharedAggregation) -> {

            // argument check
            if(param == null || sharedAggregation == null) {
                throw new IllegalArgumentException("Filter arguments cannot be null");
            } else if(!(param instanceof Enums.GenderEnum)) {
                throw new IllegalArgumentException("This filter has to be called with a gender as parameter");
            }

            Enums.GenderEnum gender = (Enums.GenderEnum) param;

            return sharedAggregation.gender.equals(gender.toString());

        };
    }

    private ShareRankFilter createUserTypeFilter() {

        return (param, sharedAggregation) -> {

            // argument check
            if(param == null || sharedAggregation == null) {
                throw new IllegalArgumentException("Filter arguments cannot be null");
            } else if(!(param instanceof Enums.TypesOfUsers)) {
                throw new IllegalArgumentException("This filter has to be called with a user type as parameter");
            }

            Enums.TypesOfUsers type = (Enums.TypesOfUsers) param;

            return sharedAggregation.userType.equals(type.toString());
        };
    }

    private ShareRankFilter createBirthyearFilter() {

        return (param, sharedAggregation) -> {

            // argument check
            if(param == null || sharedAggregation == null) {
                throw new IllegalArgumentException("Filter arguments cannot be null");
            } else if(!(param instanceof Range)) {
                throw new IllegalArgumentException("This filter has to be called with a range as parameter");
            }

            // assumes date formatted like dd mmm aaaa
            Range yearRange = (Range) param;
            Integer year = sharedAggregation.birthYear;

            return yearRange.within(year);
        };
    }

    private ShareRankFilter createTimeFrameFilter() {

        return (param, sharedAggregation) -> {

            // argument check
            if(param == null || sharedAggregation == null) {
                throw new IllegalArgumentException("Filter arguments cannot be null");
            } else if(!(param instanceof Enums.TimeFrame)) {
                throw new IllegalArgumentException("This filter has to be called with a timeframe as parameter");
            }

            Enums.TimeFrame timeWindow = (Enums.TimeFrame) param;

            String currentDate = firebaseHelper.generateDate();
            String sharedDate = sharedAggregation.date;
            boolean belongs = false;

            // TODO: different date formats in different phones? look it up
            switch(timeWindow) {
                case MONTH:
                    // keep month and year from dates so that we can check for month equality
                    belongs = currentDate.substring(3).equals(sharedDate.substring(3));
                    break;
                case YEAR:
                    // cast years and compare
                    belongs =   Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(' ') + 1)) ==
                                Integer.parseInt(sharedDate.substring(sharedDate.lastIndexOf(' ') + 1));
                    break;
                default:
                    throw new IllegalStateException("Unknown element in TimeFrame enumeration");
            }

            return belongs;
        };
    }

    private ShareRankFilter createCountryFilter() {

        return (param, sharedAggregation) -> {

            // argument check
            if(param == null || sharedAggregation == null) {
                throw new IllegalArgumentException("Filter arguments cannot be null");
            } else if(!(param instanceof String)) {
                throw new IllegalArgumentException("This filter has to be called with a string as parameter");
            }

            String countryCode = (String) param;
            String aggregationCountry = sharedAggregation.country;

            return countryCode.equals(aggregationCountry);
        };
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

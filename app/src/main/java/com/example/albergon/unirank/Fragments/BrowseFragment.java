package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.ShareRankFilter;

import java.util.List;
import java.util.Map;

public class BrowseFragment extends Fragment {

    private OnBrowseFragmentInteractionListener interactionListener;

    private FirebaseHelper firebaseHelper = null;
    private DatabaseHelper databaseHelper = null;

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

        demo(view);

        return view;
    }

    private void demo(View view) {

        i1 = (TextView) view.findViewById(R.id.i1);
        i2 = (TextView) view.findViewById(R.id.i2);
        i3 = (TextView) view.findViewById(R.id.i3);
        i4 = (TextView) view.findViewById(R.id.i4);
        i5 = (TextView) view.findViewById(R.id.i5);

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

        Button demoButton = (Button) view.findViewById(R.id.demoButton);
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
                categoryFilter = createTimeframeFilter();
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

    private ShareRankFilter createTimeframeFilter() {

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

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
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.R;

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

                int[] scores = popularIndicatorsByGender(Enums.GenderEnum.MALE, sharedPool);
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
     * Retrieves The total scores gathered by indicators in the shared pool in aggregations submitted
     * by either males or females.
     *
     * @param gender        considered gender
     * @param sharedPool    all shared aggregations
     * @return              array containing indicator scores
     */
    private int[] popularIndicatorsByGender(Enums.GenderEnum gender, List<ShareRank> sharedPool) {

        int[] indicatorsScores = new int[Tables.IndicatorsList.values().length];

        for(ShareRank sharedAggregation : sharedPool) {
            if(sharedAggregation.gender.equals(gender.toString())) {
                Map<Integer, Integer> settings = ShareRank.reprocessSettings(sharedAggregation.settings);
                for(Map.Entry<Integer, Integer> entries : settings.entrySet()) {
                    indicatorsScores[entries.getKey()] += entries.getValue();
                }
            }
        }

        return indicatorsScores;
    }

    private int[] popularIndicatorsByUserType(Enums.TypesOfUsers type, List<ShareRank> sharedPool) {
        
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

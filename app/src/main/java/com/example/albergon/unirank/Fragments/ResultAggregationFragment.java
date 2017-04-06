package com.example.albergon.unirank.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.UniversityListAdapter;
import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultAggregationFragment extends Fragment {

    private static final String SETTINGS = "settings";
    private ResultFragmentInteractionListener interactionListener = null;

    private Map<Integer, Integer> settings;
    private Aggregator aggregator = null;
    private DatabaseHelper databaseHelper = null;

    // UI elements
    ListView resultList = null;
    Button saveBtn = null;
    Button shareBtn = null;
    Button modifyBtn = null;
    Button newRankingBtn = null;

    public ResultAggregationFragment() {
        // Required empty public constructor
    }

    public static ResultAggregationFragment newInstance(HashMap<Integer, Integer> settings) {
        ResultAggregationFragment fragment = new ResultAggregationFragment();
        Bundle args = new Bundle();
        // assume hash map usage since it's serializable
        args.putSerializable(SETTINGS, settings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.result_ranking_fragment, container, false);

        if (getArguments() != null) {
            settings = (HashMap<Integer, Integer>) getArguments().getSerializable(SETTINGS);
        }

        // fragment setup
        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();
        setupUI(view);
        addButtonsBehavior();

        // TODO: move to async task and show progress spinner
        // perform aggregation and display it
        Ranking<Integer> result = performAggregation();
        displayRanking(result);

        return view;
    }

    private void setupUI(View view) {

        resultList = (ListView) view.findViewById(R.id.result_list);
        saveBtn = (Button) view.findViewById(R.id.result_save_btn);
        modifyBtn = (Button) view.findViewById(R.id.result_modify_btn);
        newRankingBtn = (Button) view.findViewById(R.id.result_new_button);
        shareBtn = (Button) view.findViewById(R.id.result_share_btn);
    }

    private void addButtonsBehavior() {

        newRankingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.restartGeneration();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.startGenerationWithSettings(settings);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private Ranking<Integer> performAggregation() {

        aggregator = new Aggregator(new HodgeRanking());

        for(Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            Indicator indicator = databaseHelper.getIndicator(entry.getKey());
            aggregator.add(indicator, entry.getValue());
        }

        return aggregator.aggregate();
    }

    private void displayRanking(Ranking<Integer> ranking) {

        List<Integer> idList = ranking.getList();
        List<University> uniList = new ArrayList<>();

        for(int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            University uni = databaseHelper.getUniversity(id);
            uniList.add(uni);
        }

        UniversityListAdapter adapter = new UniversityListAdapter(getContext(),
                R.layout.ranking_list_cell,
                uniList);

        resultList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultAggregationFragment.ResultFragmentInteractionListener) {
            interactionListener = (ResultAggregationFragment.ResultFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ResultFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface ResultFragmentInteractionListener {

        void restartGeneration();

        void startGenerationWithSettings(Map<Integer, Integer> settings);
    }

}

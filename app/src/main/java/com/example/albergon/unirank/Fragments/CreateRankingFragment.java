package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.albergon.unirank.AsyncIndicatorListAdd;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.IndicatorListAdapter;
import com.example.albergon.unirank.LayoutAdapters.UniversityListAdapter;
import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateRankingFragment extends Fragment {

    // optional aggregation update parameter
    private static final String OLD_SETTINGS = "old_settings";

    // UI elements
    private ListView indicatorList = null;
    private IndicatorListAdapter adapter = null;
    private Button addIndicatorBtn = null;
    private Button generateBtn = null;
    private Button loadBtn = null;

    private DatabaseHelper databaseHelper = null;
    private Map<Integer, Integer> currentSettings = null;
    private OnRankGenerationInteractionListener interactionListener = null;

    //TODO: remove when correct indicator picking is implemented
    private int count = 0;

    public static CreateRankingFragment newInstanceFromSettings(HashMap<Integer, Integer> settings) {
        CreateRankingFragment fragment = new CreateRankingFragment();
        Bundle args = new Bundle();
        // assume hash map usage since it's serializable
        args.putSerializable(OLD_SETTINGS, settings);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.create_ranking_fragment, container, false);

        // structures and helpers setup
        currentSettings = new HashMap<>();

        // UI setup
        setupUI(view);
        addButtonsBehavior();

        if (getArguments() != null) {
            currentSettings = (HashMap<Integer, Integer>) getArguments().getSerializable(OLD_SETTINGS);
            updateListFromSettings();
        }

        return view;
    }

    private void updateListFromSettings() {

        for(Map.Entry<Integer, Integer> entry : currentSettings.entrySet()) {
            // add indicator to the list
            AsyncIndicatorListAdd task = new AsyncIndicatorListAdd(adapter, getContext());
            CustomSeekBarListener listener = new CustomSeekBarListener();
            Integer newIndicator = entry.getKey();
            listener.bindToIndicator(newIndicator);
            AsyncIndicatorListAdd.AsyncTuple tuple =
                    new AsyncIndicatorListAdd.AsyncTuple(newIndicator, listener);
            task.execute(tuple);
        }
    }

    private void setupUI(View view) {
        // setup UI adding mechanism
        adapter = new IndicatorListAdapter(getContext(), R.layout.indicator_list_cell);
        indicatorList = (ListView) view.findViewById(R.id.indicator_list);
        indicatorList.setAdapter(adapter);

        // setup buttons
        addIndicatorBtn = (Button) view.findViewById(R.id.add_indicator_btn);
        loadBtn = (Button) view.findViewById(R.id.load_btn);
        generateBtn = (Button) view.findViewById(R.id.generate_btn);
    }

    private void addButtonsBehavior() {

        addIndicatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add indicator to the list
                AsyncIndicatorListAdd task = new AsyncIndicatorListAdd(adapter, getContext());
                CustomSeekBarListener listener = new CustomSeekBarListener();
                Integer newIndicator = count;
                AsyncIndicatorListAdd.AsyncTuple tuple =
                        new AsyncIndicatorListAdd.AsyncTuple(newIndicator, listener);
                task.execute(tuple);
                count += 1;

                // update settings
                currentSettings.put(newIndicator, 1);
                listener.bindToIndicator(newIndicator);
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                interactionListener.onPressGenerate(currentSettings);

            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateRankingFragment.OnRankGenerationInteractionListener) {
            interactionListener = (CreateRankingFragment.OnRankGenerationInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRankGenerationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface OnRankGenerationInteractionListener {

        void onPressGenerate(Map<Integer, Integer> settings);
    }

    private class CustomSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        private Integer indicator = null;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                currentSettings.put(indicator, progress+1);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        public void bindToIndicator(Integer i) {
            indicator = i;
        }
    }
}

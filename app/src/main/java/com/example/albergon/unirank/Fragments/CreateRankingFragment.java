package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.albergon.unirank.AsyncIndicatorListAdd;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.IndicatorListAdapter;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This fragment allows the user to choose the parameters of an aggregation: selecting indicators
 * and weights. It also allows to load an existing save and open its settings.
 */
public class CreateRankingFragment extends Fragment {

    // optional aggregation update parameter
    private static final String OLD_SETTINGS = "old_settings";
    private static final String OLD_RANKING = "old_ranking";

    // UI elements
    private ListView indicatorList = null;
    private IndicatorListAdapter adapter = null;
    private Button addIndicatorBtn = null;
    private Button generateBtn = null;
    private Button loadBtn = null;

    // other attributes
    private DatabaseHelper databaseHelper = null;
    private Map<Integer, Integer> currentSettings = null;
    private ArrayList<Integer> oldRanking = null;
    private OnRankGenerationInteractionListener interactionListener = null;


    /**
     * Static factory method which allows to create an instance of this fragment with some given
     * settings and a resulting ranking included. Useful to open and modify old aggregations.
     *
     * @param settings  settings given as a map from indicator ids and weights
     * @param ranking   result generated by old settings
     * @return          a fragment instance with the given indicators displayed
     */
    public static CreateRankingFragment newInstanceFromSettings(HashMap<Integer, Integer> settings,
                                                                ArrayList<Integer> ranking) {

        // arguments check
        if(settings == null || ranking == null) {
            throw new IllegalArgumentException("Cannot instantiate ranking creation with null arguments");
        }

        CreateRankingFragment fragment = new CreateRankingFragment();
        Bundle args = new Bundle();
        // assume hash map usage since it's serializable
        args.putSerializable(OLD_SETTINGS, settings);
        // assume array list usage since it's serializable
        args.putSerializable(OLD_RANKING, ranking);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_create, container, false);

        // structures and helpers setup
        currentSettings = new HashMap<>();

        // UI setup
        setupUI(view);
        addButtonsBehavior();

        // If the fragment is created based on old settings, "load them" together with the old result
        if (getArguments() != null) {
            //noinspection unchecked
            currentSettings = (HashMap<Integer, Integer>) getArguments().getSerializable(OLD_SETTINGS);
            //noinspection unchecked
            oldRanking = (ArrayList<Integer>) getArguments().getSerializable(OLD_RANKING);
            updateListFromSettings();
        }

        return view;
    }

    /**
     * This method updates the UI, inserting the current settings in the ListView. Usually invoked
     * after the settings are updated either from loading a save or when new indicators are selected.
     */
    private void updateListFromSettings() {

        for(Map.Entry<Integer, Integer> entry : currentSettings.entrySet()) {

            // add indicator to the list
            AsyncIndicatorListAdd task = new AsyncIndicatorListAdd(adapter, getContext());
            CustomSeekBarListener listener = new CustomSeekBarListener();
            Integer newIndicator = entry.getKey();
            listener.bindToIndicator(newIndicator);
            AsyncIndicatorListAdd.IndicatorCellContent tuple =
                    new AsyncIndicatorListAdd.IndicatorCellContent(newIndicator, listener, entry.getValue());
            task.execute(tuple);
        }
    }

    /**
     * This method is invoked once the dialog used to choose indicators which we want to add to the
     * aggregation is closed. All new indicators are gathered and added to the UI through a call to
     * updateListFromSetting()
     *
     * @param picked    set of indicators picked through the dialog
     */
    public void updateListFromDialog(Set<Integer> picked) {

        // arguments check
        if(picked == null) {
            throw new IllegalArgumentException("Set of picked indicators is null");
        }

        Set<Integer> toAdd = new HashSet<>(picked);
        @SuppressLint("UseSparseArrays") Map<Integer, Integer> newSettings = new HashMap<>();

        // add new indicators to current settings, keeping the weight if they were already present
        for(Integer indicator : toAdd) {
            if (!currentSettings.keySet().contains(indicator)) {
                newSettings.put(indicator, 1);
            } else {
                newSettings.put(indicator, currentSettings.get(indicator));
            }
        }

        // reset adapter to clean ListView
        adapter = null;
        adapter = new IndicatorListAdapter(getContext(), R.layout.cell_indicator_list);
        indicatorList.setAdapter(adapter);
        currentSettings = newSettings;

        // update UI
        updateListFromSettings();
    }

    /**
     * Load views from layout files, in particular buttons and the ListView, for which an adapter is
     * instantiated and set.
     *
     * @param view  root view
     */
    private void setupUI(View view) {

        // setup UI updating mechanism
        adapter = new IndicatorListAdapter(getContext(), R.layout.cell_indicator_list);
        indicatorList = (ListView) view.findViewById(R.id.indicator_list);
        indicatorList.setAdapter(adapter);

        // setup buttons
        addIndicatorBtn = (Button) view.findViewById(R.id.add_indicator_btn);
        loadBtn = (Button) view.findViewById(R.id.load_btn);
        generateBtn = (Button) view.findViewById(R.id.generate_btn);
    }

    /**
     * Add behavior to fragment's buttons
     */
    private void addButtonsBehavior() {

        // the add indicator button shows the picking dialog
        addIndicatorBtn.setOnClickListener(v -> {
            interactionListener.showPickIndicatorDialog(currentSettings.keySet());
        });

        // the generate button forwards the desired settings to the result fragment which will
        // compute the aggregation
        generateBtn.setOnClickListener(v -> {

            if(currentSettings.size() == 0) {
                Toast.makeText(getActivity(), "Choose at least one indicator", Toast.LENGTH_LONG).show();
            } else {
                interactionListener.onPressGenerate(currentSettings, oldRanking);
            }

        });

        // opens the picking dialog for saved aggregations
        loadBtn.setOnClickListener(v -> interactionListener.showLoadSaveDialog());

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

        void onPressGenerate(Map<Integer, Integer> settings, List<Integer> oldRanking);

        void showPickIndicatorDialog(Set<Integer> alreadyPicked);

        void showLoadSaveDialog();
    }

    /**
     * This custom SeekBar listener is used to keep track of the weight of one of the indicators
     * in the current settings.
     */
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

        /**
         * Bind the listener to an indicator, meaning that the SeekBar updates the weight of that
         * indicator.
         *
         * @param i     indicator id
         */
        public void bindToIndicator(Integer i) {
            indicator = i;
        }
    }
}

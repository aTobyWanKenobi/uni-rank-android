package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.UniversityListAdapter;
import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is used to compute and display the results of an aggregation.
 */
public class ResultAggregationFragment extends Fragment {

    // Factory parameter and interaction listener
    private static final String SETTINGS = "settings";
    private ResultFragmentInteractionListener interactionListener = null;

    private Map<Integer, Integer> settings;
    private Aggregator aggregator = null;
    private DatabaseHelper databaseHelper = null;

    // UI elements
    private ListView resultList = null;
    private Button saveBtn = null;
    private Button shareBtn = null;
    private Button modifyBtn = null;
    private Button newRankingBtn = null;

    /**
     * Static factory method that passes the settings of the aggregation to perform. It should be
     * the only way this fragment class is instantiated.
     *
     * @param settings  settings from the CreateRankingFragment
     * @return          a result fragment with the desired settings
     */
    public static ResultAggregationFragment newInstance(HashMap<Integer, Integer> settings) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Cannot use factory method with null parameter");
        }

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

        // Get settings deriving from factory method parameter
        if (getArguments() != null) {
            //noinspection unchecked
            settings = (HashMap<Integer, Integer>) getArguments().getSerializable(SETTINGS);
        }

        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();

        // UI instantiation and behavior
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

    /**
     * Add behavior to the 4 bottom buttons.
     */
    private void addButtonsBehavior() {

        newRankingBtn.setOnClickListener(v -> interactionListener.restartGeneration());

        modifyBtn.setOnClickListener(v -> interactionListener.startGenerationWithSettings(settings));

        saveBtn.setOnClickListener(v -> showSaveDialog());

        //TODO: implement sharing mechanism
        shareBtn.setOnClickListener(v -> {

        });

    }

    //TODO: implement naming conflict resolution
    /**
     * Show the save dialog, which lets the user input a name for the aggregation he wants to save.
     */
    private void showSaveDialog() {

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save as");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {

            String name = input.getText().toString();
            if(databaseHelper.saveAlreadyPresent(name)) {
                restartSaveDialogWithToast();
            } else {
                saveAggregation(name);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * This methods reloads the save dialog but additionally displays a toast prompting the user to
     * insert a different name for the save
     */
    private void restartSaveDialogWithToast() {

        Toast.makeText(getContext(), "Name already used, choose another", Toast.LENGTH_LONG).show();
        showSaveDialog();
    }

    /**
     * Save the current aggregation with the desired name and the current date.
     *
     * @param name  desired name for the save
     */
    private void saveAggregation(String name) {

        // generate date in string format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = dateFormat.format(new Date());

        // instantiate and save aggregation
        SaveRank save = new SaveRank(name, date, settings, aggregator.getResult().getList());
        databaseHelper.saveAggregation(save);

        // disable save button
        saveBtn.setEnabled(false);
    }

    // TODO: move computation to async task and implement progress spinner in fragment
    /**
     * This method performs the aggregation with the input settings and returns the resulting ranking.
     *
     * @return  the result of the aggregation
     */
    private Ranking<Integer> performAggregation() {

        aggregator = new Aggregator(new HodgeRanking());

        // Add settings to Aggregator object
        for(Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            Indicator indicator = databaseHelper.getIndicator(entry.getKey());
            aggregator.add(indicator, entry.getValue());
        }

        return aggregator.aggregate();
    }

    /**
     * Displays the result into a scrollable ListView.
     *
     * @param ranking   ranking to display
     */
    private void displayRanking(Ranking<Integer> ranking) {

        List<Integer> idList = ranking.getList();
        List<University> uniList = new ArrayList<>();

        // retrieve Universities from database thanks to ids
        for(int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            University uni = databaseHelper.getUniversity(id);
            uniList.add(uni);
        }

        // Setup ListView adapter
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

    /**
     * Interaction listener for implementing activity.
     */
    public interface ResultFragmentInteractionListener {

        void restartGeneration();

        void startGenerationWithSettings(Map<Integer, Integer> settings);
    }

}

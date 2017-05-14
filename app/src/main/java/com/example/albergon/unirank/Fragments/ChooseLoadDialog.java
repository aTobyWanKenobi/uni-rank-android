package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.LayoutAdapters.LoadListAdapter;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Dialog fragment defines the behavior of the AlertDialog used to display the list of saves
 * which we can load into the rank generation fragment.
 */
public class ChooseLoadDialog extends DialogFragment {

    //arguments for comparison instance
    private boolean comparisonActive = false;

    public static final String OTHER_NAME = "name";
    public static final String OTHER_LIST = "list";
    public static final String OTHER_SCORES = "scores";
    public static final String OTHER_SETTINGS = "settings";

    private SaveRank otherToCompare = null;

    // Interaction listener
    private OnChooseLoadDialogInteractionListener interactionListener = null;

    public static ChooseLoadDialog newInstance(String name,
                                              ArrayList<Integer> rank,
                                              HashMap<Integer, Double> score,
                                              HashMap<Integer, Integer> settings)  {
        ChooseLoadDialog fragment = new ChooseLoadDialog();
        Bundle args = new Bundle();
        args.putString(OTHER_NAME, name);
        args.putSerializable(OTHER_LIST, rank);
        args.putSerializable(OTHER_SCORES, score);
        args.putSerializable(OTHER_SETTINGS, settings);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_choose_load, null);

        // get arguments if fragment was launched for comparison
        if (getArguments() != null) {
            String name = getArguments().getString(OTHER_NAME);
            List<Integer> rank = (List<Integer>) getArguments().getSerializable(OTHER_LIST);
            Map<Integer, Double> score = (Map<Integer, Double>) getArguments().getSerializable(OTHER_SCORES);
            Map<Integer, Integer> settings = (Map<Integer, Integer>) getArguments().getSerializable(OTHER_SETTINGS);

            Ranking<Integer> ranking = new Ranking<Integer>(rank, score);
            otherToCompare = new SaveRank(name, FirebaseHelper.generateDate(), settings, ranking);
            comparisonActive = true;
        }

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // Initialize UI elements
        ListView loadPickList = (ListView) view.findViewById(R.id.choose_load_list);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_load_dialog);

        // Get database and fetch all save names
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext());
        List<String> saveList = databaseHelper.retrieveAllSavesName();

        // Set ListView adapter
        LoadListAdapter adapter = new LoadListAdapter(getContext(),
                R.layout.cell_pick_load_list,
                saveList,
                v -> {
                    // Open save when its row is clicked
                    LoadListAdapter.ChooseSaveHolder row = (LoadListAdapter.ChooseSaveHolder) v.getTag();
                    String name = row.getName().getText().toString();
                    if(comparisonActive) {
                        interactionListener.openComparisonSave(name, otherToCompare);
                    } else {
                        interactionListener.openSaveFromLoadDialog(name);
                    }
                    dialog.dismiss();
             });
        loadPickList.setAdapter(adapter);

        // Button behavior
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseLoadDialogInteractionListener) {
            interactionListener = (OnChooseLoadDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChooseLoadDialogInteractionListener");
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
    public interface OnChooseLoadDialogInteractionListener {
        void openSaveFromLoadDialog(String name);

        void openComparisonSave(String name, SaveRank otherSave);
    }


}

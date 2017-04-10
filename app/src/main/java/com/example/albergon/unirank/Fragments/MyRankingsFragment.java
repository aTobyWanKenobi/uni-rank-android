package com.example.albergon.unirank.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.SavesListAdapter;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.util.List;

/**
 * This fragment implements the navigation through locally saved rankings. It allows to open and
 * modify an old save after getting a preview of its settings.
 */
public class MyRankingsFragment extends Fragment {

    // Interaction listener
    private MyRankingsFragmentInteractionListener interactionListener = null;

    // UI elements
    private Button openBtn = null;
    private TextView dateTemporary = null;
    private ListView savesList = null;

    private DatabaseHelper databaseHelper = null;
    private SaveRank currentlySelectedSave = null;

    // Static factory method
    public static MyRankingsFragment newInstance() {
        return new MyRankingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_my_rankings, container, false);

        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();

        //TODO: just for test
        dateTemporary = (TextView) view.findViewById(R.id.selected_save_date);

        //UI
        savesList = (ListView) view.findViewById(R.id.saves_list);
        openBtn = (Button) view.findViewById(R.id.open_save);
        openBtn.setOnClickListener(v -> {
            if(currentlySelectedSave == null) {
                Toast.makeText(getContext(), "You must select a save to open", Toast.LENGTH_LONG).show();
            } else {
                interactionListener.openSaveFromMyRanking(currentlySelectedSave);
            }
        });

        displaySaves();

        return view;
    }

    /**
     * Display all saves present in the local database
     */
    private void displaySaves() {

        // fetch from database
        final List<String> saves = databaseHelper.fetchAllSavesName();

        // click listener
        View.OnClickListener rowListener = v -> {
            // preview currently selected save
            String name = ((SavesListAdapter.SaveHolder)v.getTag()).getName().getText().toString();
            currentlySelectedSave = databaseHelper.getSave(name);
            dateTemporary.setText(currentlySelectedSave.getDate());
        };

        // Setup ListView adapter
        SavesListAdapter adapter = new SavesListAdapter(getContext(),
                R.layout.saving_list_cell,
                saves,
                rowListener);
        savesList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyRankingsFragmentInteractionListener) {
            interactionListener = (MyRankingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MyRankingsFragmentInteractionListener");
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
    public interface MyRankingsFragmentInteractionListener {

        void openSaveFromMyRanking(SaveRank toOpen);
    }

}

package com.example.albergon.unirank.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.albergon.unirank.Database.CallbackHandlers.OnShareRankUploadListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.LayoutAdapters.SavesListAdapter;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This fragment implements the navigation through locally saved rankings. It allows to open and
 * modify an old save after getting a preview of its settings.
 */
public class MyRankingsFragment extends Fragment {

    // Interaction listener
    private MyRankingsFragmentInteractionListener interactionListener = null;

    // UI elements
    private ExpandableListView savesList = null;

    private DatabaseHelper databaseHelper = null;
    private FirebaseHelper firebaseHelper = null;

    // Static factory method
    public static MyRankingsFragment newInstance() {
        return new MyRankingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_my_rankings, container, false);

        databaseHelper = DatabaseHelper.getInstance(getContext());
        firebaseHelper = new FirebaseHelper(getContext());

        //UI
        savesList = (ExpandableListView) view.findViewById(R.id.saves_exp_list);

        displaySaves();

        return view;
    }

    private void uploadToFirebase(String name) {

        OnShareRankUploadListener callbackHandler = new OnShareRankUploadListener() {
            @Override
            public void onUploadCompleted(boolean successful) {

                if(successful) {
                    // TODO disable upload of same ranking
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
                }
            }
        };

        SaveRank toUpload = databaseHelper.retrieveSave(name);
        firebaseHelper.uploadAggregation(toUpload.getResultList(), toUpload.getSettings(), callbackHandler);
    }

    /**
     * Display all saves present in the local database
     */
    private void displaySaves() {

        // fetch from database
        final List<String> saves = databaseHelper.retrieveAllSavesName();

        // construct settings mapping
        final Map<String, Map<Integer, Integer>> settingsMap = new HashMap<>();
        for(String save : saves) {
            Map<Integer, Integer> saveSettings = databaseHelper.retrieveSave(save).getSettings();
            Map<Integer, Integer> completeSettings = new HashMap<>();
            for(int i = 0; i < Tables.IndicatorsList.values().length; i++) {
                completeSettings.put(i, saveSettings.getOrDefault(i, 0));
            }
            settingsMap.put(save, completeSettings);
        }

        // Setup ListView adapter
        SavesListAdapter adapter = new SavesListAdapter(getContext(),
                saves,
                settingsMap,
                createButtonHandler());
        savesList.setAdapter(adapter);
    }

    private MyRankingButtonHandler createButtonHandler() {
        return new MyRankingButtonHandler() {
            @Override
            public void open(String name) {
                SaveRank toOpen = databaseHelper.retrieveSave(name);
                interactionListener.openSaveFromMyRanking(toOpen);
            }

            @Override
            public void compare(String name) {
                SaveRank toCompare = databaseHelper.retrieveSave(name);
                interactionListener.launchCompareDialog(toCompare);
            }

            @Override
            public void share(String name) {
                uploadToFirebase(name);
            }

            @Override
            public void delete(String name) {
                // delete save and refresh list
                databaseHelper.deleteSavedAggregation(name);
                displaySaves();
            }
        };
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

        void launchCompareDialog(SaveRank currentlySelected);

    }

}

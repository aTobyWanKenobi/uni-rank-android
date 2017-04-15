package com.example.albergon.unirank.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This fragment implements the navigation through locally saved rankings. It allows to open and
 * modify an old save after getting a preview of its settings.
 */
public class MyRankingsFragment extends Fragment {

    // Interaction listener
    private MyRankingsFragmentInteractionListener interactionListener = null;

    // UI elements
    private Button openBtn = null;
    private Button deleteBtn = null;
    private Button shareBtn = null;
    private ListView savesList = null;
    private TextView selectedNameTxt = null;
    private TextView selectedDateTxt = null;
    private TextView selectedSettingsTxt = null;

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

        databaseHelper = DatabaseHelper.getInstance(getContext());

        //TODO: just for test
        selectedNameTxt = (TextView) view.findViewById(R.id.selected_save_name);
        selectedDateTxt = (TextView) view.findViewById(R.id.selected_save_date);
        selectedSettingsTxt = (TextView) view.findViewById(R.id.selected_save_settings);

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

        deleteBtn = (Button) view.findViewById(R.id.delete_save);
        deleteBtn.setOnClickListener(v -> {
            if(currentlySelectedSave == null) {
                Toast.makeText(getContext(), "You must select a save to delete", Toast.LENGTH_LONG).show();
            } else {
                // delete save and refresh list
                databaseHelper.deleteSavedAggregation(currentlySelectedSave.getName());
                displaySaves();
            }
        });

        shareBtn = (Button) view.findViewById(R.id.share_save);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentlySelectedSave == null) {
                    Toast.makeText(getContext(), "You must select a save to share", Toast.LENGTH_LONG).show();
                } else {
                    uploadToFirebase();
                    shareBtn.setEnabled(false);
                }
            }
        });

        displaySaves();

        return view;
    }

    //TODO: move to a firebase helper
    private void uploadToFirebase() {

        DatabaseReference firebase = ((TabbedActivity) getActivity()).getFirebaseInstance();

        Settings userSettings = databaseHelper.retriveSettings(false);
        ShareRank toShare = new ShareRank(generateDate(),
                currentlySelectedSave.getResult(),
                currentlySelectedSave.getSettings(),
                userSettings);
        String randomId = String.valueOf(generateRandomId());

        firebase.child("shared").child(randomId).setValue(toShare);
        firebase.child("shared").child(randomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shareBtn.setEnabled(false);
                Toast.makeText(getContext(), "Upload succesful", Toast.LENGTH_LONG).show();
                //Todo: implement correct flow when uploading
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: better firebase error handling
                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
                throw new DatabaseException(databaseError.getMessage());
            }
        });

    }

    //TODO: move to a firebase helper
    private int generateRandomId() {
        Random rnd = new Random();
        int rndNumber = 100000 + rnd.nextInt(900000);

        return rndNumber;
    }

    //TODO: move to a firebase helper
    private String generateDate() {
        // generate date in string format
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = dateFormat.format(new Date());

        return date;
    }

    /**
     * Display all saves present in the local database
     */
    private void displaySaves() {

        // fetch from database
        final List<String> saves = databaseHelper.retrieveAllSavesName();

        // click listener
        View.OnClickListener rowListener = v -> {
            // preview currently selected save
            String name = ((SavesListAdapter.SaveHolder)v.getTag()).getName().getText().toString();
            currentlySelectedSave = databaseHelper.retrieveSave(name);

            selectedNameTxt.setText("Save name : " + currentlySelectedSave.getName());
            selectedDateTxt.setText("Save date : " +currentlySelectedSave.getDate());
            selectedSettingsTxt.setText("Settings :  " + currentlySelectedSave.getSettings());
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

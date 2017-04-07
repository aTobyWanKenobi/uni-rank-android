package com.example.albergon.unirank.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.SavesListAdapter;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyRankingsFragment extends Fragment {

    private MyRankingsFragmentInteractionListener interactionListener = null;

    private Button openBtn = null;
    private ListView savesList = null;
    private DatabaseHelper databaseHelper = null;

    private int currentlySelectedSave = -1;

    public MyRankingsFragment() {
        // Required empty public constructor
    }

    public static MyRankingsFragment newInstance() {
        MyRankingsFragment fragment = new MyRankingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_my_rankings, container, false);

        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();

        //UI
        openBtn = (Button) view.findViewById(R.id.open_save);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentlySelectedSave == -1) {
                    Toast.makeText(getContext(), "You must select a save to open", Toast.LENGTH_LONG).show();
                } else {
                    interactionListener.openSave(currentlySelectedSave);
                }
            }
        });

        //addSavesToDB();
        savesList = (ListView) view.findViewById(R.id.saves_list);
        displaySaves();

        return view;
    }

    private void displaySaves() {

        List<SaveRank> saves = databaseHelper.fetchAllSaves();

        View.OnClickListener rowListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentlySelectedSave = ((SavesListAdapter.SaveHolder)v.getTag()).getId();
            }
        };

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

    public interface MyRankingsFragmentInteractionListener {

        void openSave(int id);
    }

}

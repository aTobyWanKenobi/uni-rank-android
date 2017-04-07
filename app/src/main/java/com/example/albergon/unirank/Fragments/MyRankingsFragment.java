package com.example.albergon.unirank.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    ListView savesList = null;
    DatabaseHelper databaseHelper = null;

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

        //addSavesToDB();
        savesList = (ListView) view.findViewById(R.id.saves_list);
        displaySaves();

        return view;
    }

    //TODO: just for testing, remove
    private void addSavesToDB() {

        List<Integer> rank = new ArrayList<>();
        rank.add(1);
        Map<Integer, Integer> settings = new HashMap<>();
        settings.put(1, 1);

        SaveRank s1 = new SaveRank("Test1", "04/04/2017", settings, rank);
        SaveRank s2 = new SaveRank("Test2", "05/04/2017", settings, rank);
        SaveRank s3 = new SaveRank("Test3", "06/04/2017", settings, rank);
        SaveRank s4 = new SaveRank("Test4", "07/04/2017", settings, rank);

        databaseHelper.saveAggregation(s1);
        databaseHelper.saveAggregation(s2);
        databaseHelper.saveAggregation(s3);
        databaseHelper.saveAggregation(s4);
    }

    private void displaySaves() {

        List<SaveRank> saves = databaseHelper.fetchAllSaves();

        SavesListAdapter adapter = new SavesListAdapter(getContext(),
                R.layout.saving_list_cell,
                saves);

        savesList.setAdapter(adapter);
    }

}

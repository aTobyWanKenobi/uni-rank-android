package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.DisplaySettingsAdapter;
import com.example.albergon.unirank.LayoutAdapters.UniversityListAdapter;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompareFragment extends Fragment {

    private OnCompareFragmentInteractionListener interactionListener;

    // factory method parameters
    private static final String RANK_NAME_1 = "name1";
    private static final String RANK_LIST_1 = "rank1";
    private static final String SCORE_MAP_1 = "score1";
    private static final String SETTINGS_MAP_1 = "setting1";
    private static final String RANK_NAME_2 = "name2";
    private static final String RANK_LIST_2 = "rank2";
    private static final String SCORE_MAP_2 = "score2";
    private static final String SETTINGS_MAP_2 = "settings2";

    // ranks structures
    private String name1 = null;
    private String name2 = null;

    private List<University> uniList1 = null;
    private List<University> uniList2 = null;

    private List<Integer> rank1 = null;
    private Map<Integer, Double> score1 = null;
    private Map<Integer, Integer> settings1 = null;
    private List<Integer> rank2 = null;
    private Map<Integer, Double> score2 = null;
    private Map<Integer, Integer> settings2 = null;

    // UI elements
    private ListView shownRank = null;
    private TextView rank1Name = null;
    private ListView rank1Settings = null;
    private TextView rank2Name = null;
    private ListView rank2Settings = null;

    private CurrentlySelected selected = null;
    private DatabaseHelper databaseHelper = null;


    public static CompareFragment newInstance(String name1, ArrayList<Integer> rank1, HashMap<Integer, Double> score1, HashMap<Integer, Integer> settings1,
                                              String name2, ArrayList<Integer> rank2, HashMap<Integer, Double> score2, HashMap<Integer, Integer> settings2)  {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        args.putString(RANK_NAME_1, name1);
        args.putSerializable(RANK_LIST_1, rank1);
        args.putSerializable(SCORE_MAP_1, score1);
        args.putSerializable(SETTINGS_MAP_1, settings1);
        args.putString(RANK_NAME_2, name2);
        args.putSerializable(RANK_LIST_2, rank2);
        args.putSerializable(SCORE_MAP_2, score2);
        args.putSerializable(SETTINGS_MAP_2, settings2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        if (getArguments() != null) {
            name1 = getArguments().getString(RANK_NAME_1);
            rank1 = (List<Integer>) getArguments().getSerializable(RANK_LIST_1);
            score1 = (Map<Integer, Double>) getArguments().getSerializable(SCORE_MAP_1);
            settings1 = (Map<Integer, Integer>) getArguments().getSerializable(SETTINGS_MAP_1);
            name2 = getArguments().getString(RANK_NAME_2);
            rank2 = (List<Integer>) getArguments().getSerializable(RANK_LIST_2);
            score2 = (Map<Integer, Double>) getArguments().getSerializable(SCORE_MAP_2);
            settings2 = (Map<Integer, Integer>) getArguments().getSerializable(SETTINGS_MAP_2);
        } else {
            throw new IllegalStateException("Can instantiate compare fragment only through factory method");
        }

        databaseHelper = DatabaseHelper.getInstance(getContext());

        // Compute university lists
        uniList1 = computeUniversityListFromIds(rank1);
        uniList2 = computeUniversityListFromIds(rank2);

        setupUI(view);

        return view;
    }

    public List<University> computeUniversityListFromIds(List<Integer> idList) {

        List<University> uniList = new ArrayList<>();

        // retrieve Universities from database thanks to ids
        for(int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            University uni = databaseHelper.retrieveUniversity(id);
            uniList.add(uni);
        }

        return uniList;
    }


    private void setupUI(View view) {

        shownRank = (ListView) view.findViewById(R.id.compare_current_rank);
        rank1Name = (TextView) view.findViewById(R.id.compare_name_rank1);
        rank1Settings = (ListView) view.findViewById(R.id.compare_settings_rank1);
        rank2Name = (TextView) view.findViewById(R.id.compare_name_rank2);
        rank2Settings = (ListView) view.findViewById(R.id.compare_settings_rank2);

        rank1Name.setText(name1);
        rank2Name.setText(name2);

        //show rank 1 initially
        switchRank(CurrentlySelected.RANK1);

        //show settings recaps
        DisplaySettingsAdapter adapter1 = new DisplaySettingsAdapter(getContext(),
                R.layout.cell_settings_recap_small,
                settings1);
        rank1Settings.setAdapter(adapter1);

        DisplaySettingsAdapter adapter2 = new DisplaySettingsAdapter(getContext(),
                R.layout.cell_settings_recap_small,
                settings2);
        rank2Settings.setAdapter(adapter2);

        //add click behaviour to layouts
        //TODO

    }

    private void switchRank(CurrentlySelected rank) {

        UniversityListAdapter adapter;

        switch(rank) {
            case RANK1:
                adapter = new UniversityListAdapter(getContext(),
                        R.layout.cell_ranking_list,
                        uniList1,
                        rank2,
                        score1);
                break;
            case RANK2:
                adapter = new UniversityListAdapter(getContext(),
                        R.layout.cell_ranking_list,
                        uniList2,
                        rank1,
                        score2);
                break;
            default:
                throw new IllegalStateException("Unknown element of enum CurrentlySelected");
        }

        shownRank.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCompareFragmentInteractionListener) {
            interactionListener = (OnCompareFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCompareFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }


    public interface OnCompareFragmentInteractionListener {

    }

    private enum CurrentlySelected {
        RANK1, RANK2;
    }
}

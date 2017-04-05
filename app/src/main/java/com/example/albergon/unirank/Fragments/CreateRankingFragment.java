package com.example.albergon.unirank.Fragments;

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

    private ListView indicatorList = null;
    private IndicatorListAdapter adapter = null;

    private Indicator[] testIndicators = null;
    private int count = 0;

    private Aggregator aggregator = null;
    private DatabaseHelper databaseHelper = null;

    public CreateRankingFragment() {
        // Required empty public constructor
    }

    public static CreateRankingFragment newInstance() {
        CreateRankingFragment fragment = new CreateRankingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.create_ranking_fragment, container, false);

        // create aggregator with HodgeRank algorithm and database helper to setup aggregation
        aggregator = new Aggregator(new HodgeRanking());
        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();

        adapter = new IndicatorListAdapter(getContext(), R.layout.indicator_list_cell);
        indicatorList = (ListView) view.findViewById(R.id.indicator_list);
        indicatorList.setAdapter(adapter);

        createTest();
        addButtonsBehavior(view);

        return view;
    }

    //TODO: remove when correct indicator picking is implemented
    public void createTest() {
        testIndicators = new Indicator[5];
        testIndicators[0] = databaseHelper.getIndicator(0);
        testIndicators[1] = databaseHelper.getIndicator(1);
        testIndicators[2] = databaseHelper.getIndicator(2);
        testIndicators[3] = databaseHelper.getIndicator(3);
        testIndicators[4] = databaseHelper.getIndicator(4);
    }

    private SeekBar.OnSeekBarChangeListener createTestListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }


    private void addButtonsBehavior(View view) {

        ((Button) view.findViewById(R.id.add_indicator_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncIndicatorListAdd task = new AsyncIndicatorListAdd(adapter, getContext());
                AsyncIndicatorListAdd.AsyncTuple tuple = new AsyncIndicatorListAdd.AsyncTuple(
                        testIndicators[count], createTestListener()
                        );
                task.execute(tuple);

                count += 1;
            }
        });

        ((Button) view.findViewById(R.id.generate_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    private void restartFragment() {
        ((TabbedActivity) getActivity()).restartRankGeneration();
    }

    private View.OnClickListener createButtonListener(final int index) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*
                // Retrieve indicator from database and add it to aggregator with correct weight
                Indicator indicator = databaseHelper.getIndicator(index);
                int weight = seekBars[index].getProgress() + 1;
                aggregator.add(indicator, weight);

                // Disable button and seekbar
                seekBars[index].setEnabled(false);
                addButtons[index].setEnabled(false);
                */
            }
        };
    }

    private void displayRanking(Ranking<Integer> ranking) {

        List<Integer> idList = ranking.getList();
        List<University> uniList = new ArrayList<>();

        for(int i = 0; i < idList.size(); i++) {
            int id = idList.get(i);
            University uni = databaseHelper.getUniversity(id);
            uniList.add(uni);
        }

        UniversityListAdapter adapter = new UniversityListAdapter(getContext(),
                R.layout.ranking_list_cell,
                uniList);

        //rankList.setAdapter(adapter);
    }
}

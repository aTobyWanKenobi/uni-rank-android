package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.LayoutAdapters.UniversityListAdapter;
import com.example.albergon.unirank.TabbedActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreateRankingFragment extends Fragment {

    private OnCreateRankingFragmentInteractionListener defaultListener;

    private TextView indicator1 = null;
    private TextView indicator2 = null;
    private TextView indicator3 = null;
    private TextView indicator4 = null;
    private TextView indicator5 = null;

    private SeekBar seekBar1 = null;
    private SeekBar seekBar2 = null;
    private SeekBar seekBar3 = null;
    private SeekBar seekBar4 = null;
    private SeekBar seekBar5 = null;

    private Button addIndicator1Button = null;
    private Button addIndicator2Button = null;
    private Button addIndicator3Button = null;
    private Button addIndicator4Button = null;
    private Button addIndicator5Button = null;

    private Button generateButton = null;
    private ListView rankList = null;

    private SeekBar[] seekBars = null;
    private Button[] addButtons = null;

    private Aggregator aggregator = null;
    private DatabaseHelper databaseHelper = null;

    public CreateRankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CreateRankingFragment newInstance() {
        CreateRankingFragment fragment = new CreateRankingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout for this fragment
        final View view = inflater.inflate(R.layout.create_ranking_fragment, container, false);

        // create aggregator with HodgeRank algorithm and database helper to setup aggregation
        aggregator = new Aggregator(new HodgeRanking());
        databaseHelper = new DatabaseHelper(getContext());
        try {
            databaseHelper.createDatabase();
        } catch (IOException e) {

        }
        databaseHelper.openDatabase();

        createUI(view);
        onAddButtonsBehavior();

        return view;
    }

    private void createUI(View view) {

        indicator1 = (TextView) view.findViewById(R.id.indicator1_name);
        indicator2 = (TextView) view.findViewById(R.id.indicator2_name);
        indicator3 = (TextView) view.findViewById(R.id.indicator3_name);
        indicator4 = (TextView) view.findViewById(R.id.indicator4_name);
        indicator5 = (TextView) view.findViewById(R.id.indicator5_name);

        seekBar1 = (SeekBar) view.findViewById(R.id.indicator1);
        seekBar2 = (SeekBar) view.findViewById(R.id.indicator2);
        seekBar3 = (SeekBar) view.findViewById(R.id.indicator3);
        seekBar4 = (SeekBar) view.findViewById(R.id.indicator4);
        seekBar5 = (SeekBar) view.findViewById(R.id.indicator5);

        SeekBar[] seekBarsC = {seekBar1, seekBar2, seekBar3, seekBar4, seekBar5};
        seekBars = Arrays.copyOf(seekBarsC, seekBarsC.length);

        addIndicator1Button = (Button) view.findViewById(R.id.i1add);
        addIndicator2Button = (Button) view.findViewById(R.id.i2add);
        addIndicator3Button = (Button) view.findViewById(R.id.i3add);
        addIndicator4Button = (Button) view.findViewById(R.id.i4add);
        addIndicator5Button = (Button) view.findViewById(R.id.i5add);

        Button[] addButtonsC = {addIndicator1Button, addIndicator2Button, addIndicator3Button,
                        addIndicator4Button, addIndicator5Button};
        addButtons = Arrays.copyOf(addButtonsC, addButtonsC.length);

        generateButton = (Button) view.findViewById(R.id.generate_button);
        rankList = (ListView) view.findViewById(R.id.generated_ranking);

        indicator1.setText(Tables.IndicatorsList.values()[0].TABLE_NAME);
        indicator2.setText(Tables.IndicatorsList.values()[1].TABLE_NAME);
        indicator3.setText(Tables.IndicatorsList.values()[2].TABLE_NAME);
        indicator4.setText(Tables.IndicatorsList.values()[3].TABLE_NAME);
        indicator5.setText(Tables.IndicatorsList.values()[4].TABLE_NAME);
    }

    private void onAddButtonsBehavior() {

        for(int i = 0; i < addButtons.length; i++) {
            addButtons[i].setOnClickListener(createButtonListener(i));
        }

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i < addButtons.length; i++) {
                    addButtons[i].setEnabled(false);
                    addButtons[i].setVisibility(View.GONE);
                    seekBars[i].setEnabled(false);
                    seekBars[i].setVisibility(View.GONE);
                    rankList.setVisibility(View.VISIBLE);
                    generateButton.setText("Restart");
                    generateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            restartFragment();
                        }
                    });
                }

                Ranking<Integer> aggregatedRank = aggregator.aggregate();
                displayRanking(aggregatedRank);
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

                // Retrieve indicator from database and add it to aggregator with correct weight
                Indicator indicator = databaseHelper.getIndicator(index);
                int weight = seekBars[index].getProgress() + 1;
                aggregator.add(indicator, weight);

                // Disable button and seekbar
                seekBars[index].setEnabled(false);
                addButtons[index].setEnabled(false);
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
                R.layout.ranking_list_cell_layout,
                uniList);

        rankList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateRankingFragmentInteractionListener) {
            defaultListener = (OnCreateRankingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        defaultListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCreateRankingFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

package com.example.albergon.unirank.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.albergon.unirank.R;

public class ResultAggregationFragment extends Fragment {


    public ResultAggregationFragment() {
        // Required empty public constructor
    }

    public static ResultAggregationFragment newInstance() {
        ResultAggregationFragment fragment = new ResultAggregationFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.result_ranking_fragment, container, false);

        return view;
    }

}

package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.albergon.unirank.LayoutAdapters.CurationGridAdapter;
import com.example.albergon.unirank.R;


public class CurationFragment extends Fragment {

    private OnCurationFragmentInteractionListener interactionListener;

    // layout elements
    private GridView curationGrid = null;

    public static CurationFragment newInstance() {
        CurationFragment fragment = new CurationFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_curation, container, false);

        curationGrid = (GridView) view.findViewById(R.id.curation_grid);
        curationGrid.setAdapter(new CurationGridAdapter(getContext(), interactionListener));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCurationFragmentInteractionListener) {
            interactionListener = (OnCurationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCurationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface OnCurationFragmentInteractionListener {

        void onCurationClick(CurationGridAdapter.Curations curation);

    }
}

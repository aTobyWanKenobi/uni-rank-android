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

/**
 * This Fragment implements the landing screen of the application after initialization. Its main purpose
 * is to access the rank generation cycle either by starting a nre aggregation from scratch or by clicking
 * on one of the curated aggregations that this Fragment displays. Their computation has been performed
 * during app launch.
 */
public class CurationFragment extends Fragment {

    // interaction listener
    private OnCurationFragmentInteractionListener interactionListener;

    /**
     * Factory method
     *
     * @return  a CurationFragment instance
     */
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

        // create layout elements
        GridView curationGrid = (GridView) view.findViewById(R.id.curation_grid);
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

    /**
     * Interaction listener for implementing activity.
     */
    public interface OnCurationFragmentInteractionListener {

        void onCurationClick(CurationGridAdapter.Curations curation);

    }
}

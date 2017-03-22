package com.example.albergon.unirank;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;


public class CreateRankingFragment extends Fragment {

    private OnCreateRankingFragmentInteractionListener defaultListener;

    private TextView indicator1 = null;
    private TextView indicator2 = null;
    private TextView indicator3 = null;
    private TextView indicator4 = null;
    private TextView indicator5 = null;

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

        createUI(view);

        return view;
    }

    public void createUI(View view) {

        indicator1 = (TextView) view.findViewById(R.id.indicator1_name);
        indicator2 = (TextView) view.findViewById(R.id.indicator2_name);
        indicator3 = (TextView) view.findViewById(R.id.indicator3_name);
        indicator4 = (TextView) view.findViewById(R.id.indicator4_name);
        indicator5 = (TextView) view.findViewById(R.id.indicator5_name);

        indicator1.setText(Tables.IndicatorsList.values()[0].TABLE_NAME);
        indicator2.setText(Tables.IndicatorsList.values()[1].TABLE_NAME);
        indicator3.setText(Tables.IndicatorsList.values()[2].TABLE_NAME);
        indicator4.setText(Tables.IndicatorsList.values()[3].TABLE_NAME);
        indicator5.setText(Tables.IndicatorsList.values()[4].TABLE_NAME);
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

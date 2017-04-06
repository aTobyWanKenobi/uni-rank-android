package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.LayoutAdapters.PickListAdapter;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;


public class ChooseIndicatorsDialog extends DialogFragment {

    private static final String ALREADY_PICKED = "already_picked";
    private ChooseIndicatorDialogInteractionListener interactionListener;

    private List<Integer> alreadyPicked = null;
    private AlertDialog dialog = null;

    // UI elements
    ListView pickingList = null;
    Button addBtn = null;
    Button cancelBtn = null;

    public static ChooseIndicatorsDialog newInstance(ArrayList<Integer> alreadyPicked) {
        ChooseIndicatorsDialog fragment = new ChooseIndicatorsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ALREADY_PICKED, alreadyPicked);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_choose_indicators_dialog, null);

        // UI setup
        setupUI(view);

        if (getArguments() != null) {
            //TODO: set already picked
        }

        builder.setView(view);
        dialog = builder.create();

        return dialog;
    }

    private void setupUI(View view) {
        pickingList = (ListView) view.findViewById(R.id.indicator_picking_list);
        addBtn = (Button) view.findViewById(R.id.add_indicator_dialog_btn);
        cancelBtn = (Button) view.findViewById(R.id.cancel_indicator_dialog_btn);

        List<Integer> indicatorsIdList = new ArrayList<>();
        for(int i = 0; i < Tables.IndicatorsList.values().length; i++) {
            indicatorsIdList.add(i);
        }

        PickListAdapter adapter = new PickListAdapter(getContext(),
                R.layout.pick_indicators_list_cell,
                indicatorsIdList);

        pickingList.setAdapter(adapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChooseIndicatorDialogInteractionListener) {
            interactionListener = (ChooseIndicatorDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChooseIndicatorDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public interface ChooseIndicatorDialogInteractionListener {

    }
}

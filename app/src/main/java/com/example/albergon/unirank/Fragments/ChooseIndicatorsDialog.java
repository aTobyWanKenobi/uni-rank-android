package com.example.albergon.unirank.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.LayoutAdapters.PickListAdapter;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChooseIndicatorsDialog extends DialogFragment {

    private static final String ALREADY_PICKED = "already_picked";
    private ChooseIndicatorDialogInteractionListener interactionListener;

    private Set<Integer> alreadyPicked = null;

    // UI elements
    private ListView pickingList = null;
    private Button addBtn = null;
    private Button cancelBtn = null;

    public static ChooseIndicatorsDialog newInstance(ArrayList<Integer> alreadyPickedArg) {
        ChooseIndicatorsDialog fragment = new ChooseIndicatorsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ALREADY_PICKED, alreadyPickedArg);
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

        builder.setCancelable(false);
        builder.setView(view);

        if (getArguments() == null) {
            alreadyPicked = new HashSet<>();
        } else {
            alreadyPicked = new HashSet<>((ArrayList<Integer>) getArguments().getSerializable(ALREADY_PICKED));
        }

        // UI setup
        setupUI(view);

        final AlertDialog dialog = builder.create();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.addIndicators(selectedSet());
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        return dialog;
    }

    private void setupUI(View view) {
        pickingList = (ListView) view.findViewById(R.id.indicator_picking_list);
        addBtn = (Button) view.findViewById(R.id.add_indicator_dialog_btn);
        cancelBtn = (Button) view.findViewById(R.id.cancel_indicator_dialog_btn);

        List<PickListAdapter.CheckBoxTuple> indicatorsIdList = new ArrayList<>();
        for(int i = 0; i < Tables.IndicatorsList.values().length; i++) {
            boolean picked = alreadyPicked.contains(i);
            indicatorsIdList.add(new PickListAdapter.CheckBoxTuple(i, picked));
        }

        PickListAdapter adapter = new PickListAdapter(getContext(),
                R.layout.pick_indicators_list_cell,
                indicatorsIdList);

        pickingList.setAdapter(adapter);
    }

    private Set<Integer> selectedSet() {

        Set<Integer> selected = new HashSet<>();
        CheckBox box;
        PickListAdapter.PickHolder row;
        for(int i = 0; i < pickingList.getChildCount(); i++) {
            row = (PickListAdapter.PickHolder) pickingList.getChildAt(i).getTag();
            box = row.getCheckBox();
            if(box.isChecked()) {
                selected.add((int) pickingList.getItemIdAtPosition(i));
            }
        }

        return selected;
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

        void addIndicators(Set<Integer> pickedOnes);
    }
}

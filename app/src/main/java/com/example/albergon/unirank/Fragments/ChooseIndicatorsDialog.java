package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

/**
 * This Dialog fragment class defines the behavior of the AlertDialog used to choose indicators to
 * include in an aggregation during the generation process. It shows a checkable list of indicators
 * and updates the underlying fragment UI once closed.
 */
public class ChooseIndicatorsDialog extends DialogFragment {

    // argument for factory method and interaction listener
    private static final String ALREADY_PICKED = "already_picked";
    private ChooseIndicatorDialogInteractionListener interactionListener;

    private Set<Integer> alreadyPicked = null;

    // UI elements
    private ListView pickingList = null;
    private Button addBtn = null;
    private Button cancelBtn = null;

    /**
     * Static factory method that allows to instantiate the Dialog with a set of already chosen
     * indicators. The result will be a partially checked indicator list which allows to update an
     * existing setup.
     *
     * @param alreadyPickedArg      list of already selected indicators
     * @return                      a new instance of the Dialog
     */
    public static ChooseIndicatorsDialog newInstance(ArrayList<Integer> alreadyPickedArg) {

        // arguments check
        if(alreadyPickedArg == null) {
            throw new IllegalArgumentException("Cannot use the factory method of ChooseIndicatorDialog" +
                    "with a null list");
        }

        ChooseIndicatorsDialog fragment = new ChooseIndicatorsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ALREADY_PICKED, alreadyPickedArg);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_choose_indicators_dialog, null);

        // Get optional arguments if the factory method was used
        if (getArguments() == null) {
            alreadyPicked = new HashSet<>();
        } else {
            //noinspection unchecked
            alreadyPicked = new HashSet<>((ArrayList<Integer>) getArguments().getSerializable(ALREADY_PICKED));
        }

        // UI setup
        setupUI(view);

        // Setup the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // Add positive and negative button behavior
        addBtn.setOnClickListener(v -> {
            interactionListener.addIndicators(selectedSet());
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    /**
     * Private method that initializes UI elements, in particular the list view containing all
     * possible indicators.
     *
     * @param view  root view
     */
    private void setupUI(View view) {

        // Initialize elements
        pickingList = (ListView) view.findViewById(R.id.indicator_picking_list);
        addBtn = (Button) view.findViewById(R.id.add_indicator_dialog_btn);
        cancelBtn = (Button) view.findViewById(R.id.cancel_indicator_dialog_btn);

        // Fill indicator ListView
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

    /**
     * This method collects the ids of the selected indicators in the dialog.
     *
     * @return  a set containing the unique ids of selected indicators
     */
    private Set<Integer> selectedSet() {

        Set<Integer> selected = new HashSet<>();
        CheckBox box;
        PickListAdapter.PickHolder row;

        // Iterate through list items
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

    /**
     * Activity interaction interface.
     */
    public interface ChooseIndicatorDialogInteractionListener {

        void addIndicators(Set<Integer> pickedOnes);
    }
}

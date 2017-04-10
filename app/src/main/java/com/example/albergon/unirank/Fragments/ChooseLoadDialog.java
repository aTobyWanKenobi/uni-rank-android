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
import android.widget.ListView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.LayoutAdapters.LoadListAdapter;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.util.List;

/**
 * This Dialog fragment defines the behavior of the AlertDialog used to display the list of saves
 * which we can load into the rank generation fragment.
 */
public class ChooseLoadDialog extends DialogFragment {

    // Interaction listener
    private OnChooseLoadDialogInteractionListener interactionListener = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_choose_load_dialog, null);

        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // Initialize UI elements
        ListView loadPickList = (ListView) view.findViewById(R.id.choose_load_list);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_load_dialog);

        // Get database and fetch all save names
        DatabaseHelper databaseHelper = ((TabbedActivity) getActivity()).getDatabase();
        List<String> saveList = databaseHelper.fetchAllSavesName();

        // Set ListView adapter
        LoadListAdapter adapter = new LoadListAdapter(getContext(),
                R.layout.pick_load_list_cell,
                saveList,
                v -> {
                    // Open save when its row is clicked
                    LoadListAdapter.ChooseSaveHolder row = (LoadListAdapter.ChooseSaveHolder) v.getTag();
                    interactionListener.openSaveFromLoadDialog(row.getName().getText().toString());
                    dialog.dismiss();
             });
        loadPickList.setAdapter(adapter);

        // Button behavior
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseLoadDialogInteractionListener) {
            interactionListener = (OnChooseLoadDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChooseLoadDialogInteractionListener");
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
    public interface OnChooseLoadDialogInteractionListener {
        void openSaveFromLoadDialog(String name);
    }
}

package com.example.albergon.unirank.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.LayoutAdapters.LoadListAdapter;
import com.example.albergon.unirank.LayoutAdapters.PickListAdapter;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChooseLoadDialog extends DialogFragment {

    private OnChooseLoadDialogInteractionListener interactionListener = null;

    private ListView loadPickList = null;
    private Button cancelBtn = null;
    private DatabaseHelper databaseHelper = null;

    public ChooseLoadDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_choose_load_dialog, null);

        builder.setCancelable(false);
        builder.setView(view);

        databaseHelper = ((TabbedActivity) getActivity()).getDatabase();

        final AlertDialog dialog = builder.create();

        loadPickList = (ListView) view.findViewById(R.id.choose_load_list);
        cancelBtn = (Button) view.findViewById(R.id.cancel_load_dialog);

        List<String> saveList = databaseHelper.fetchAllSavesName();

        LoadListAdapter adapter = new LoadListAdapter(getContext(),
                R.layout.pick_load_list_cell,
                saveList,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadListAdapter.ChooseSaveHolder row = (LoadListAdapter.ChooseSaveHolder) v.getTag();
                        interactionListener.openSaveFromLoadDialog(row.getName().getText().toString());
                        dialog.dismiss();
                 }
                });

        loadPickList.setAdapter(adapter);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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

    public interface OnChooseLoadDialogInteractionListener {
        void openSaveFromLoadDialog(String name);
    }
}

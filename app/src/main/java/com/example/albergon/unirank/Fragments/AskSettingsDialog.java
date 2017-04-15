package com.example.albergon.unirank.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This fragment defines a dialog which purpose is to ask to the user to input some meaningful information
 * which will be used when he shares rankings with other users to create useful statistics
 */
public class AskSettingsDialog extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_ask_settings_dialog, null);

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
}

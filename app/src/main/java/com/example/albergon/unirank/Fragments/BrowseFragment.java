package com.example.albergon.unirank.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.R;
import com.example.albergon.unirank.TabbedActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseFragment extends Fragment {

    private OnBrowseFragmentInteractionListener interactionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);


        Map<Integer, Integer> settings = new HashMap<>();
        settings.put(1,1);
        settings.put(2,2);

        List<Integer> rank = new ArrayList<>();
        rank.add(1);
        rank.add(3);
        rank.add(2);

        final ShareRank demoShare = new ShareRank("demoName", "demoDate", rank, settings);
        final DatabaseReference firebase = ((TabbedActivity) getActivity()).getFirebaseInstance();

        final TextView demoTxt = (TextView) view.findViewById(R.id.demo_txt);

        Button demoBtn = (Button) view.findViewById(R.id.demo_btn);
        demoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebase.child("shared").child("test").setValue(demoShare);
                firebase.child("shared").child("test").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        demoTxt.setText("Updated database");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        demoTxt.setText("Failed");
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBrowseFragmentInteractionListener) {
            interactionListener = (OnBrowseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBrowseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * Interaction listener for implementing activity
     */
    public interface OnBrowseFragmentInteractionListener {

    }
}

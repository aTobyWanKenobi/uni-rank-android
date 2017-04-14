package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This ListView adapter implementation defines the behavior of a ListView containing universities
 * and their rank. It's used in the ResultAggregationFragment.
 */
public class UniversityListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<University> universities = null;

    public UniversityListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<University> universities) {
        //noinspection unchecked
        super(context, resource, universities);

        this.context = context;
        this.layoutResourceId = resource;
        this.universities = new ArrayList<>(universities);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        UniHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UniHolder((TextView)row.findViewById(R.id.rank),
                                    (TextView)row.findViewById(R.id.uni_name));
            row.setTag(holder);
        }
        else
        {
            holder = (UniHolder) row.getTag();
        }

        University uni = universities.get(position);
        holder.getRank().setText(String.valueOf(position+1));
        holder.getName().setText(uni.getName());

        return row;
    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    private static class UniHolder {

        private TextView rank = null;
        private TextView name = null;

        public UniHolder(TextView rank, TextView name) {

            // arguments check
            if(rank == null || name == null) {
                throw new IllegalArgumentException("UniHolder parameters cannot be null");
            }

            this.rank = rank;
            this.name = name;
        }

        public TextView getRank() {
            return rank;
        }

        public TextView getName() {
            return name;
        }
    }
}

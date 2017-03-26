package com.example.albergon.unirank;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobia Albergoni on 25.03.2017.
 */
public class UniversityListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<University> universities = null;

    public UniversityListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<University> universities) {
        super(context, resource, universities);

        this.context = context;
        this.layoutResourceId = resource;
        this.universities = new ArrayList<>(universities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UniHolder holder = null;

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

    private static class UniHolder {

        private TextView rank = null;
        private TextView name = null;

        public UniHolder(TextView rank, TextView name) {
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

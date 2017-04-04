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

import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobia Albergoni on 04.04.2017.
 */

public class SavesListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<SaveRank> savings = null;

    public SavesListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<SaveRank> savings) {
        super(context, resource, savings);

        this.context = context;
        this.layoutResourceId = resource;
        this.savings = new ArrayList<>(savings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SavesListAdapter.SavesHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SavesListAdapter.SavesHolder((TextView)row.findViewById(R.id.save_name),
                    (TextView)row.findViewById(R.id.save_date));
            row.setTag(holder);
        }
        else
        {
            holder = (SavesListAdapter.SavesHolder) row.getTag();
        }

        SaveRank save = savings.get(position);
        holder.getName().setText(save.getName());
        holder.getDate().setText(save.getDate());

        return row;
    }

    private static class SavesHolder {

        private TextView name = null;
        private TextView date = null;

        public SavesHolder(TextView name, TextView date) {
            this.name = name;
            this.date = date;
        }

        public TextView getDate() {
            return date;
        }

        public TextView getName() {
            return name;
        }
    }
}

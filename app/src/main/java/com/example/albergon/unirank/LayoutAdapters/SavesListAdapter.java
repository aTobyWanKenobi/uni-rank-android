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
    private List<String> savings = null;
    private View.OnClickListener rowListener = null;

    public SavesListAdapter(@NonNull Context context,
                            @LayoutRes int resource,
                            @NonNull List<String> savings,
                            View.OnClickListener rowListener) {
        super(context, resource, savings);

        this.context = context;
        this.layoutResourceId = resource;
        this.savings = new ArrayList<>(savings);
        this.rowListener = rowListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SaveHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SaveHolder((TextView)row.findViewById(R.id.save_name));
            row.setTag(holder);
        }
        else
        {
            holder = (SaveHolder) row.getTag();
        }

        holder.getName().setText(savings.get(position));

        row.setOnClickListener(rowListener);

        return row;
    }

    public static class SaveHolder {

        private TextView name = null;

        public SaveHolder(TextView name) {
            this.name = name;
        }

        public TextView getName() {
            return name;
        }

    }
}

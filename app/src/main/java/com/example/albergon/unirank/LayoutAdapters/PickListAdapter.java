package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobia Albergoni on 06.04.2017.
 */

public class PickListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<Integer> indicators = null;

    public PickListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Integer> indicators) {
        super(context, resource, indicators);

        this.context = context;
        this.layoutResourceId = resource;
        this.indicators = new ArrayList<>(indicators);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PickListAdapter.PickHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PickListAdapter.PickHolder((TextView)row.findViewById(R.id.indicator_pick_name),
                    (CheckBox)row.findViewById(R.id.indicator_checkbox));
            row.setTag(holder);
        }
        else
        {
            holder = (PickListAdapter.PickHolder) row.getTag();
        }

        holder.getName().setText(Tables.IndicatorsList.values()[indicators.get(position)].NAME);

        return row;
    }

    private static class PickHolder {

        private TextView name = null;
        private CheckBox checkBox = null;

        public PickHolder(TextView name, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.name = name;
        }

        public TextView getCheckBox() {
            return checkBox;
        }

        public TextView getName() {
            return name;
        }
    }
}

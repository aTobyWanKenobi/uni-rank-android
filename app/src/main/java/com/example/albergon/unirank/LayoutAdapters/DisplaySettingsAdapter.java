package com.example.albergon.unirank.LayoutAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.R;

import java.util.HashMap;
import java.util.Map;

/**
 * This list view adapter serves to display a recap of the settings in a particular aggregation
 */
public class DisplaySettingsAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private Map<Integer, Integer> settings = null;

    @SuppressLint("UseSparseArrays")
    public DisplaySettingsAdapter(@NonNull Context context, @LayoutRes int resource,
                                  @NonNull Map<Integer, Integer> settings) {

        this.context = context;
        this.layoutResourceId = resource;
        this.settings = new HashMap<>(settings);
    }

    @Override
    public int getCount() {
        return Tables.IndicatorsList.values().length;
    }

    @Override
    public Object getItem(int position) {
        return Tables.IndicatorsList.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        SettingsHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SettingsHolder( (TextView)row.findViewById(R.id.indicator_name_compare),
                    (ProgressBar) row.findViewById(R.id.indicator_weight_compare));
            row.setTag(holder);
        }
        else
        {
            holder = (SettingsHolder) row.getTag();
        }

        holder.getIndicatorName().setText(Tables.IndicatorsList.values()[position].NAME);
        holder.getWeightBar().setProgress(settings.getOrDefault(position, 0));

        return row;
    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    private static class SettingsHolder {

        private TextView indicatorName = null;
        private ProgressBar weightBar = null;

        public SettingsHolder(TextView indicatorName, ProgressBar weightBar) {

            // arguments check
            if(indicatorName == null || weightBar == null) {
                throw new IllegalArgumentException("SettingsHolder parameters cannot be null");
            }

            this.indicatorName = indicatorName;
            this.weightBar = weightBar;
        }

        public TextView getIndicatorName() {
            return indicatorName;
        }

        public ProgressBar getWeightBar() {
            return weightBar;
        }

    }
}

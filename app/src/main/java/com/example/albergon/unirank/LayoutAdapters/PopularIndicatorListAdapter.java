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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopularIndicatorListAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private Map<Integer, Integer> indicators = null;
    private List<Integer> sortedIndicators = null;
    private int maxWeight = 0;


    public PopularIndicatorListAdapter(@NonNull Context context, @LayoutRes int resource, Map<Integer, Integer> indicators) {
        this.indicators = new HashMap<>(indicators);
        this.sortedIndicators = sortIndicators(indicators);
        this.context = context;
        this.layoutResourceId = resource;
        maxWeight = Collections.max(indicators.values());
    }

    private List<Integer> sortIndicators(Map<Integer, Integer> toSort) {

        List<Integer> indicatorsList = new ArrayList<>(toSort.keySet());
        indicatorsList.sort(new PopularIndicatorsComparator(toSort));
        return indicatorsList;
    }

    @Override
    public int getCount() {
        return sortedIndicators.size();
    }

    @Override
    public Object getItem(int position) {
        return sortedIndicators.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PopularIndicatorHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PopularIndicatorHolder((TextView)row.findViewById(R.id.pop_indicator_name_txt),
                    (ProgressBar) row.findViewById(R.id.indicator_cumulative_weight));
            row.setTag(holder);
        }
        else
        {
            holder = (PopularIndicatorHolder) row.getTag();
        }

        int indicator = sortedIndicators.get(position);
        int progress = (int)((double)100*indicators.get(indicator)/maxWeight);
        holder.getName().setText(Tables.IndicatorsList.values()[indicator].NAME);
        holder.getProgressBar().setProgress(progress);

        return row;
    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    private static class PopularIndicatorHolder {

        private TextView name = null;
        private ProgressBar progressBar = null;

        public PopularIndicatorHolder(TextView name, ProgressBar progressBar) {
            this.name = name;
            this.progressBar = progressBar;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public TextView getName() {
            return name;
        }
    }

    /**
     * Nested static class that implements a comparator whose specific task is to order indicators
     * based on a weight associated to them.
     */
    public static class PopularIndicatorsComparator implements Comparator<Integer> {

        private Map<Integer, Integer> indicatorWeights = null;

        /**
         * Public constructor, takes the indicators ids and the weights associated to them.
         * This class assumes that the compare method will be called for lists whose entries appear
         * in the map stored in the object.
         *
         * @param indicatorWeights     a map containing the weight of the indicators
         */
        @SuppressLint("UseSparseArrays")
        public PopularIndicatorsComparator(Map<Integer, Integer> indicatorWeights) {

            // arguments check
            if(indicatorWeights == null) {
                throw new IllegalArgumentException("Weight map for comparator cannot be null");
            }

            this.indicatorWeights = new HashMap<>(indicatorWeights);
        }

        @Override
        public int compare(Integer o1, Integer o2) {

            // order is higher score down to lowest

            if(indicatorWeights.get(o1) < indicatorWeights.get(o2)) {
                return 1;
            } else if(indicatorWeights.get(o1).equals(indicatorWeights.get(o2))) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
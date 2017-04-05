package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.albergon.unirank.AsyncIndicatorListAdd;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 05.04.2017.
 */

public class IndicatorListAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<AsyncIndicatorListAdd.AsyncTuple> indicators = null;

    public IndicatorListAdapter(@NonNull Context context, @LayoutRes int resource) {

        this.indicators = new ArrayList<>();
        this.context = context;
        this.layoutResourceId = resource;
    }

    public void addIndicator(AsyncIndicatorListAdd.AsyncTuple tuple) {
        indicators.add(tuple);
    }

    @Override
    public int getCount() {
        return indicators.size();
    }

    @Override
    public Object getItem(int position) {
        return indicators.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        IndicatorListAdapter.IndicatorHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new IndicatorListAdapter.IndicatorHolder((TextView)row.findViewById(R.id.indicator_name_txt),
                    (SeekBar) row.findViewById(R.id.indicator_bar));
            row.setTag(holder);
        }
        else
        {
            holder = (IndicatorListAdapter.IndicatorHolder) row.getTag();
        }

        AsyncIndicatorListAdd.AsyncTuple tuple = indicators.get(position);
        holder.getName().setText(Tables.IndicatorsList.values()[tuple.getIndicator().getId()].NAME);
        holder.getSeekBar().setOnSeekBarChangeListener(tuple.getListener());

        return row;
    }

    private static class IndicatorHolder {

        private TextView name = null;
        private SeekBar seekBar = null;

        public IndicatorHolder(TextView name, SeekBar seekBar) {
            this.name = name;
            this.seekBar = seekBar;
        }

        public SeekBar getSeekBar() {
            return seekBar;
        }

        public TextView getName() {
            return name;
        }
    }
}

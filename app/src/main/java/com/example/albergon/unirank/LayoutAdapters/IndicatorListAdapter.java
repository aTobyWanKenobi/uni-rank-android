package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.albergon.unirank.AsyncIndicatorListAdd;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This ListView adapter implementation defines the behavior of a ListView containing indicators
 * names and SeekBars. It's used in the CreateRankingFragment.
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

    /**
     * Add an indicator item in the list and its listener.
     *
     * @param tuple     indicator and corresponding SeekBAr listener
     */
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
        IndicatorListAdapter.IndicatorHolder holder;

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
        holder.getName().setText(Tables.IndicatorsList.values()[tuple.getIndicator()].NAME);
        holder.getSeekBar().setOnSeekBarChangeListener(tuple.getListener());

        return row;
    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
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

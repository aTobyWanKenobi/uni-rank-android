package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This list adapter models the popular indicators filters list
 */
public class FilterListAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<ShareRankFilter> filters = null;

    public FilterListAdapter(@NonNull Context context, @LayoutRes int resource) {
        this.context = context;
        this.layoutResourceId = resource;
        filters = new ArrayList<>();
    }

    public void addFilter(ShareRankFilter filter) {
        filters.add(filter);
    }

    @Override
    public int getCount() {
        return filters.size();
    }

    @Override
    public Object getItem(int position) {
        return filters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<ShareRankFilter> getCurrentFilters() {
        return new ArrayList<>(filters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        FilterHolder holder;
        ShareRankFilter currentFilter = filters.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FilterHolder(  (TextView)row.findViewById(R.id.category_text),
                                        (TextView)row.findViewById(R.id.parameter_text));
            row.setTag(holder);
        }
        else
        {
            holder = (FilterHolder) row.getTag();
        }

        String category = currentFilter.getCategoryString();
        String parameter = currentFilter.getParameterString();
        holder.getCategory().setText(category);
        holder.getParameter().setText(parameter);

        return row;
    }

    private static class FilterHolder {

        private TextView category = null;
        private TextView parameter = null;

        public FilterHolder(TextView category, TextView parameter) {

            // arguments check
            if(category == null || parameter == null) {
                throw new IllegalArgumentException("FilterHolder parameters cannot be null");
            }

            this.category = category;
            this.parameter = parameter;
        }

        public TextView getCategory() {
            return category;
        }

        public TextView getParameter() {
            return parameter;
        }
    }



}

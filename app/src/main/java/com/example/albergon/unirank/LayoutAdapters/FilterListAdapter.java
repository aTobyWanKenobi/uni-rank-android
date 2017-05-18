package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This list adapter models the popular indicators filters list
 */
public class FilterListAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<FilterListCellContent> filters = null;

    public FilterListAdapter(@NonNull Context context, @LayoutRes int resource) {
        this.context = context;
        this.layoutResourceId = resource;
        filters = new ArrayList<>();
    }

    public void addFilter(FilterListCellContent filterCellContent) {
        filters.add(filterCellContent);
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

    public List<FilterListCellContent> getCurrentFilters() {
        return new ArrayList<>(filters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FilterListCellContent filterCell = filters.get(position);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_pop_indicator_filter, null);
        }

        Spinner categorySpinner = (Spinner) convertView.findViewById(R.id.filter_type_spinner);
        Spinner parameterSpinner = (Spinner) convertView.findViewById(R.id.filter_parameter_spinner);

        filterCell.setCategorySpinner(categorySpinner);
        filterCell.setParameterSpinner(parameterSpinner);

        return convertView;
    }
}

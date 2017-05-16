package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.example.albergon.unirank.AsyncFilterListAdd;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This list adapter models the popular indicators filters list
 */
public class FilterListAdapter extends BaseAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<AsyncFilterListAdd.FilterCellContent> filters = null;

    public FilterListAdapter(@NonNull Context context, @LayoutRes int resource) {
        this.context = context;
        this.layoutResourceId = resource;
        filters = new ArrayList<>();
    }

    public void addFilter(AsyncFilterListAdd.FilterCellContent filterCellContent) {
        filters.add(filterCellContent);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void sdjhfjasd() {
        // disable parameter spinner until category is chosen and set temporary adapter
        parameterSpinner.setEnabled(false);
        List<String> noParametersList = new ArrayList<>();
        noParametersList.add("not selected");
        ArrayAdapter<String> parameterAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                noParametersList);
        parameterSpinner.setAdapter(parameterAdapter);

        // set category spinner adapter
        List<String> categoriesList = new ArrayList<>();
        for(Enums.PopularIndicatorsCategories category : Enums.PopularIndicatorsCategories.values()) {
            categoriesList.add(category.toString());
        }

        // add no selection item
        categoriesList.add(0, "not selected");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.cell_simple_dropdown_text,
                categoriesList);
        categorySpinner.setAdapter(categoryAdapter);

        // set category spinner listener
        categorySpinner.setOnItemSelectedListener(createCategorySpinnerListener());
    }
}

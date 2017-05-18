package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter serves to display curated rankings to the curation fragment
 */
public class CurationGridAdapter extends BaseAdapter {

    private Context context = null;
    private List<CurationCellContent> curations = null;

    public CurationGridAdapter(Context context) {

        this.context = context;
        curations = initializeCurations();
    }

    private List<CurationCellContent> initializeCurations() {

        List<CurationCellContent> defaultCurations = new ArrayList<>();
        defaultCurations.add(new CurationCellContent(Curations.BEST_COUNTRY, context));
        defaultCurations.add(new CurationCellContent(Curations.TYPE_AND_AGE, context));
        defaultCurations.add(new CurationCellContent(Curations.LAST_MONTH, context));
        defaultCurations.add(new CurationCellContent(Curations.BEST_OVERALL, context));
        defaultCurations.add(new CurationCellContent(Curations.EMPTY, context));

        return defaultCurations;
    }

    @Override
    public int getCount() {
        return curations.size();
    }

    @Override
    public Object getItem(int position) {
        return curations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CurationCellContent curationContent = curations.get(position);

        if(convertView == null) {
            convertView = curationContent.getLayout();
        }

        return convertView;
    }


    public static enum Curations {

        BEST_COUNTRY,
        TYPE_AND_AGE,
        LAST_MONTH,
        BEST_OVERALL,
        EMPTY;
    }
}

package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.albergon.unirank.Fragments.CurationFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter serves to display curated rankings in the curation fragment
 */
public class CurationGridAdapter extends BaseAdapter {

    private Context context = null;
    private CurationFragment.OnCurationFragmentInteractionListener interactionListener = null;
    private List<CurationCellContent> curations = null;

    /**
     * Public constructor that simply encapsulates parameters
     *
     * @param context                   activity context
     * @param interactionListener       interaction listener for when the grid element will be pressed
     */
    public CurationGridAdapter(Context context, CurationFragment.OnCurationFragmentInteractionListener interactionListener) {

        this.context = context;
        this.interactionListener = interactionListener;
        curations = initializeCurations();
    }

    /**
     * Initialize all grid cells for current curated aggregations.
     *
     * @return  a list of CurationCellContent
     */
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

        final CurationCellContent curationContent = curations.get(position);

        if(convertView == null) {
            convertView = curationContent.getLayout();
        }

        convertView.setOnClickListener(v -> interactionListener.onCurationClick(curationContent.getCuration()));

        return convertView;
    }

    /**
     * Current curated aggregations in UniRank.
     */
    public enum Curations {

        BEST_COUNTRY,
        TYPE_AND_AGE,
        LAST_MONTH,
        BEST_OVERALL,
        EMPTY
    }
}

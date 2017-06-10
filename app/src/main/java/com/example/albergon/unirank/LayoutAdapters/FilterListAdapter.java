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
import android.widget.TextView;

import com.example.albergon.unirank.Fragments.OnAddFilterReturn;
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
    private List<ShareRankFilter> filters = null;
    private OnAddFilterReturn additionListener = null;

    public FilterListAdapter(@NonNull Context context, @LayoutRes int resource, OnAddFilterReturn additionListener) {
        this.context = context;
        this.layoutResourceId = resource;
        this.additionListener = additionListener;
        filters = new ArrayList<>();
    }

    public void addFilter(ShareRankFilter filter) {
        filters.add(filter);
        additionListener.updateQuery();
    }

    public void removeFilter(int position) {
        filters.remove(position);
        this.notifyDataSetChanged();
        additionListener.updateQuery();
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
                                        (TextView)row.findViewById(R.id.parameter_text),
                                        (ImageView) row.findViewById(R.id.category_icon),
                                        (ImageView) row.findViewById(R.id.remove_icon));
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

        // set appropriate icon
        switch(currentFilter.getCategory()) {

            case GENDER:
                holder.getCategoryIcon().setBackgroundResource(R.drawable.icon_gender);
                break;
            case TYPE:
                holder.getCategoryIcon().setBackgroundResource(R.drawable.icon_types);
                break;
            case BIRTHYEAR:
                holder.getCategoryIcon().setBackgroundResource(R.drawable.icon_age);
                break;
            case TIMEFRAME:
                holder.getCategoryIcon().setBackgroundResource(R.drawable.icon_curation_month);
                break;
            case COUNTRY:
                holder.getCategoryIcon().setBackgroundResource(R.drawable.icon_curation_country);
                break;
            default:
                throw new IllegalStateException("Unknown element in category enum");
        }

        // add remove icon behaviour
        holder.getRemoveIcon().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFilter(position);
            }
        });

        return row;
    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    private static class FilterHolder {

        private TextView category = null;
        private TextView parameter = null;
        private ImageView categoryIcon = null;
        private ImageView removeIcon = null;

        public FilterHolder(TextView category, TextView parameter, ImageView categoryIcon, ImageView removeIcon) {

            // arguments check
            if(category == null || parameter == null || categoryIcon == null || removeIcon == null) {
                throw new IllegalArgumentException("FilterHolder parameters cannot be null");
            }

            this.category = category;
            this.parameter = parameter;
            this.categoryIcon = categoryIcon;
            this.removeIcon = removeIcon;
        }

        public TextView getCategory() {
            return category;
        }

        public TextView getParameter() {
            return parameter;
        }

        public ImageView getCategoryIcon() {
            return categoryIcon;
        }

        public ImageView getRemoveIcon() {
            return removeIcon;
        }
    }



}

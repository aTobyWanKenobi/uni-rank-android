package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.albergon.unirank.Model.University;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This ListView adapter implementation defines the behavior of a ListView containing universities
 * and their rank. It's used in the ResultAggregationFragment.
 */
public class UniversityListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<University> universities = null;
    private List<Integer> oldResult = null;

    public UniversityListAdapter(@NonNull Context context, @LayoutRes int resource,
                                 @NonNull List<University> universities, List<Integer> oldResult) {
        //noinspection unchecked
        super(context, resource, universities);

        this.context = context;
        this.layoutResourceId = resource;
        this.universities = new ArrayList<>(universities);
        this.oldResult = (oldResult == null) ? null : new ArrayList<>(oldResult);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        UniHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UniHolder( (TextView)row.findViewById(R.id.rank),
                                    (TextView)row.findViewById(R.id.uni_name),
                                    (ImageView) row.findViewById(R.id.rank_change_icon),
                                    (TextView) row.findViewById(R.id.rank_change_number));
            row.setTag(holder);
        }
        else
        {
            holder = (UniHolder) row.getTag();
        }

        University uni = universities.get(position);
        if(oldResult != null) {
            setOldRankingComparison(holder, uni, position);
        }
        holder.getRank().setText(String.valueOf(position+1));
        holder.getName().setText(uni.getName());

        return row;
    }

    private void setOldRankingComparison(UniHolder holder, University uni, int position) {

        int change = oldResult.indexOf(uni.getId()) - position;

        holder.getChangeNumber().setText(String.valueOf(change));

        if(change > 0) {
            holder.getChangeIcon().setBackgroundResource(R.drawable.green_arrow_up);
        } else if(change < 0) {
            holder.getChangeIcon().setBackgroundResource(R.drawable.red_arrow_down);
        } else {
            holder.getChangeIcon().setBackgroundResource(R.drawable.yellow_equal_sign);
        }

    }

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    private static class UniHolder {

        private TextView rank = null;
        private TextView name = null;
        private ImageView changeIcon = null;
        private TextView changeNumber = null;
        private ProgressBar scoreBar = null;

        public UniHolder(TextView rank, TextView name, ImageView changeIcon, TextView changeNumber) {

            // arguments check
            if(rank == null || name == null || changeIcon == null || changeNumber == null) {
                throw new IllegalArgumentException("UniHolder parameters cannot be null");
            }

            this.rank = rank;
            this.name = name;
            this.changeIcon = changeIcon;
            this.changeNumber = changeNumber;
        }

        public TextView getRank() {
            return rank;
        }

        public TextView getName() {
            return name;
        }

        public ImageView getChangeIcon() {
            return changeIcon;
        }

        public TextView getChangeNumber() {
            return changeNumber;
        }
    }
}

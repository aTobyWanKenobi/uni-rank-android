package com.example.albergon.unirank.LayoutAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Fragments.MyRankingButtonHandler;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This ListView adapter implementation defines the behavior of a ListView containing the names of
 * local aggregation saves. It's used in MyRankingsFragment.
 */
public class SavesListAdapter extends BaseExpandableListAdapter {

    private Context context = null;
    private List<String> savings = null;
    private Map<String, Map<Integer, Integer>> settings;

    private MyRankingButtonHandler buttonHandler = null;

    public SavesListAdapter(@NonNull Context context,
                            @NonNull List<String> savings,
                            @NonNull Map<String, Map<Integer, Integer>> settings,
                            MyRankingButtonHandler buttonHandler) {

        // arguments check
        if(buttonHandler == null) {
            throw new IllegalArgumentException("Button handler cannot be null");
        }

        this.context = context;
        this.savings = new ArrayList<>(savings);
        this.settings = new HashMap<>(settings);
        this.buttonHandler = buttonHandler;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String saveName = savings.get(groupPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_saving_list, null);
        }

        // set save name
        TextView saveNameTxt = (TextView) convertView.findViewById(R.id.save_name);
        saveNameTxt.setText(saveName);

        ImageView openIcon = (ImageView) convertView.findViewById(R.id.open_icon_myrank);
        openIcon.setOnClickListener(v -> buttonHandler.open(saveName));

        ImageView compareIcon = (ImageView) convertView.findViewById(R.id.compare_icon_myrank);
        compareIcon.setOnClickListener(v -> buttonHandler.compare(saveName));

        ImageView shareIcon = (ImageView) convertView.findViewById(R.id.share_icon_myrank);
        shareIcon.setOnClickListener(v -> buttonHandler.share(saveName));

        ImageView deleteIcon = (ImageView) convertView.findViewById(R.id.delete_icon_myrank);
        deleteIcon.setOnClickListener(v -> buttonHandler.delete(saveName));

        //convertView.setOnClickListener(rowListener);
        convertView.setTag(saveNameTxt);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String indicatorName = Tables.IndicatorsList.values()[childPosition].toString();
        int weight = settings.get(savings.get(groupPosition)).get(childPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_settings_recap_horizontal, null);
        }

        TextView indicatorNameTxt = (TextView) convertView.findViewById(R.id.indicator_name_compare_recap);
        indicatorNameTxt.setText(indicatorName);

        ProgressBar indicatorWeightBar = (ProgressBar) convertView.findViewById(R.id.indicator_weight_compare_recap);
        indicatorWeightBar.setProgress(weight);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return savings.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return settings.get(savings.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return settings.get(savings.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((Map<Integer, Integer>) getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}

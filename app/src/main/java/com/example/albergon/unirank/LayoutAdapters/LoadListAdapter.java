package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobia Albergoni on 10.04.2017.
 */

public class LoadListAdapter extends ArrayAdapter {

    private Context context = null;
    private int layoutResourceId = 0;
    private List<String> saves = null;
    private View.OnClickListener rowListener = null;

    public LoadListAdapter(@NonNull Context context,
                           @LayoutRes int resource,
                           @NonNull List<String> saves,
                           View.OnClickListener rowListener) {
        super(context, resource, saves);

        this.context = context;
        this.layoutResourceId = resource;
        this.saves = new ArrayList<>(saves);
        this.rowListener = rowListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChooseSaveHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ChooseSaveHolder((TextView)row.findViewById(R.id.save_name_list_txt));
            row.setTag(holder);
        }
        else
        {
            holder = (ChooseSaveHolder) row.getTag();
        }

        holder.getName().setText(saves.get(position));
        row.setOnClickListener(rowListener);

        return row;
    }

    public static class ChooseSaveHolder {

        private TextView saveName = null;

        public ChooseSaveHolder(TextView nameView) {
            saveName = nameView;
        }

        public TextView getName() {
            return saveName;
        }
    }

}

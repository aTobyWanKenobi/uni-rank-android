package com.example.albergon.unirank.LayoutAdapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.albergon.unirank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This ListView adapter implementation defines the behavior of a ListView containing the names of
 * saved rankings. It's used in the ChooseLoadDialog.
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
        //noinspection unchecked
        super(context, resource, saves);

        //arguments check
        if(rowListener == null) {
            throw new IllegalArgumentException("Row listener cannot be null");
        }

        this.context = context;
        this.layoutResourceId = resource;
        this.saves = new ArrayList<>(saves);
        this.rowListener = rowListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ChooseSaveHolder holder;

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

    /**
     * Object which will be set as the row's tag and that contains the instantiated layout elements
     * of a list cell.
     */
    public static class ChooseSaveHolder {

        private TextView saveName = null;

        public ChooseSaveHolder(TextView nameView) {

            //arguments check
            if(nameView == null) {
                throw new IllegalArgumentException("TextView for ChooseSaveHolder cannot be null");
            }

            saveName = nameView;
        }

        public TextView getName() {
            return saveName;
        }
    }

}

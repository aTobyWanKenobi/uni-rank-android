package com.example.albergon.unirank;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.albergon.unirank.LayoutAdapters.FilterListAdapter;
import com.example.albergon.unirank.LayoutAdapters.IndicatorListAdapter;
import com.example.albergon.unirank.Model.Enums;

/**
 * This AsyncTask extension updates the contents of a ListView outside of the UI thread, to allow it
 * to refresh and show the results in real time.
 */
public class AsyncFilterListAdd extends AsyncTask<FilterListCellContent, FilterListCellContent, Void> {

    private FilterListAdapter arrayAdapter = null;
    private Context context = null;

    /**
     * This public constructor takes as parameters the context where the task will be launched and the
     * adapter of the ListView it has to manipulate.
     *
     * @param arrayAdapter      adapter of the ListView
     * @param context           context
     */
    public AsyncFilterListAdd(FilterListAdapter arrayAdapter, Context context) {

        // arguments check
        if(arrayAdapter == null || context == null) {
            throw new IllegalArgumentException("Arguments for AsyncTask cannot be null");
        }

        this.context = context;
        this.arrayAdapter = arrayAdapter;
    }


    @Override
    protected Void doInBackground(FilterListCellContent... params) {

        // arguments check
        if(params.length != 1) {
            throw new IllegalArgumentException("Cannot start this task with more than one item");
        }

        publishProgress(params[0]);

        return null;
    }

    @Override
    protected void onProgressUpdate(FilterListCellContent... item) {
        arrayAdapter.addFilter(item[0]);
    }

    @Override
    protected void onPostExecute(Void unused) {
        arrayAdapter.notifyDataSetChanged();
    }

}

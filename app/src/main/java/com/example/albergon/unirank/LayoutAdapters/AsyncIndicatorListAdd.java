package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.SeekBar;

/**
 * This AsyncTask extension updates the contents of a ListView outside of the UI thread, to allow it
 * to refresh and show the results in real time.
 */
public class AsyncIndicatorListAdd extends AsyncTask<AsyncIndicatorListAdd.IndicatorCellContent, AsyncIndicatorListAdd.IndicatorCellContent, Void> {

    private IndicatorListAdapter arrayAdapter = null;
    private Context context = null;

    /**
     * This public constructor takes as parameters the context where the task will be launched and the
     * adapter of the ListView it has to manipulate.
     *
     * @param arrayAdapter      adapter of the ListView
     * @param context           context
     */
    public AsyncIndicatorListAdd(IndicatorListAdapter arrayAdapter, Context context) {

        // arguments check
        if(arrayAdapter == null || context == null) {
            throw new IllegalArgumentException("Arguments for AsyncTask cannot be null");
        }

        this.context = context;
        this.arrayAdapter = arrayAdapter;

        System.out.println("DEBUG: TASK CREATED");
    }


    @Override
    protected Void doInBackground(IndicatorCellContent... params) {

        // arguments check
        if(params.length != 1) {
            throw new IllegalArgumentException("Cannot start this task with more than one item");
        }

        System.out.println(params.length);

        publishProgress(params[0]);

        return null;
    }

    @Override
    protected void onProgressUpdate(IndicatorCellContent... item) {
        arrayAdapter.addIndicator(item[0]);

        System.out.println("DEBUG: ADD : " + item[0].getIndicator() + " " + item[0].getInitialWeight());
    }

    @Override
    protected void onPostExecute(Void unused) {
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * This nested static class serves as a container for the tuple composed by an indicator and the
     * associated SeekBar listener.
     */
    public static class IndicatorCellContent {

        private Integer indicator = null;
        private int initialWeight = 1;
        private SeekBar.OnSeekBarChangeListener listener = null;

        public IndicatorCellContent(Integer indicator, SeekBar.OnSeekBarChangeListener listener, int initialWeight) {

            // arguments check
            if(indicator == null || listener == null) {
                throw new IllegalArgumentException("Parameters for IndicatorCellContent cannot be null");
            }

            this.indicator = indicator;
            this.listener = listener;
            this.initialWeight = initialWeight;
        }

        public Integer getIndicator() {
            return indicator;
        }

        public SeekBar.OnSeekBarChangeListener getListener() {
            return listener;
        }

        public int getInitialWeight() {
            return initialWeight;
        }
    }
}

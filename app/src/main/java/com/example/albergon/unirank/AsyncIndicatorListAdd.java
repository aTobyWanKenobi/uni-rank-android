package com.example.albergon.unirank;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.SeekBar;

import com.example.albergon.unirank.LayoutAdapters.IndicatorListAdapter;

/**
 * This AsyncTask extension updates the contents of a ListView outside of the UI thread, to allow it
 * to refresh and show the results in real time.
 */
public class AsyncIndicatorListAdd extends AsyncTask<AsyncIndicatorListAdd.AsyncTuple, AsyncIndicatorListAdd.AsyncTuple, Void> {

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
    }


    @Override
    protected Void doInBackground(AsyncTuple... params) {

        // arguments check
        if(params.length != 1) {
            throw new IllegalArgumentException("Cannot start this task with more than one item");
        }

        publishProgress(params[0]);

        return null;
    }

    @Override
    protected void onProgressUpdate(AsyncTuple... item) {
        arrayAdapter.addIndicator(item[0]);
    }

    @Override
    protected void onPostExecute(Void unused) {
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * This nested static class serves as a container for the tuple composed by an indicator and the
     * associated SeekBar listener.
     */
    public static class AsyncTuple {

        private Integer indicator = null;
        private SeekBar.OnSeekBarChangeListener listener = null;

        public AsyncTuple(Integer indicator, SeekBar.OnSeekBarChangeListener listener) {

            // arguments check
            if(indicator == null || listener == null) {
                throw new IllegalArgumentException("Parameters for AsyncTuple cannot be null");
            }

            this.indicator = indicator;
            this.listener = listener;
        }

        public Integer getIndicator() {
            return indicator;
        }

        public SeekBar.OnSeekBarChangeListener getListener() {
            return listener;
        }
    }
}

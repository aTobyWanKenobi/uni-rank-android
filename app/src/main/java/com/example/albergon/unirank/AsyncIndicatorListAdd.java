package com.example.albergon.unirank;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albergon.unirank.LayoutAdapters.IndicatorListAdapter;
import com.example.albergon.unirank.Model.Indicator;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by Tobia Albergoni on 05.04.2017.
 */
public class AsyncIndicatorListAdd extends AsyncTask<AsyncIndicatorListAdd.AsyncTuple, AsyncIndicatorListAdd.AsyncTuple, Void> {

    private IndicatorListAdapter arrayAdapter = null;
    private Context context = null;

    public AsyncIndicatorListAdd(IndicatorListAdapter arrayAdapter, Context context) {

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
        Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
        arrayAdapter.notifyDataSetChanged();
    }

    public static class AsyncTuple {

        private Indicator indicator = null;
        private SeekBar.OnSeekBarChangeListener listener = null;

        public AsyncTuple(Indicator indicator, SeekBar.OnSeekBarChangeListener listener) {
            this.indicator = indicator;
            this.listener = listener;
        }

        public Indicator getIndicator() {
            return indicator;
        }

        public SeekBar.OnSeekBarChangeListener getListener() {
            return listener;
        }
    }
}

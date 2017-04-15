package com.example.albergon.unirank;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Fragments.AskSettingsDialog;

public class StartActivity extends AppCompatActivity implements
        AskSettingsDialog.OnAskSettingsInteractionListener {

    private ProgressBar progressCircle = null;
    private TextView loadDatabaseTxt = null;

    //TODO: just for testing, remove
    private TextView feedback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setupUI();

        AsyncOpenDatabase openDBTask = new AsyncOpenDatabase();
        openDBTask.execute(this);
    }

    public void setupUI() {

        progressCircle = (ProgressBar) findViewById(R.id.load_app_progress);
        loadDatabaseTxt = (TextView) findViewById(R.id.load_universities_txt);

        feedback = (TextView) findViewById(R.id.opening_feedback);
    }

    public void startTabbedActivity() {
        Intent tabActivityIntent = new Intent(this, TabbedActivity.class);
        startActivity(tabActivityIntent);
    }

    public void askSettings() {
        DialogFragment dialog = new AskSettingsDialog();
        dialog.show(getSupportFragmentManager(), "AskSettingsDialog");
    }

    @Override
    public void goToApp() {
        startTabbedActivity();
    }

    private class AsyncOpenDatabase extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            progressCircle.setVisibility(View.VISIBLE);
            loadDatabaseTxt.setVisibility(View.VISIBLE);
            feedback.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Context... params) {


            //TODO: just wait a bit spinning, just because it looks nice
            long startTime = System.currentTimeMillis();

            while(System.currentTimeMillis() - startTime < 1000) {
                publishProgress(0);
            }

            boolean exists = DatabaseHelper.databaseExists();
            // first database instantiation of app
            DatabaseHelper.getInstance(params[0]);

            //TODO: just wait a bit spinning, just because it looks nice
            startTime = System.currentTimeMillis();
            while(System.currentTimeMillis() - startTime < 1000) {
                publishProgress(0);
            }

            return exists;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressCircle.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            progressCircle.setVisibility(View.INVISIBLE);
            loadDatabaseTxt.setVisibility(View.INVISIBLE);
            feedback.setVisibility(View.VISIBLE);

            if(!exists) {
                askSettings();
            } else {
                startTabbedActivity();
            }
        }
    }
}

package com.example.albergon.unirank.Model;

import android.content.Context;
import android.drm.DrmManagerClient;
import android.os.AsyncTask;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.LayoutAdapters.CurationGridAdapter;
import com.example.albergon.unirank.LayoutAdapters.FilterListCellContent;
import com.example.albergon.unirank.Model.SharedRankFilters.BirthyearFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.CountryFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.FilterManager;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.TimeframeFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.UserTypeFilter;
import com.google.firebase.database.DatabaseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class launches the asynchronous download and processing of the curated rankings and notifies
 * the relative activities and fragments
 */
public class CreationUpdater extends AsyncTask<Void, Void, Map<Integer, Integer>> {

    private Context context = null;
    private CurationGridAdapter.Curations curation = null;
    private FilterManager filterManager = null;
    private OnCurationDownloadNotifier notifier = null;
    private List<ShareRank> pool = null;
    private int[] indicatorsScores = null;

    public CreationUpdater(CurationGridAdapter.Curations curation, Context context, OnCurationDownloadNotifier notifier, List<ShareRank> pool){

        this.context = context;
        this.curation = curation;
        this.notifier = notifier;
        this.pool = pool;
        indicatorsScores = new int[Tables.IndicatorsList.values().length];

        createFilters();
    }

    private void createFilters() {

        Settings currentSettings = DatabaseHelper.getInstance(context).retriveSettings(false);

        switch(curation) {

            case BEST_COUNTRY:
                filterManager = new FilterManager(new CountryFilter(currentSettings.getCountryCode()));
                break;
            case TYPE_AND_AGE:
                ShareRankFilter typeFilter = new UserTypeFilter(currentSettings.getType());
                ShareRankFilter ageFilter = createAgeFilter(currentSettings.yearOfBirth);
                filterManager = new FilterManager(typeFilter, ageFilter);
                break;
            case LAST_MONTH:
                filterManager = new FilterManager(new TimeframeFilter(Enums.TimeFrame.MONTH));
                break;
            case BEST_OVERALL:
                filterManager = new FilterManager();
                break;
            case EMPTY:
                throw new IllegalStateException("This method should not be called with this argument");
            default:
                throw new IllegalStateException("Unknows element in enum Curations");
        }
    }

    private ShareRankFilter createAgeFilter(int birthYear) {

        int currentYear = Integer.parseInt(FirebaseHelper.generateDate().substring(FirebaseHelper.generateDate().lastIndexOf(' ') + 1));
        int age = currentYear - birthYear;
        Range yearRange;

        if(age <= 15) {
            yearRange = new Range(currentYear - 15, currentYear);
        } else if(age > 15 && age <= 20) {
            yearRange = new Range(currentYear - 20, currentYear - 15);
        } else if(age > 20 && age <= 27) {
            yearRange = new Range(currentYear - 27, currentYear - 20);
        } else if(age > 27 && age <= 50) {
            yearRange = new Range(currentYear - 50, currentYear - 27);
        } else {
            yearRange = new Range(1900, currentYear - 50);
        }
        return new BirthyearFilter(yearRange);
    }

    @Override
    protected Map<Integer, Integer> doInBackground(Void... params) {

        indicatorsScores = filterManager.filter(pool);
        Map<Integer, Integer> settings = normalizeSettings();

        return settings;
    }

    @Override
    protected void onPostExecute(Map<Integer, Integer> result) {
        notifier.onSettingsDownloadCompleted(curation, result);
    }

    private Map<Integer, Integer> normalizeSettings() {

        int max = 0;
        for(int score : indicatorsScores) {
            if(score > max) {
                max = score;
            }
        }

        Map<Integer, Integer> settings = new HashMap<>();

        for(int i = 0; i < indicatorsScores.length; i++) {
            settings.put(i, (int) (10*((double) indicatorsScores[i]/max)));
        }

        return settings;
    }


}

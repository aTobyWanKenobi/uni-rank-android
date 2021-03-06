package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.LayoutAdapters.CurationGridAdapter;
import com.example.albergon.unirank.Model.SharedRankFilters.BirthyearFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.CountryFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.FilterManager;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.TimeframeFilter;
import com.example.albergon.unirank.Model.SharedRankFilters.UserTypeFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class launches the asynchronous download and processing of the curated rankings and notifies
 * the relative activities and fragments
 */
public class CurationUpdater extends AsyncTask<Void, Void, Map<Integer, Integer>> {

    private Context context = null;
    private CurationGridAdapter.Curations curation = null;
    private OnCurationDownloadNotifier notifier = null;
    private List<ShareRank> pool = null;

    public CurationUpdater(CurationGridAdapter.Curations curation, Context context, OnCurationDownloadNotifier notifier, List<ShareRank> pool){

        this.context = context;
        this.curation = curation;
        this.notifier = notifier;
        this.pool = pool;
    }

    /**
     * Creates a Birthyear filter for the PEERS curated ranking.
     *
     * @param birthYear     user birth year
     * @return              relative filter
     */
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

        Settings currentSettings = DatabaseHelper.getInstance(context).retrieveSettings(false);
        FilterManager filterManager;

        // create correct filter
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
                throw new IllegalStateException("Unknown element in enum Curations");
        }

        return normalizeSettings(filterManager.filter(pool));
    }

    @Override
    protected void onPostExecute(Map<Integer, Integer> result) {
        notifier.onSettingsDownloadCompleted(curation, result);
    }

    /**
     * This private method normalizes popularity scores computed from the shared pool into the 1-10
     * weight range used by UniRank.
     *
     * @param indicatorsScores      indicator popularity scores
     * @return                      mapping with normalized weights
     */
    private Map<Integer, Integer> normalizeSettings(int[] indicatorsScores) {

        // find max value
        int max = 0;
        for(int score : indicatorsScores) {
            if(score > max) {
                max = score;
            }
        }

        @SuppressLint("UseSparseArrays") Map<Integer, Integer> settings = new HashMap<>();

        // normalize base don max value
        for(int i = 0; i < indicatorsScores.length; i++) {
            int weight = (int) (10*((double) indicatorsScores[i]/max));
            if(weight > 0) {
                settings.put(i, weight);
            }
        }

        return settings;
    }


}

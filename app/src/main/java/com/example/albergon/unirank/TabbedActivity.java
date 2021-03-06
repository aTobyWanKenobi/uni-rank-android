package com.example.albergon.unirank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.example.albergon.unirank.Database.CallbackHandlers.OnFirebaseErrorListener;
import com.example.albergon.unirank.Database.CallbackHandlers.OnSharedPoolRetrievalListener;
import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Fragments.AddFilterDialog;
import com.example.albergon.unirank.Fragments.AskSettingsDialog;
import com.example.albergon.unirank.Fragments.BrowseFragment;
import com.example.albergon.unirank.Fragments.ChooseIndicatorsDialog;
import com.example.albergon.unirank.Fragments.ChooseLoadDialog;
import com.example.albergon.unirank.Fragments.CompareFragment;
import com.example.albergon.unirank.Fragments.CreateRankingFragment;
import com.example.albergon.unirank.Fragments.CurationFragment;
import com.example.albergon.unirank.Fragments.MyRankingsFragment;
import com.example.albergon.unirank.Fragments.OnAskSettingsReturn;
import com.example.albergon.unirank.Fragments.ResultAggregationFragment;
import com.example.albergon.unirank.LayoutAdapters.CurationGridAdapter;
import com.example.albergon.unirank.LayoutAdapters.TabsFragmentPagerAdapter;
import com.example.albergon.unirank.Model.CurationUpdater;
import com.example.albergon.unirank.Model.OnCurationDownloadNotifier;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.ShareRank;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This activity is the main activity of the application. It implements the navigation mechanism
 * via a TabLayout. It additionally implements all the fragment interaction listeners that allow
 * inter-fragment communication.
 */
public class TabbedActivity extends AppCompatActivity implements
        CreateRankingFragment.OnRankGenerationInteractionListener,
        ResultAggregationFragment.ResultFragmentInteractionListener,
        ChooseIndicatorsDialog.ChooseIndicatorDialogInteractionListener,
        MyRankingsFragment.MyRankingsFragmentInteractionListener,
        ChooseLoadDialog.OnChooseLoadDialogInteractionListener,
        BrowseFragment.OnBrowseFragmentInteractionListener,
        CompareFragment.OnCompareFragmentInteractionListener,
        CurationFragment.OnCurationFragmentInteractionListener,
        AskSettingsDialog.OnAskSettingsInteractionListener {

    // Database instance, unique for the entire application
    private DatabaseHelper databaseHelper = null;

    // Layout elements
    private TabLayout tabLayout = null;
    private TabsFragmentPagerAdapter tabsPagerAdapter = null;
    private ViewPager viewPager = null;

    // Fragment management system
    private Fragment currentFragment = null;
    private FragmentManager fragmentManager = null;

    // start activity elements
    private ProgressBar progressCircle = null;
    private CheckBox localCheckbox = null;
    private CheckBox remoteCheckbox = null;
    private CheckBox curationCheckbox = null;
    private boolean settingsOk = true;

    // curation data
    private Map<Integer, Integer> countryCurSettings = null;
    private Map<Integer, Integer> peersCurSettings = null;
    private Map<Integer, Integer> monthCurSettings = null;
    private Map<Integer, Integer> bestCurSettings = null;

    private List<Integer> countryCurRank = null;
    private List<Integer> peersCurRank = null;
    private List<Integer> monthCurRank = null;
    private List<Integer> bestCurRank = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setupStartUI();

        AsyncOpenDatabase openDBTask = new AsyncOpenDatabase();
        openDBTask.execute(this);
    }

    /**
     * Setup splash screen and initialization layout
     */
    public void setupStartUI() {

        progressCircle = (ProgressBar) findViewById(R.id.load_app_progress);
        localCheckbox = (CheckBox) findViewById(R.id.local_checkbox);
        remoteCheckbox = (CheckBox) findViewById(R.id.remote_checkbox);
        curationCheckbox = (CheckBox) findViewById(R.id.curation_checkbox);
    }

    /**
     * If it's the first application startup, show dialog to ask user settings
     */
    public void askSettings() {
        AskSettingsDialog dialog = new AskSettingsDialog();
        dialog.addSettingsListener(new OnAskSettingsReturn() {
            @Override
            public void setSettingsOk() {
                settingsOk = true;
            }
        });
        dialog.show(getSupportFragmentManager(), "AskSettingsDialog");


    }

    /**
     * Instantiate application layout and dismantle splash screen structures.
     */
    private void createRealActivity() {
        setContentView(R.layout.activity_tabbed);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        tabsPagerAdapter = new TabsFragmentPagerAdapter(getSupportFragmentManager(), this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(createSwitcher());

        // initializing the fragment structure
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        changeFragment(new CurationFragment());

        databaseHelper = DatabaseHelper.getInstance(this);
    }

    /**
     * Create listener that notifies when curated aggregations are computed and ready to be shown to users.
     *
     * @return  a OnCurationDownloadNotifier object
     */
    private OnCurationDownloadNotifier createCurationNotifier() {

        return new OnCurationDownloadNotifier() {
            @Override
            public void onSettingsDownloadCompleted(CurationGridAdapter.Curations curation, Map<Integer, Integer> settings) {

                switch(curation) {

                    case BEST_COUNTRY:
                        countryCurSettings = settings;
                        break;
                    case TYPE_AND_AGE:
                        peersCurSettings = settings;
                        break;
                    case LAST_MONTH:
                        monthCurSettings = settings;
                        break;
                    case BEST_OVERALL:
                        bestCurSettings = settings;
                        break;
                    case EMPTY:
                        throw new IllegalArgumentException("Should not arrive here with EMPTY as curation");
                    default:
                         throw new IllegalStateException("Unknown element in enum Curations");
                }
            }

            @Override
            public void onRankingDownloadCompleted(CurationGridAdapter.Curations curation, List<Integer> ranking) {

            }
        };
    }

    /**
     * This method creates the selection listener that implements fragments and tab switching.
     *
     * @return  a OnTabSelectedListener
     */
    private TabLayout.OnTabSelectedListener createSwitcher() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFragment(tabsPagerAdapter.getItem(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    /**
     * Method that manages the fragment switching by replacing the old fragment with the new one.
     *
     * @param newFragment   fragment to set as current
     */
    private void changeFragment(Fragment newFragment) {
        currentFragment = newFragment;
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit();
    }

    /***********************************************************************************************
     * FRAGMENT INTERACTION LISTENERS
     **********************************************************************************************/

    /**
     * Launches a ResultAggregationFragment which performs the aggregation with the parameter settings.
     *
     * @param settings  settings for the aggregation
     */
    @Override
    public void onPressGenerate(Map<Integer, Integer> settings, List<Integer> oldRanking, boolean cached) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Cannot perform aggregation from null settings");
        }

        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        ArrayList<Integer> oldResult = (oldRanking == null)? null : new ArrayList<>(oldRanking);
        changeFragment(ResultAggregationFragment.newInstance(settingsCopy, oldResult, cached));
    }

    @Override
    public void showPickIndicatorDialog(Set<Integer> alreadyPicked) {

        // arguments check
        if(alreadyPicked == null) {
            throw new IllegalArgumentException("The set of already picked indicators cannot be null");
        }

        DialogFragment dialog = ChooseIndicatorsDialog.newInstance(new ArrayList<>(alreadyPicked));
        dialog.show(fragmentManager, "ChooseIndicatorsDialog");
    }

    @Override
    public void showLoadSaveDialog() {

        DialogFragment dialog = new ChooseLoadDialog();
        dialog.show(fragmentManager, "LoadSaveDialog");
    }

    @Override
    public void restartGeneration() {
        changeFragment(new CreateRankingFragment());
    }

    /**
     * Switches to a CreateRankingFragment instance with predefined settings.
     *
     * @param settings      desired starting settings
     */

    @Override
    public void startGenerationWithSettings(Map<Integer, Integer> settings, List<Integer> oldRanking, boolean cacheValid) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Cannot generate a ranking from null settings");
        }

        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        if(cacheValid) {
            ArrayList<Integer> oldRankingCopy = new ArrayList<>(oldRanking);
            changeFragment(CreateRankingFragment.newInstanceFromSettings(settingsCopy, oldRankingCopy, cacheValid));
        } else {
            changeFragment(CreateRankingFragment.newInstanceFromSettings(settingsCopy, null, cacheValid));
        }
    }

    /**
     * Updates the indicators chosen in CreateRankingFragment via the apposite dialog.
     *
     * @param pickedOnes    selected indicators
     */
    @Override
    public void addIndicators(Set<Integer> pickedOnes) {

        // arguments check
        if(pickedOnes == null) {
            throw new IllegalArgumentException("Cannot have a null set of indicators selected");
        }

        ((CreateRankingFragment) currentFragment).updateListFromDialog(pickedOnes);
    }

    /**
     * Opens a saved aggregation in the CreateRankingFragment
     *
     * @param toOpen    save to open
     */
    @Override
    public void openSaveFromMyRanking(SaveRank toOpen) {
        launchCreationWithData(toOpen);
    }

    @Override
    public void launchCompareDialog(SaveRank currentlySelected) {

        // transform collections into serializable objects
        ArrayList<Integer> rankList = new ArrayList<>(currentlySelected.getResultList());
        HashMap<Integer, Double> rankScores = new HashMap<>(currentlySelected.getResultScores());
        HashMap<Integer, Integer> rankSettings = new HashMap<>(currentlySelected.getSettings());

        DialogFragment dialog = ChooseLoadDialog.newInstance(currentlySelected.getName(),
                rankList, rankScores, rankSettings);
        dialog.show(fragmentManager, "LoadSaveDialog");
    }

    public void launchCompareFragment(SaveRank rank1, SaveRank rank2) {

        // transform collections into serializable objects
        ArrayList<Integer> rank1List = new ArrayList<>(rank1.getResultList());
        HashMap<Integer, Double> rank1Scores = new HashMap<>(rank1.getResultScores());
        HashMap<Integer, Integer> rank1Settings = new HashMap<>(rank1.getSettings());

        ArrayList<Integer> rank2List = new ArrayList<>(rank2.getResultList());
        HashMap<Integer, Double> rank2Scores = new HashMap<>(rank2.getResultScores());
        HashMap<Integer, Integer> rank2Settings = new HashMap<>(rank2.getSettings());

        // launch comparison
        changeFragment(CompareFragment.newInstance(rank1.getName(), rank1List, rank1Scores, rank1Settings,
                rank2.getName(), rank2List, rank2Scores, rank2Settings));
    }

    /**
     * Opens a saved aggregation in the CreateRankingFragment
     *
     * @param name    save to open
     */
    @Override
    public void openSaveFromLoadDialog(String name) {
        SaveRank toOpen = databaseHelper.retrieveSave(name);
        launchCreationWithData(toOpen);
    }

    @Override
    public void openComparisonSave(String name, SaveRank otherSave) {
        SaveRank toOpen = databaseHelper.retrieveSave(name);
        launchCompareFragment(toOpen, otherSave);
    }

    private void launchCreationWithData(SaveRank toOpen) {
        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> settings = new HashMap<>(toOpen.getSettings());
        ArrayList<Integer> oldRanking = new ArrayList<>(toOpen.getResultList());

        //noinspection ConstantConditions
        tabLayout.getTabAt(0).select();
        changeFragment(CreateRankingFragment.newInstanceFromSettings(settings, oldRanking, true));
    }

    @Override
    public void onCurationClick(CurationGridAdapter.Curations curation) {

        switch(curation) {

            case BEST_COUNTRY:
                while(countryCurSettings == null) {}
                startGenerationWithSettings(countryCurSettings, null, false);
                break;
            case TYPE_AND_AGE:
                while(peersCurSettings == null) {}
                startGenerationWithSettings(peersCurSettings, null, false);
                break;
            case LAST_MONTH:
                while(monthCurSettings == null) {}
                startGenerationWithSettings(monthCurSettings, null, false);
                break;
            case BEST_OVERALL:
                while(bestCurSettings == null) {}
                startGenerationWithSettings(bestCurSettings, null, false);
                break;
            case EMPTY:
                changeFragment(new CreateRankingFragment());
                break;
            default:
                throw new IllegalStateException("Unknown element in enum Curations");
        }
    }

    @Override
    public void goToApp() {
        createRealActivity();
    }

    @Override
    public void showFilterDialog(AddFilterDialog dialog) {
        dialog.show(fragmentManager, "Add filters dialog");
    }

    private class AsyncOpenDatabase extends AsyncTask<Context, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            progressCircle.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Context... params) {

            boolean exists = DatabaseHelper.databaseExists();
            // first database instantiation of app
            DatabaseHelper.getInstance(params[0]);
            if(!exists) {
                settingsOk = false;
                publishProgress(3);
            }
            while(!settingsOk) {}

            publishProgress(0);

            retrieveSharedPool(params[0]);

            while(countryCurSettings == null || peersCurSettings == null || bestCurSettings == null || monthCurSettings == null) {
                //publishProgress(0);
            }

            //TODO remove, just for correct behaviour check and demo
            SystemClock.sleep(2000);

            return exists;
        }

        public void retrieveSharedPool(Context context) {
            OnSharedPoolRetrievalListener retrievalListener = new OnSharedPoolRetrievalListener() {
                @Override
                public void onSharedPoolRetrieved(List<ShareRank> sharedPool) {
                    publishProgress(1);
                    updateCurations(sharedPool, context);
                }
            };

            OnFirebaseErrorListener errorListener = new OnFirebaseErrorListener() {
                @Override
                public void onError(String message) {
                    throw new DatabaseException(message);
                }
            };

            FirebaseHelper firebaseHelper = new FirebaseHelper(context);
            firebaseHelper.retrieveSharedPool(retrievalListener, errorListener);
        }

        private void updateCurations(List<ShareRank> pool, Context context) {

            OnCurationDownloadNotifier notifier = createCurationNotifier();

            CurationUpdater countryCurUpdater = new CurationUpdater(CurationGridAdapter.Curations.BEST_COUNTRY, context, notifier, pool);
            countryCurUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            CurationUpdater peersCurUpdater = new CurationUpdater(CurationGridAdapter.Curations.TYPE_AND_AGE, context, notifier, pool);
            peersCurUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            CurationUpdater monthCurUpdater = new CurationUpdater(CurationGridAdapter.Curations.LAST_MONTH, context, notifier, pool);
            monthCurUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            CurationUpdater topCurUpdater = new CurationUpdater(CurationGridAdapter.Curations.BEST_OVERALL, context, notifier, pool);
            topCurUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            publishProgress(2);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            switch(progress[0]) {
                case 0:
                    localCheckbox.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    remoteCheckbox.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    curationCheckbox.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    askSettings();
                    break;
            }

            progressCircle.setProgress(0);
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            progressCircle.setVisibility(View.INVISIBLE);
            localCheckbox.setVisibility(View.INVISIBLE);
            remoteCheckbox.setVisibility(View.INVISIBLE);
            curationCheckbox.setVisibility(View.INVISIBLE);

            createRealActivity();
        }
    }

}

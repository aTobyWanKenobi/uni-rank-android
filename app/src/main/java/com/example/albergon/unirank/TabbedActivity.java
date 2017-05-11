package com.example.albergon.unirank;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Fragments.BrowseFragment;
import com.example.albergon.unirank.Fragments.ChooseIndicatorsDialog;
import com.example.albergon.unirank.Fragments.ChooseLoadDialog;
import com.example.albergon.unirank.Fragments.CompareFragment;
import com.example.albergon.unirank.Fragments.CreateRankingFragment;
import com.example.albergon.unirank.Fragments.MyRankingsFragment;
import com.example.albergon.unirank.Fragments.ResultAggregationFragment;
import com.example.albergon.unirank.LayoutAdapters.TabsFragmentPagerAdapter;
import com.example.albergon.unirank.Model.SaveRank;

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
        CompareFragment.OnCompareFragmentInteractionListener {

    // Database instance, unique for the entire application
    private DatabaseHelper databaseHelper = null;

    // Layout elements
    private TabLayout tabLayout = null;
    private TabsFragmentPagerAdapter tabsPagerAdapter = null;
    private ViewPager viewPager = null;

    // Fragment management system
    private Fragment currentFragment = null;
    private FragmentManager fragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        changeFragment(new CreateRankingFragment());

        databaseHelper = DatabaseHelper.getInstance(this);
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

    // TODO: keep fragment stack

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

    /**
     * Launches a ResultAggregationFragment which performs the aggregation with the parameter settings.
     *
     * @param settings  settings for the aggregation
     */
    @Override
    public void onPressGenerate(Map<Integer, Integer> settings, List<Integer> oldRanking) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Cannot perform aggregation from null settings");
        }

        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        ArrayList<Integer> oldResult = (oldRanking == null)? null : new ArrayList<>(oldRanking);
        changeFragment(ResultAggregationFragment.newInstance(settingsCopy, oldResult));
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
    public void startGenerationWithSettings(Map<Integer, Integer> settings, List<Integer> oldRanking) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Cannot generate a ranking from null settings");
        }

        @SuppressLint("UseSparseArrays") HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        ArrayList<Integer> oldRankingCopy = new ArrayList<>(oldRanking);
        changeFragment(CreateRankingFragment.newInstanceFromSettings(settingsCopy, oldRankingCopy));
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
        changeFragment(CreateRankingFragment.newInstanceFromSettings(settings, oldRanking));
    }
}

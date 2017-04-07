package com.example.albergon.unirank;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Fragments.ChooseIndicatorsDialog;
import com.example.albergon.unirank.Fragments.CreateRankingFragment;
import com.example.albergon.unirank.Fragments.ResultAggregationFragment;
import com.example.albergon.unirank.LayoutAdapters.TabsFragmentPagerAdapter;
import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.Indicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity implements
        CreateRankingFragment.OnRankGenerationInteractionListener,
        ResultAggregationFragment.ResultFragmentInteractionListener,
        ChooseIndicatorsDialog.ChooseIndicatorDialogInteractionListener {

    private DatabaseHelper databaseHelper = null;

    private TabLayout tabLayout = null;
    private TabsFragmentPagerAdapter tabsPagerAdapter = null;
    private ViewPager viewPager = null;

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

        tabLayout.addOnTabSelectedListener(createBlueSwitcher());

        // initializing the fragment structure
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        changeFragment(new CreateRankingFragment());

        // TODO: move it to service
        // open database
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.createDatabase();
        } catch (IOException e) {

        }
        databaseHelper.openDatabase();

    }

    public DatabaseHelper getDatabase() {
        return databaseHelper;
    }

    private TabLayout.OnTabSelectedListener createBlueSwitcher() {
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
    private void changeFragment(Fragment newFragment) {
        currentFragment = newFragment;
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit();
    }

    @Override
    public void onPressGenerate(Map<Integer, Integer> settings) {

        HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        changeFragment(ResultAggregationFragment.newInstance(settingsCopy));
    }

    @Override
    public void showPickIndicatorDialog(Set<Integer> alreadyPicked) {

        DialogFragment dialog = ChooseIndicatorsDialog.newInstance(new ArrayList<>(alreadyPicked));
        dialog.show(fragmentManager, "ChooseIndicatorsDialog");
    }

    @Override
    public void restartGeneration() {
        changeFragment(new CreateRankingFragment());
    }

    @Override
    public void startGenerationWithSettings(Map<Integer, Integer> settings) {
        HashMap<Integer, Integer> settingsCopy = new HashMap<>(settings);
        changeFragment(CreateRankingFragment.newInstanceFromSettings(settingsCopy));
    }

    @Override
    public void addIndicators(Set<Integer> pickedOnes) {
        ((CreateRankingFragment) currentFragment).updateListFromDialog(pickedOnes);
    }
}

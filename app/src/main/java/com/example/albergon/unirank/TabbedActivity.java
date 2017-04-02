package com.example.albergon.unirank;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.albergon.unirank.Fragments.BlueFragment;
import com.example.albergon.unirank.Fragments.CreateRankingFragment;
import com.example.albergon.unirank.Fragments.GreenFragment;
import com.example.albergon.unirank.Fragments.RedFragment;
import com.example.albergon.unirank.LayoutAdapters.TabsFragmentPagerAdapter;

public class TabbedActivity extends AppCompatActivity implements
    CreateRankingFragment.OnCreateRankingFragmentInteractionListener {

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

        currentFragment = CreateRankingFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();

    }

    public void restartRankGeneration() {
        currentFragment = CreateRankingFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentFragment)
                .commit();
    }

    private TabLayout.OnTabSelectedListener createBlueSwitcher() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragment = tabsPagerAdapter.getItem(tab.getPosition());

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

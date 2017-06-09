package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.albergon.unirank.Fragments.BrowseFragment;
import com.example.albergon.unirank.Fragments.CurationFragment;
import com.example.albergon.unirank.Fragments.MyRankingsFragment;
import com.example.albergon.unirank.Fragments.SettingsFragment;

/**
 * This adapter is assigned to the main activity TabLayout to manage number and names of tabs, on top
 * of instantiating the correct fragments for each tab.
 */
public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 4;
    private Context context;

    // Tab titles
    private final String[] tabTitles = new String[] { "Create", "MyRank", "Browse", "Settings" };


    /**
     * Public constructor that takes the fragment manager of the context in which its implemented and
     * the context itself.
     *
     * @param fm        activity's fragment manager
     * @param context   context
     */
    public TabsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        //arguments check
        if(context == null) {
            throw new IllegalArgumentException("Context for TabPager cannot be null");
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment toRet;

        // Instantiate correct fragment depending on selected tab
        switch(position){
            case 0: toRet = new CurationFragment();
                    //new CreateRankingFragment();
                    break;
            case 1: toRet = MyRankingsFragment.newInstance();
                break;
            case 2: toRet = new BrowseFragment();
                break;
            case 3: toRet = new SettingsFragment();
                break;
            default: throw new IllegalStateException("A non-existing tab was selected");
        }

        return toRet;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}

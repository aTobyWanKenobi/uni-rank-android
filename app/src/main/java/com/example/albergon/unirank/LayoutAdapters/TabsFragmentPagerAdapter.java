package com.example.albergon.unirank.LayoutAdapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.albergon.unirank.Fragments.CreateRankingFragment;
import com.example.albergon.unirank.Fragments.GreenFragment;
import com.example.albergon.unirank.Fragments.MyRankingsFragment;

/**
 * Created by Tobia Albergoni on 02.04.2017.
 */
public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Create", "MyRank", "Browse", "Settings" };
    private Context context;

    public TabsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment toRet = null;

        switch(position){
            case 0: toRet = CreateRankingFragment.newInstance();
                    break;
            case 1: toRet = MyRankingsFragment.newInstance();
                break;
            case 2: toRet = GreenFragment.newInstance();
                break;
            case 3: toRet = GreenFragment.newInstance();
                break;
            default: throw new IllegalStateException("Boh");
        }

        return toRet;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}

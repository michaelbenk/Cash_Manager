package com.pr.se.cash_manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * The PagerAdapter is the adapter to populate pages inside of a {@link ViewPager}.
 * <br><br>
 * The PagerAdapter needs an array of {@link TabFragment} to display the current tab. The arrey
 * is initialized in the {@link MainActivity} and each fragment fills its content by itself.
 *
 * @author Team 1
 * @version 1.0
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private TabFragment[] tabs;

    public PagerAdapter(FragmentManager fm, TabFragment[] tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return this.tabs[position];
    }

    @Override
    public int getCount() {
        return this.tabs.length;
    }
}

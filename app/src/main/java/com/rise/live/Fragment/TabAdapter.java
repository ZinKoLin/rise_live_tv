package com.rise.live.Fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LatestFragment homeFragment = new LatestFragment();
                return homeFragment;
            case 1:
                CategoryFragment photoFragment = new CategoryFragment();
                return photoFragment;
            case 3:
                LiveFragment liveFragment = new LiveFragment();
                return liveFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}

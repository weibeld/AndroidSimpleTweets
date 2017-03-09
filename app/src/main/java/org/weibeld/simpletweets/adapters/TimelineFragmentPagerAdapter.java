package org.weibeld.simpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.weibeld.simpletweets.fragments.TimelineFragment;

import java.util.List;

/**
 * Created by dw on 08/03/17.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    List<TimelineFragment> mFrags;

    public TimelineFragmentPagerAdapter(FragmentManager fragmentManager, List<TimelineFragment> frags) {
        super(fragmentManager);
        mFrags = frags;
    }

    @Override
    public Fragment getItem(int position) {
        return mFrags.get(position);
    }

    @Override
    public int getCount() {
        return mFrags.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFrags.get(position).getTitle();
    }
}

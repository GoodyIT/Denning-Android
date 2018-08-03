package it.denning.navigation.add.leaveapplication;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.denning.navigation.add.leaveapplication.leaveapplication.LeaveApplicationFragment;
import it.denning.navigation.add.leaveapplication.leaverecord.LeaveRecordFragment;

/**
 * Created by denningit on 2018-01-26.
 */

public class LeaveAppPageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    String[] titles = {"Leave Application", "Leave Record"};
    String code;

    public LeaveAppPageAdapter(FragmentManager fm, String code) {
        super(fm);
        this.code = code;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LeaveApplicationFragment.newInstance();
            case 1:
                return LeaveRecordFragment.newInstance(code);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

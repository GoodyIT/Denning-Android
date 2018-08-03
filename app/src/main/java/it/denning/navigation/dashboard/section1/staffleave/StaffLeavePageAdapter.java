package it.denning.navigation.dashboard.section1.staffleave;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import it.denning.navigation.add.leaveapplication.leaveapplication.LeaveApplicationFragment;
import it.denning.navigation.add.leaveapplication.leaverecord.LeaveRecordFragment;
import it.denning.navigation.dashboard.section1.staffleave.dashboardleaverecord.DashboardLeaveRecordFragment;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

/**
 * Created by denningit on 2018-01-26.
 */

public class StaffLeavePageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    String[] titles = {"Leave Application", "Leave Record"};

    public StaffLeavePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DashboardLeaveAppFragment.newInstance();
            case 1:
                return DashboardLeaveRecordFragment.newInstance();
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

package it.denning.navigation.dashboard.section4.feestransfer;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.denning.navigation.dashboard.section1.staffleave.dashboardleaverecord.DashboardLeaveRecordFragment;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;
import it.denning.navigation.dashboard.section4.feestransfer.transferred.TransferredFragment;
import it.denning.navigation.dashboard.section4.feestransfer.untransferred.UnTransferredFragment;

/**
 * Created by denningit on 2018-01-26.
 */

public class FeesTransferAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    String[] titles = {"UNTRANSFERRED", "TRANSFERRED"};
    String url;

    public FeesTransferAdapter(FragmentManager fm, String url) {
        super(fm);
        this.url = url;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UnTransferredFragment.newInstance(url);
            case 1:
                return TransferredFragment.newInstance(url);
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

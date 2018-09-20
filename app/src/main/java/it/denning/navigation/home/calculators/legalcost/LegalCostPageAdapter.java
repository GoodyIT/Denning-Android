package it.denning.navigation.home.calculators.legalcost;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.denning.navigation.dashboard.section1.staffleave.dashboardleaverecord.DashboardLeaveRecordFragment;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

/**
 * Created by denningit on 2018-01-26.
 */

public class LegalCostPageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;
    String[] titles = {"SPA", "Loan", "Tenancy", "Shares"};

    public LegalCostPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SPAFragment.newInstance();
            case 1:
                return LoanFragment.newInstance();
            case 2:
                return TenancyFragment.newInstance();
            case 3:
                return SharesFragment.newInstance();
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

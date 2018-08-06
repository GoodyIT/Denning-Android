package it.denning.navigation.home.calculators.legalcost;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeaveActivity;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeavePageAdapter;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class LegalCostActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.viewpager)
    ViewPager vpPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LegalCostActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_staff_leave;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupPageAdapter();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.basic_legal_costs));
    }

    private void setupPageAdapter() {
        LegalCostPageAdapter adapterViewPager = new LegalCostPageAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(vpPager);
    }
}

package it.denning.navigation.dashboard.section4.feestransfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeavePageAdapter;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class FeesTransferActivity extends BaseActivity{

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.viewpager)
    ViewPager vpPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    public String url, baseUrl;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context, String api) {
        Intent intent = new Intent(context, FeesTransferActivity.class);
        intent.putExtra("url", api);
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
        toolbarTitle.setText(getString(R.string.fees_transfer_title));
        url = getIntent().getStringExtra("api");
        int lastIndex = url.lastIndexOf('/');
        baseUrl = url.substring(0, lastIndex + 1);
    }

    private void setupPageAdapter() {
        FeesTransferAdapter adapterViewPager = new FeesTransferAdapter(getSupportFragmentManager(), baseUrl);
        vpPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(vpPager);
    }
}

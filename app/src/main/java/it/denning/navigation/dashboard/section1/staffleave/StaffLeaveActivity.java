package it.denning.navigation.dashboard.section1.staffleave;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.LeaveRecordModel;
import it.denning.model.NameCode;
import it.denning.navigation.add.leaveapplication.LeaveAppPageAdapter;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class StaffLeaveActivity extends BaseActivity{

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

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, StaffLeaveActivity.class);
        intent.putExtra("url", url);
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
        toolbarTitle.setText(getString(R.string.staff_leave_title));
        url = getIntent().getStringExtra("url");
        int lastIndex = url.lastIndexOf('=');
        baseUrl = url.substring(0, lastIndex + 1);
    }

    private void setupPageAdapter() {
        StaffLeavePageAdapter adapterViewPager = new StaffLeavePageAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(vpPager);
    }
}

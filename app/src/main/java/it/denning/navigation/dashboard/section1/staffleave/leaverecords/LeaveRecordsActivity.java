package it.denning.navigation.dashboard.section1.staffleave.leaverecords;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.NameCode;
import it.denning.navigation.add.leaveapplication.LeaveAppPageAdapter;
import it.denning.navigation.add.leaveapplication.leaverecord.LeaveRecordFragment;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class LeaveRecordsActivity extends BaseActivity{

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public String code;

    public static void start(Context context, String code) {
        Intent intent = new Intent(context, LeaveRecordsActivity.class);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_leave_records;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupFragment();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.leave_records_title);
        code = getIntent().getStringExtra("code");
    }

    private void setupFragment() {
        LeaveRecordFragment fragment = LeaveRecordFragment.newInstance(code);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }
}

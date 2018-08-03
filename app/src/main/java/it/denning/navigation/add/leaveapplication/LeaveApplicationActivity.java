package it.denning.navigation.add.leaveapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.CodeDescription;
import it.denning.model.NameCode;
import it.denning.model.Template;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.template.TemplateAdapter;
import it.denning.search.utils.bootstrap.CustomBootstrapStyle;
import it.denning.search.utils.desc.GeneralDescActivity;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class LeaveApplicationActivity extends BaseActivity{

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.viewpager)
    ViewPager vpPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public NameCode submittedBy;

    public static void start(Context context, NameCode submittedBy) {
        Intent intent = new Intent(context, LeaveApplicationActivity.class);
        intent.putExtra("submittedBy", submittedBy);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_leave_application;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupPageAdapter();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.leave_application_title));
        toolbarSubTitle.setText(DISharedPreferences.getInstance().getUsername());
        submittedBy = (NameCode) getIntent().getSerializableExtra("submittedBy");
    }

    private void setupPageAdapter() {
        LeaveAppPageAdapter adapterViewPager = new LeaveAppPageAdapter(getSupportFragmentManager(), submittedBy.code);
        vpPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(vpPager);
    }
}

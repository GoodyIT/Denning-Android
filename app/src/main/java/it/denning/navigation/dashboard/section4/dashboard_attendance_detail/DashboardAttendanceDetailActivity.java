package it.denning.navigation.dashboard.section4.dashboard_attendance_detail;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.AttendanceDetailModel;
import it.denning.model.StaffOnlineModel;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceAdapter;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class DashboardAttendanceDetailActivity extends GeneralActivity {
    @BindView(R.id.footer_balance)
    TextView footerValue;
    @BindView(R.id.name_textview)
            TextView name;
    @BindView(R.id.date_textview)
            TextView date;

    List<AttendanceDetailModel> modelArrayList = new ArrayList<>();
    StaffOnlineModel model;
    DashboardAttendanceDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_attendance_detail);
        ButterKnife.bind(this);

        model = (StaffOnlineModel)getIntent().getExtras().getSerializable("model");
        updateHeaderAndFooter();
        setupList();
        fetchTask();
    }

    void updateHeaderAndFooter() {
        name.setText(model.name);
        date.setText(DIHelper.today());
        footerValue.setText(model.totalHour);
    }

    void setupList() {
        adapter = new DashboardAttendanceDetailAdapter(modelArrayList);
//        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(adapter);

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
        dashboardList.setItemViewCacheSize(0);
    }

    void fetchTask() {
        showActionBarProgress();
        String url  = model.API;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                AttendanceDetailModel[] models = new Gson().fromJson(jsonElement, AttendanceDetailModel[].class);
                displayResult(Arrays.asList(models));
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hidewActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void displayResult(List<AttendanceDetailModel> newList) {
        adapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }
}

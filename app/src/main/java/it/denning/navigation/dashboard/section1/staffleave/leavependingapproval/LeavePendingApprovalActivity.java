package it.denning.navigation.dashboard.section1.staffleave.leavependingapproval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.NameCode;
import it.denning.model.StaffLeaveModel;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.navigation.add.leaveapplication.leaveapplication.LeaveApplicationAdapter;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeaveActivity;
import it.denning.navigation.dashboard.section1.staffleave.leaverecords.LeaveRecordsActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class LeavePendingApprovalActivity extends BaseActivity implements OnSectionItemClickListener {

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        if (api.trim().length() != 0) {
            StaffLeaveActivity.start(this, api);
        }
        finish();
    }

    public NameCode submittedBy;
    private StaffLeaveModel staffLeaveModel;
    private String api;
    public boolean fromDashboard = false;
    public boolean isDone = false, isSaved = false;
    LeavePendingAppAdapter adapter;

    public static void start(Context context, NameCode submittedBy, StaffLeaveModel staffLeaveModel, boolean fromDashboard) {
        start(context, submittedBy, staffLeaveModel, fromDashboard, "");
    }

    public static void start(Context context, NameCode submittedBy, StaffLeaveModel staffLeaveModel, boolean fromDashboard, String url) {
        Intent intent = new Intent(context, LeavePendingApprovalActivity.class);
        intent.putExtra("submittedBy", submittedBy);
        intent.putExtra("staffLeaveModel", staffLeaveModel);
        intent.putExtra("fromDashboard", fromDashboard);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_general_subtitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupRecyclerView();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.leave_pending_approval_title));
        submittedBy = (NameCode) getIntent().getSerializableExtra("submittedBy");
        toolbarSubTitle.setText(submittedBy.name);
        staffLeaveModel = (StaffLeaveModel) getIntent().getSerializableExtra("staffLeaveModel");
        fromDashboard = getIntent().getBooleanExtra("fromDashboard", false);
        api = getIntent().getStringExtra("url");
    }

    private void setupRecyclerView() {
        adapter = new LeavePendingAppAdapter(this, staffLeaveModel, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void approve() {
        submitApplication(adapter.buildSaveParams("1"));
    }

    private void _reject(String reason) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("strManagerRemarks", reason);
        submitApplication(NetworkManager.mergeJSONObjects(adapter.buildSaveParams("3"), jsonObject));
    }

    public void reject() {
        new MaterialDialog.Builder(this)
            .title(R.string.reject_title)
                .content(R.string.reject_reason_content)
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .positiveText(R.string.agree)
                .input(
                        "Reason",
                        "",
                        false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                _reject(input.toString());
                            }
                        })
                .show();
    }

    private void submitApplication(JsonObject params) {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.STAFF_LEAVE_SAVE_URL;
        NetworkManager.getInstance().sendPrivatePutRequest(url, params, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccess(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageSuccess(JsonElement jsonElement) {
        hideProgress();
        isDone = true;
        staffLeaveModel = new Gson().fromJson(jsonElement, StaffLeaveModel.class);
        adapter.updateModel(staffLeaveModel);
        adapter.buildUI();
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    private void gotoLeaveRecords() {
        LeaveRecordsActivity.start(this, staffLeaveModel.getClsStaff().code);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        switch (name) {
            case "Approve":
                approve();
                break;
            case "Reject":
                reject();
                break;
        }

        findViewById(R.id.search_bank_layout).requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.leave_records, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave_records) {
            gotoLeaveRecords();
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }
}

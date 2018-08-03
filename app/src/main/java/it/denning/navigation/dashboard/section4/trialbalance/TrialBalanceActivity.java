package it.denning.navigation.dashboard.section4.trialbalance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
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
import it.denning.general.DISharedPreferences;
import it.denning.model.TrialBalance;
import it.denning.navigation.dashboard.section4.bankandcash.BankCashActivity;
import it.denning.navigation.dashboard.section4.fileledgers.FileLedgerActivity;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class TrialBalanceActivity extends GeneralActivity implements OnItemClickListener {
    @BindView(R.id.title_textview)
    TextView activityTitle;

    TrialBalanceAdapter adapter;
    ArrayList<TrialBalance> originalModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_staff_duetask);
        ButterKnife.bind(this);

        initFields();
        setupList();
        fetchTask();
    }

    void initFields() {
        activityTitle.setText("Trial Balance");
        _url = getIntent().getStringExtra("api");
    }

    void setupList() {
        originalModelArrayList = new ArrayList<>();
        adapter = new TrialBalanceAdapter(originalModelArrayList, this);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
        dashboardList.setAdapter(adapter);

        dashboardList.setItemViewCacheSize(0);
    }

    void fetchTask() {
        String url  = _url;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                TrialBalance[] models = new Gson().fromJson(jsonElement, TrialBalance[].class);
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

    void displayResult(List<TrialBalance> newList) {
        adapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        TrialBalance model = adapter.getModels().get(position);
        if (model.isBalance.equals("Yes")) {
            gotoNext(BankCashActivity.class, model.APIdebit);
        } else {
            gotoNext(FileLedgerActivity.class, model.APIcredit);
        }
    }

    public void gotoNext(Class<?> cls, String url) {
        Intent i = new Intent(this, cls);
        i.putExtra("api", url);
        startActivity(i);
    }
}

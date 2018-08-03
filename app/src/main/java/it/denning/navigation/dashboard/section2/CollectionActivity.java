package it.denning.navigation.dashboard.section2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.LedgerDetail;
import it.denning.model.SearchResultModel;
import it.denning.navigation.dashboard.section1.FileListingAdapter;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.accounts.transaction.TransactionDetailActivity;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class CollectionActivity extends GeneralActivity implements OnItemClickListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.tabs)
    TabLayout filterTabbar;
    @BindView(R.id.collection_subtitle)
    TextView subTitle;
    @BindView(R.id.segmented)
    SegmentedGroup segmentedGroup;

    CollectionAdapter collectionAdapter;
    ArrayList<LedgerDetail> modelArrayList;
    int position = 0;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_collection);
        ButterKnife.bind(this);

        initFields();
        parseURL();
        setupList();
        setupFilter();
        fetchTask();
    }

    void initFields() {
        _url = getIntent().getStringExtra("api");
        subTitle.setText(getIntent().getStringExtra("label"));
        position = getIntent().getIntExtra("position", 0);
        segmentedGroup.setOnCheckedChangeListener(this);
        filter = "";
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(modelArrayList, getApplicationContext(), this);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(collectionAdapter);
        dashboardList.setLayoutManager(linearLayoutManager);

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setupFilter() {
        filterArray = new String[]{"All", "Client", "Disbursement", "FD", "Advance", "Office"};
        balanceFilterArray = new String[]{"all", "client", "disb", "fdeposit", "advance", "office"};
        for (int i = 0; i < filterArray.length; i++) {
            filterTabbar.addTab(filterTabbar.newTab().setText(filterArray[i]));
            filterTabbar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    applyFilter(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    void applyFilter(TabLayout.Tab tab) {
        curTopFilter = balanceFilterArray[tab.getPosition()];
        page = 1;
        fetchTask();
    }

    void fetchTask() {
        String url  = baseUrl + "/"+  curTopFilter + "/"  +  curBalanceFilter + "?search=" + filter + "&page=" + page;

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                LedgerDetail[] models = new Gson().fromJson(jsonElement, LedgerDetail[].class);
                if (models.length > 0) {
                    page++;
                }
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

    void displayResult(List<LedgerDetail> newList) {
        collectionAdapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        LedgerDetail model = collectionAdapter.getModels().get(position);
        TransactionDetailActivity.start(this, model);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.first_segment:
                curBalanceFilter = "notDeposited";
                break;
            case R.id.second_segment:
                curBalanceFilter = "Deposited";
                break;
        }

        page = 1;
        fetchTask();
    }
}

package it.denning.navigation.dashboard.section4.bankrecon;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import it.denning.model.BankReconModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class BankReconActivity extends GeneralActivity implements OnItemClickListener {
    @BindView(R.id.tabs)
    TabLayout filterTabbar;
    @BindView(R.id.dashboard_search)
    SearchView searchView;
    @BindView(R.id.dashboard_second_layout)
    LinearLayout linearLayout;

    BankReconAdapter bankReconAdapter;
    ArrayList<BankReconModel> modelArrayList;

    int page = 1;
    String filter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_bank_recon);
        ButterKnife.bind(this);
        _url = getIntent().getStringExtra("api");
        parseURL();
        setupList();
        setupFilter();
        setupSearchView();
        loadData();
    }

    void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                hideKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchQuery("");
                hideKeyboard();
                return false;
            }
        });
    }

    void searchQuery(String query) {
        filter = query;
        page = 1;
        loadData();
    }

    void setupList() {
        filterArray = new String[]{"Client", "Disbursement", "FD", "Advance", "Office", "Other"};
        balanceFilterArray = new String[]{"client", "disb", "fd", "advance", "office", "other"};
        modelArrayList = new ArrayList<>();
        bankReconAdapter = new BankReconAdapter(modelArrayList, getApplicationContext(), this);
//        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(bankReconAdapter);

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
        curBalanceFilter = balanceFilterArray[tab.getPosition()];
        bankReconAdapter.clear();
        loadData();
    }

    private void loadData() {
        showActionBarProgress();
        String url  = baseUrl + '/'  +curTopFilter   + '/' + curBalanceFilter + "?search=" + filter + "&page=" + page;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                BankReconModel[] models = new Gson().fromJson(jsonElement, BankReconModel[].class);
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

    void displayResult(List<BankReconModel> newList) {
        bankReconAdapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {

    }
}

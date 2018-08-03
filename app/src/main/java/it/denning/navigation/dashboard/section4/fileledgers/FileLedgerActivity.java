package it.denning.navigation.dashboard.section4.fileledgers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
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
import it.denning.model.BankReconModel;
import it.denning.navigation.dashboard.section4.bankrecon.BankReconAdapter;
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

public class FileLedgerActivity extends GeneralActivity implements OnItemClickListener {
    @BindView(R.id.tabs)
    TabLayout filterTabbar;
    @BindView(R.id.dashboard_search)
    SearchView searchView;
    @BindView(R.id.dashboard_second_layout)
    LinearLayout linearLayout;
    @BindView(R.id.title_textview)
    TextView    activityTitle;

    FileLedgerAdapter adapter;
    ArrayList<BankReconModel> modelArrayList;
    int page = 1, selectedIndex = 0;
    String filter= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_bank_recon);
        ButterKnife.bind(this);

        initFields();
        parseURL();
        setupList();
        getSelectedIndex();
        setupFilter();
        setupSearchView();
        loadData();
    }

    void initFields() {
        activityTitle.setText("File Ledger");
        _url = getIntent().getStringExtra("api");
    }

    void getSelectedIndex() {
        for (int i = 0; i < balanceFilterArray.length; i++) {
            if (balanceFilterArray[i].equals(curBalanceFilter)) {
                selectedIndex = i;
                break;
            }
        }
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
                hideKeyboard();
                searchQuery("");
                return false;
            }
        });
    }

    void searchQuery(String query) {
        filter = query;
        page = 1;
        adapter.clear();
        loadData();
    }

    void setupList() {
        filterArray = new String[]{"All", "Client", "Disbursement", "FD", "Advance", "Other"};
        balanceFilterArray = new String[]{"all", "client", "disb", "fd", "advance", "other"};
        modelArrayList = new ArrayList<>();
        adapter = new FileLedgerAdapter(modelArrayList, getApplicationContext(), this);
        dashboardList.setLayoutManager(linearLayoutManager);
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
        filterTabbar.getTabAt(selectedIndex).select();
    }

    void applyFilter(TabLayout.Tab tab) {
        curBalanceFilter = balanceFilterArray[tab.getPosition()];
        page = 1;
        adapter.clear();
        loadData();
    }

    void loadData() {
        showActionBarProgress();
        String url  =  baseUrl + '/'  +  curTopFilter + '/' + curBalanceFilter + "?search=" + filter + "&page=" + page;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                BankReconModel[] models = new Gson().fromJson(jsonElement, BankReconModel[].class);
                if (models.length > 0) {
                    page++;
                }
                displayResult(Arrays.asList(models));
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(getApplicationContext(), error);
                hidewActionBarProgress();
            }
        });

    }

    void displayResult(List<BankReconModel> newList) {
        adapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        position--;
        Intent i = new Intent(this, FileLedgerDetailActivity.class);
        i.putExtra("api", modelArrayList.get(position).API);
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", modelArrayList.get(position));
        i.putExtras(bundle);
        startActivity(i);
    }
}

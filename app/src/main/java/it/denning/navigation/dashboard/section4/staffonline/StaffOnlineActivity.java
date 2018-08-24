package it.denning.navigation.dashboard.section4.staffonline;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.general.OkHttpUtils;
import it.denning.model.ItemModel;
import it.denning.model.SearchResultModel;
import it.denning.model.StaffOnlineModel;
import it.denning.model.ThreeItemModel;
import it.denning.navigation.dashboard.section1.FileListingAdapter;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class StaffOnlineActivity extends GeneralActivity implements OnItemClickListener {

    @BindView(R.id.badge_all)
    TextView badgeAll;
    @BindView(R.id.badge_today)
    TextView badgeToday;
    @BindView(R.id.badge_thisweek)
    TextView badgeThisWeek;

    @BindView(R.id.dashboard_search)
    SearchView searchView;

    StaffOnlineAdapter adapter;
    ArrayList<StaffOnlineModel> originalModelArrayList = new ArrayList<>();
    String filter = "", _url;
    int page = 1;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_staff_online);
        ButterKnife.bind(this);

        _url = getIntent().getStringExtra("api");

        setupList();
        setupSearchView();
        setupEndlessScroll();

        updateHeader();
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
        originalModelArrayList = new ArrayList<>();
        adapter = new StaffOnlineAdapter(originalModelArrayList, getApplicationContext(), this);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
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

    private void setupEndlessScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadData();
            }
        };
        dashboardList.clearOnScrollListeners();
        dashboardList.addOnScrollListener(scrollListener);
    }

    private void updateHeader() {
        String url  = DIConstants.DASHBOARD_S10_GET_URL;
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageHeaderResponse(jsonElement.getAsJsonObject());
            }
        });
    }

    void manageHeaderResponse(JsonObject jsonObject) {
        ThreeItemModel threeItemModel = new Gson().fromJson(jsonObject, ThreeItemModel.class);
        if (threeItemModel.items.size() > 0) {
            badgeAll.setText(threeItemModel.items.get(0).value);
            badgeToday.setText(threeItemModel.items.get(1).value);
            badgeThisWeek.setText(threeItemModel.items.get(2).value);
        }
    }

    private void loadData() {
        String url  = _url + "?search=" + filter + "&page=" + page;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                StaffOnlineModel[] models = new Gson().fromJson(jsonElement, StaffOnlineModel[].class);
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

    void displayResult(List<StaffOnlineModel> newList) {
        adapter.swapItems(newList);
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
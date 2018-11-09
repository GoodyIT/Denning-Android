package it.denning.navigation.dashboard.section4.dashboardattendance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.StaffOnlineModel;
import it.denning.model.ThreeItemModel;
import it.denning.navigation.dashboard.section4.dashboard_attendance_detail.DashboardAttendanceDetailActivity;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class DashboardAttendanceActivity extends GeneralActivity implements OnItemClickListener {
    @BindView(R.id.badge_all)
    TextView badgeAll;
    @BindView(R.id.badge_onduty)
    TextView badgeOnDuty;
    @BindView(R.id.badge_offduty)
    TextView badgeOffDuty;

    @BindView(R.id.button_all)
    Button btnAll;
    @BindView(R.id.button_onduty)
    Button btnOnDuty;
    @BindView(R.id.button_offduty)
    Button btnOffDuty;
    List<Button> btnArray = new ArrayList<>();

    @BindView(R.id.dashboard_search)
    SearchView searchView;

    private EndlessRecyclerViewScrollListener scrollListener;
    DashboardAttendanceAdapter adapter;
    List<StaffOnlineModel> modelArrayList;
    ThreeItemModel headerModel;
    String filter = "", _url;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_attendance);
        ButterKnife.bind(this);

        btnArray.add(btnAll);
        btnArray.add(btnOnDuty);
        btnArray.add(btnOffDuty);

        setupList();
        _url = getIntent().getStringExtra("api");
        if (!_url.contains("?")) {
            _url += "?filterBy=all";
        }
        fetchHeader();
        loadData();

        setupSearchView();
        setupEndlessScroll();
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
             //   searchQuery(newText);
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
        adapter.clear();
        loadData();
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        adapter = new DashboardAttendanceAdapter(modelArrayList, this, this);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(adapter);
        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
//        dashboardList.setLayoutManager(linearLayoutManager);
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

    void fetchHeader() {
        String url  = DIConstants.DASHBOARD_S11_GET_URL;
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageHeaderResponse(jsonElement.getAsJsonObject());
            }
        });
    }

    void manageHeaderResponse(JsonObject jsonObject) {
        headerModel = new Gson().fromJson(jsonObject, ThreeItemModel.class);
        if (headerModel.items.size() > 0) {
            badgeAll.setText(headerModel.items.get(0).value);
            badgeOnDuty.setText(headerModel.items.get(1).value);
            badgeOffDuty.setText(headerModel.items.get(2).value);
        }
    }

    void resetViews() {
        for (Button button : btnArray) {
            button.setTextColor(Color.GRAY);
        }
    }

    void setStatus(Button button) {
        button.setTextColor(Color.RED);
    }

    void didTapButton(int index) {
        resetViews();
        setStatus(btnArray.get(index));
        _url = headerModel.items.get(index).api;

        page = 1;
        adapter.clear();
        loadData();
    }

    @OnClick(R.id.button_all)
    void onTapAll() {
        didTapButton(0);
    }

    @OnClick(R.id.button_onduty)
    void onTapOnDuty() {
        didTapButton(1);
    }

    @OnClick(R.id.button_offduty)
    void onTapOffDuty() {
        didTapButton(2);
    }

    void loadData() {
        String url  = _url + "&search=" + filter + "&page=" + page;
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
        StaffOnlineModel model = modelArrayList.get(position);
        Intent i = new Intent(this, DashboardAttendanceDetailActivity.class);
        i.putExtra("model", model);
        startActivity(i);
    }
}

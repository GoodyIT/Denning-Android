package it.denning.navigation.dashboard.section1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.general.OkHttpUtils;
import it.denning.model.ItemModel;
import it.denning.model.TaskCheckModel;
import it.denning.navigation.dashboard.section4.taskchecklist.StaffDueTaskActivity;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.utils.KeyboardUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class MyDueTaskActivity extends GeneralActivity implements OnItemClickListener {
    @BindView(R.id.tabs)
    TabLayout filterTabbar;
    @BindView(R.id.dashboard_search)
    SearchView searchView;
    @BindView(R.id.subtitle)
    TextView subTitle;
    @BindView(R.id.dashboard_second_layout)
    LinearLayout linearLayout;

    MyDueTaskAdapter dueTaskAdapter;
    LinearLayoutManager taskLayoutManager;
    ArrayList<TaskCheckModel> modelArrayList;
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;
    String name = "";
    String[] topFilterArray = {"today", "next7", "afterNext7"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_mydutask);
        ButterKnife.bind(this);

        initFields();

        parseURL();

        setupFilter();

        setupList();

        setupSearchView();

        setupEndlessScroll();

        loadData();
    }

    void initFields() {
        _url = getIntent().getStringExtra("api");
        name = getIntent().getStringExtra("name");
        subTitle.setText(name);
        filter = "";
    }

    @Override
    public void parseURL() {
        int lastIndex = _url.lastIndexOf('=');
        if (lastIndex == -1) {
            curTopFilter = topFilterArray[0];
            baseUrl = _url;
        } else {
            curTopFilter = _url.substring(lastIndex+1);
            baseUrl = _url.substring(0, lastIndex);
        }
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        dueTaskAdapter = new MyDueTaskAdapter(modelArrayList, getApplicationContext(), this);
        taskLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        dashboardList.setLayoutManager(taskLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(dueTaskAdapter);

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               hideKeyboard();
                return false;
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(MyDueTaskActivity.this);
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(MyDueTaskActivity.this);
                searchData("");
                return false;
            }
        });
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

    private void searchData(String query) {
        filter = query;
        page = 1;
        NetworkManager.getInstance().clearQueue();
        dueTaskAdapter.clear();
        loadData();
    }

    private void loadData() {
        String url  = baseUrl + "?" + curTopFilter + "&search=" + filter + "&page=" + page;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                TaskCheckModel[] models = new Gson().fromJson(jsonElement, TaskCheckModel[].class);
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

    void displayResult(List<TaskCheckModel> newList) {
        dueTaskAdapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @SuppressWarnings("deprecation")
    private void setupFilter() {
        String[] filterArray = {"Today", "Next 7 Days", "After 7 Days"};
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
        curTopFilter = topFilterArray[0];
    }

    void applyFilter(TabLayout.Tab tab) {
        dueTaskAdapter.clear();
        curTopFilter = topFilterArray[tab.getPosition()];
        page = 1;
        loadData();
    }

    @Override
    public void onClick(View view, int position) {

    }
}

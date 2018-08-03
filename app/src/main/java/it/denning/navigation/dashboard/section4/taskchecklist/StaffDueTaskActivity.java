package it.denning.navigation.dashboard.section4.taskchecklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.ItemModel;
import it.denning.navigation.dashboard.section1.MyDueTaskActivity;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class StaffDueTaskActivity extends GeneralActivity implements OnItemClickListener {
    StaffDueTaskAdapter staffDueTaskAdapter;
    ArrayList<ItemModel> modelArrayList;
    private EndlessRecyclerViewScrollListener scrollListener;
    int page = 1;

    @BindView(R.id.dashboard_search)
    SearchView searchView;
    @BindView(R.id.title_textview)
    TextView    title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_contact_listing);
        ButterKnife.bind(this);

        initFields();

        setupList();
        setupSearchView();
        setupEndlessScroll();
        loadData();
    }

    void initFields() {
        _url = getIntent().getStringExtra("api");
        title.setText(R.string.due_task_title);
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        staffDueTaskAdapter = new StaffDueTaskAdapter(modelArrayList, getApplicationContext(), this);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(staffDueTaskAdapter);

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
                KeyboardUtils.hideKeyboard(StaffDueTaskActivity.this);
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
                KeyboardUtils.hideKeyboard(StaffDueTaskActivity.this);
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
        staffDueTaskAdapter.clear();
        loadData();
    }

    private void loadData() {
        String url  = _url + "?search=" + filter;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                ItemModel[] models = new Gson().fromJson(jsonElement, ItemModel[].class);
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

    void displayResult(List<ItemModel> newList) {
        staffDueTaskAdapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        ItemModel model = modelArrayList.get(position);
        Intent i = new Intent(this, MyDueTaskActivity.class);
        i.putExtra("api", model.api);
        i.putExtra("name", model.label);
        startActivity(i);
    }
}

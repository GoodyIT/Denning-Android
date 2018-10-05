package it.denning.navigation.dashboard.section4.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.general.OnBottomReachedListener;
import it.denning.model.Contact;
import it.denning.model.SearchResultModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.MySearchBaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class DashboardContactActivity extends MySearchBaseActivity implements OnItemClickListener {
    String _url;

    DashboardContactAdapter adapter;
    ArrayList<SearchResultModel> modelArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String filter = "";
    private int page = 1;
    private boolean hasCallback = false;
    private boolean fromSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupRecyclerView();

        setupSearchView();

//        setupEndlessScroll();
        loadData();
    }

    private void initFields() {
        _url = getIntent().getStringExtra("api");
        toolbarTitle.setText(R.string.contact_title);
        hasCallback = getIntent().getBooleanExtra("hasCallback", false);
    }

    private void setupRecyclerView() {
        adapter = new DashboardContactAdapter(new ArrayList<SearchResultModel>(), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                loadData();
            }
        });
    }

    private void setupEndlessScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fromSearch = false;
                loadData();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(DashboardContactActivity.this);
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                searchData(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(DashboardContactActivity.this);
                searchData("");
                return true;
            }
        });
    }

    private void searchData(String query) {
        page = 1;
        filter = query;
        fromSearch = true;
        loadData();
    }

    private void manageResponse(JsonElement jsonElement) {
        hideActionBarProgress();
        SearchResultModel[] accountTypes = new Gson().fromJson(jsonElement, SearchResultModel[].class);
        if (accountTypes.length > 0) {
            page++;
        }
        if (fromSearch) {
            adapter.clear();
        }
        adapter.swapItems(Arrays.asList(accountTypes));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        ErrorUtils.showError(this, error);
    }

    private void loadData() {
        String url = _url + "?search=" + filter + "&page=" + page;

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        KeyboardUtils.hideKeyboard(this);
        if (hasCallback) {
            setResult(Activity.RESULT_CANCELED, new Intent());
        }
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        SearchResultModel model = adapter.getModels().get(position);
        if (hasCallback) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("code", model.getJsonDesc().get("code").getAsString());
            returnIntent.putExtra("name", model.getJsonDesc().get("name").getAsString());
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        } else {
            openContact(model.key);
        }
    }

    private void manageContactResponse(JsonElement jsonElement) {
        DISharedPreferences.contact = new Gson().fromJson(jsonElement, Contact.class);
        SearchContactActivity.start(this, "");
    }

    void openContact(final String key) {
        String url = DIConstants.CONTACT_GET_DETAIL_URL + key;
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageContactResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }
}

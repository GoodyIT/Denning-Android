package it.denning.navigation.add.utils.accounttype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.AccountType;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.MySearchBaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-16.
 */

public class AccountTypeActivity extends MySearchBaseActivity implements OnItemClickListener {
    private int title;
    private AccountTypeAdapter adapter;
    private String filter = "";
    private int page = 1;
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;


    public static void start(Context context, int title) {
        Intent intent = new Intent(context, AccountTypeActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupRecyclerView();

        setupSearchView();

        setupEndlessScroll();
        loadData();
    }

    private void initFields() {
        title = getIntent().getIntExtra("title", -1);
        toolbarTitle.setText(title);
    }

    private void setupRecyclerView() {
        adapter = new AccountTypeAdapter(new ArrayList<AccountType>());
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this);
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
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(AccountTypeActivity.this);
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
                KeyboardUtils.hideKeyboard(AccountTypeActivity.this);
                return true;
            }
        });
    }



    private void searchData(String query) {
        page = 1;
        filter = query;
        adapter.clear();
        loadData();
    }

    private void manageResponse(JsonElement jsonElement) {
        hideActionBarProgress();
        AccountType[] accountTypes = new Gson().fromJson(jsonElement, AccountType[].class);
        if (accountTypes.length > 0) {
            page++;
        }
        adapter.swapItems(Arrays.asList(accountTypes));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        ErrorUtils.showError(this, error);
    }

    private void loadData() {
        String url = DIConstants.ACCOUNT_TYPE_GET_LIST_URL + filter + "&page=" + page;

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

    @Override
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        KeyboardUtils.hideKeyboard(this);
        Intent intent = new Intent();
        intent.putExtra("AccountType", adapter.getModels().get(position));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}

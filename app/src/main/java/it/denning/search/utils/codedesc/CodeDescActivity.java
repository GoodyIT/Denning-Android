package it.denning.search.utils.codedesc;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.CodeDescription;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.utils.OnClickListenerForCodeDesc;
import it.denning.search.utils.OnClickListenerWithCode;
import it.denning.ui.activities.base.MySearchBaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-27.
 */

public class CodeDescActivity extends MySearchBaseActivity implements OnClickListenerForCodeDesc {
    private String title, url;
    private String filter = "";
    private int page = 1;
    private LinearLayoutManager linearLayoutManager;
    private CodeDescAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupSearchView();

        setupRecyclerView();

        setupEndlessScroll();

        loadData();
    }

    private void initFields() {
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        toolbarTitle.setText(title);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(CodeDescActivity.this);
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
                KeyboardUtils.hideKeyboard(CodeDescActivity.this);
                return true;
            }
        });
    }

    private void searchData(String query) {
        filter = query;
        page = 1;
        NetworkManager.getInstance().clearQueue();
        adapter.clear();
        loadData();
    }

    private void setupRecyclerView() {
        adapter = new CodeDescAdapter(new ArrayList<CodeDescription>(), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
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

    private void manageResponse(JsonArray jsonArray) {
        hideActionBarProgress();
        CodeDescription[] codeDescriptions = new Gson().fromJson(jsonArray, CodeDescription[].class);
        if (codeDescriptions.length > 0) {
            page++;
        }
        adapter.addItems(Arrays.asList(codeDescriptions));
    }

    private void loadData() {
        String _url = url + filter + "&page=" + page;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(_url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(CodeDescActivity.this, error);
            }
        });
    }

    @Override
    protected void onBack() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onClick(View view, CodeDescription model) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("model",model);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}

package it.denning.navigation.dashboard.section4.feestransferreddetail;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.FeesTransferModel;
import it.denning.model.FeesUnTransferModel;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.network.services.DownloadService;
import it.denning.network.utils.Download;
import it.denning.network.utils.DownloadCompleteInterface;
import it.denning.network.utils.ProgressInterface;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class FeesTransferDetailActivity extends BaseActivity implements OnItemClickListener{
    @BindView(R.id.general_search)
    SearchView searchView;
    @BindView(R.id.toolbar_title)
    TextView    title;
    @BindView(R.id.toolbar_sub_title)
    TextView subTitle;
    @BindView(R.id.recycler_list)
    public RecyclerView recyclerView;

    FeesTransferDetailsAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    FeesTransferModel feesTransferModel;
    ArrayList<FeesUnTransferModel> modelArrayList = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);;
    public String filter = "";
    int page = 1;

    @Override
    protected int getContentResId() {
        return R.layout.activity_fees_transfer_details;
    }

    public static void start(Context context, FeesTransferModel model) {
        Intent intent = new Intent(context, FeesTransferDetailActivity.class);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupList();
        setupSearchView();
        loadData();
        setupEndlessScroll();
    }

    void setupList() {
        adapter = new FeesTransferDetailsAdapter(modelArrayList, getApplicationContext(), this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardUtils.hideKeyboard(v);
                return false;
            }
        });
        recyclerView.setItemViewCacheSize(0);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(FeesTransferDetailActivity.this);
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
                KeyboardUtils.hideKeyboard(FeesTransferDetailActivity.this);
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
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void searchData(String query) {
        filter = query;
        page = 1;
        NetworkManager.getInstance().clearQueue();
        adapter.clear();
        loadData();
    }

    private void initFields() {
        title.setText(R.string.fees_transfer_title);
        feesTransferModel = (FeesTransferModel) getIntent().getSerializableExtra("model");
        subTitle.setText(feesTransferModel.batchNo);
    }


    private void manageSuccessResponse(JsonElement jsonElement) {
        hideActionBarProgress();
        FeesUnTransferModel[] FeesUnTransferModels = new Gson().fromJson(jsonElement, FeesUnTransferModel[].class);
        if (FeesUnTransferModels.length > 0) {
            page++;
        }

        adapter.swapItems(Arrays.asList(FeesUnTransferModels));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        ErrorUtils.showError(this, error);
    }

    void loadData() {
        showActionBarProgress();
        String url  = feesTransferModel.API + "?page=" + page + "&search=" + filter;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccessResponse(jsonElement);
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
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onClick(View view, int position) {

    }
}

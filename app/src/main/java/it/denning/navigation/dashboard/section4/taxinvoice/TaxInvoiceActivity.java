package it.denning.navigation.dashboard.section4.taxinvoice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.TaxInvoiceModel;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class TaxInvoiceActivity extends BaseActivity implements OnItemClickListener, ProgressInterface, DownloadCompleteInterface {
    @BindView(R.id.tablayout)
    TabLayout filterTabbar;
    @BindView(R.id.general_search)
    SearchView searchView;
    @BindView(R.id.toolbar_title)
    TextView    title;
    @BindView(R.id.recycler_list)
    public RecyclerView recyclerView;

    TaxInvoiceAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<TaxInvoiceModel> modelArrayList = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);;
    public String curBalanceFilter, _url, baseUrl;
    public String[] filterArray ;
    public String[] balanceFilterArray;
    public String filter = "", fileNo;
    int page = 1;
    private boolean hasCallback = false;

    @Override
    protected int getContentResId() {
        return R.layout.activity_search_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        parseURL();
        setupList();
        setupSearchView();
        setupFilter();
        loadData();
        setupEndlessScroll();
    }

    void setupList() {
        filterArray = new String[]{"All", "Settled", "Outstanding"};
        balanceFilterArray = new String[]{"all", "settled", "outstanding"};
        adapter = new TaxInvoiceAdapter(modelArrayList, getApplicationContext(), this);
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
                KeyboardUtils.hideKeyboard(TaxInvoiceActivity.this);
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
                KeyboardUtils.hideKeyboard(TaxInvoiceActivity.this);
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
        title.setText(R.string.tax_invoice_title);
        _url = getIntent().getStringExtra("api");
        fileNo = getIntent().getStringExtra("fileNo");
        hasCallback = getIntent().getBooleanExtra("hasCallback", false);
    }

    public void parseURL() {
        int lastIndex = _url.lastIndexOf('/');
        curBalanceFilter = _url.substring(lastIndex+1);
        baseUrl = _url.substring(0, lastIndex);
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
        page = 1;
        adapter.clear();
        NetworkManager.getInstance().clearQueue();
        loadData();
    }

    private void manageSuccessResponse(JsonElement jsonElement) {
        hideActionBarProgress();
        TaxInvoiceModel[] taxInvoiceModels = new Gson().fromJson(jsonElement, TaxInvoiceModel[].class);
        if (taxInvoiceModels.length > 0) {
            page++;
        }

        adapter.swapItems(Arrays.asList(taxInvoiceModels));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        ErrorUtils.showError(this, error);
    }

    void loadData() {
        showActionBarProgress();
        String url  = baseUrl + '/' + curBalanceFilter + "?page=" + page + "&search=" + filter + "&fileNo=" + fileNo;
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
        TaxInvoiceModel model = adapter.getModels().get(position);
        _url = model.APIpdf;
        if (hasCallback) { // Called from Add Receipt
            Intent intent = new Intent();
            intent.putExtra("model", model);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else { // Open document
            downloadFile();
        }
    }

    public void downloadFile(){

        if(checkPermission()){
            startDownload();
        } else {
            requestPermission();
        }
    }

    private void startDownload(){
        DownloadService downloadService =  new DownloadService(this, _url, this, this);
        downloadService.initDownload();
        showActionBarProgress();
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DIConstants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DIConstants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload();
                } else {
                    showSnackbar(R.string.permission_denied, Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onProgress(final Download download) {
        if (download.getProgress() == 100) {
            hideActionBarProgress();
        }
    }

    @Override
    public void onComplete(final File file) {
        if (DISharedPreferences.isDenningFile) {
            DISharedPreferences.file = file;
            DISharedPreferences.isDenningFile = false;
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hideActionBarProgress();
                        new DIFileManager(TaxInvoiceActivity.this).openFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onFailure(String error) {
        ErrorUtils.showError(this, error);
    }

}

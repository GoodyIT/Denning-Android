package it.denning.navigation.dashboard.section4.dashboardquotation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.BillModel;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceActivity;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceAdapter;
import it.denning.navigation.dashboard.section4.viewquotation.ViewQuotationActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-02-09.
 */

public class DashboardQuotationActivity extends BaseActivity implements OnItemClickListener {
    @BindView(R.id.tablayout)
    TabLayout filterTabbar;
    @BindView(R.id.general_search)
    SearchView searchView;
    @BindView(R.id.toolbar_title)
    TextView    title;
    @BindView(R.id.recycler_list)
    public RecyclerView recyclerView;

    DashboardQuotationAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<TaxInvoiceModel> modelArrayList = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);;
    public String curBalanceFilter, _url, baseUrl;
    public String[] filterArray ;
    public String[] balanceFilterArray;
    public String filter = "";
    int page = 1;
    private boolean hasCallback = false;

    @OnClick(R.id.back_btn)
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        NetworkManager.getInstance().clearQueue();
        finish();
    }

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
        filterArray = new String[]{"All", "Converted", "Pending"};
        balanceFilterArray = new String[]{"all", "convert", "pending"};
        adapter = new DashboardQuotationAdapter(modelArrayList, getApplicationContext(), this);
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
                KeyboardUtils.hideKeyboard(DashboardQuotationActivity.this);
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
             //   searchData(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchData("");
                KeyboardUtils.hideKeyboard(DashboardQuotationActivity.this);
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
        title.setText(R.string.quotation_title);
        _url = getIntent().getStringExtra("api");
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
        String url  = baseUrl + '/' + curBalanceFilter + "?page=" + page + "&search=" + filter;
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
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(model.APIpdf, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                gotoViewQuotation(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(DashboardQuotationActivity.this, error);
            }
        });
    }

    public void gotoViewQuotation(JsonElement jsonElement) {
        hideProgress();
        BillModel billModel = new Gson().fromJson(jsonElement, BillModel.class);
        ViewQuotationActivity.start(this, billModel);
    }
}

package it.denning.navigation.dashboard.section4.dashboardcontactfolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.ContactFolderItem;
import it.denning.model.DocumentModel;
import it.denning.model.FeesTransferModel;
import it.denning.model.FeesUnTransferModel;
import it.denning.navigation.dashboard.section4.feestransferreddetail.FeesTransferDetailsAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.document.DocumentActivity;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class DashboardContactFolderActivity extends BaseActivity implements OnItemClickListener{
    @BindView(R.id.general_search)
    SearchView searchView;
    @BindView(R.id.toolbar_title)
    TextView    title;
    @BindView(R.id.recycler_list)
    public RecyclerView recyclerView;

    DashboardContactFolderAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<ContactFolderItem> modelArrayList = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);;
    public String filter = "", api;
    int page = 1;

    @Override
    protected int getContentResId() {
        return R.layout.activity_search_general;
    }

    public static void start(Context context, FeesTransferModel model) {
        Intent intent = new Intent(context, DashboardContactFolderActivity.class);
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
        adapter = new DashboardContactFolderAdapter(modelArrayList, getApplicationContext(), this);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
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
                KeyboardUtils.hideKeyboard(DashboardContactFolderActivity.this);
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
                KeyboardUtils.hideKeyboard(DashboardContactFolderActivity.this);
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
        title.setText(R.string.contact_title);
        api = getIntent().getStringExtra("api");
    }

    private void manageSuccessResponse(JsonElement jsonElement) {
        hideActionBarProgress();
        ContactFolderItem[] FeesUnTransferModels = new Gson().fromJson(jsonElement, ContactFolderItem[].class);
        if (FeesUnTransferModels.length > 0) {
            page++;
        }

        adapter.swapItems(Arrays.asList(FeesUnTransferModels));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    void loadData() {
        showActionBarProgress();
        String url  = api + "?page=" + page + "&search=" + filter;
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
        ContactFolderItem item = adapter.getModels().get(position);
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(item.url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                DISharedPreferences.documentModel = new Gson().fromJson(jsonElement, DocumentModel.class);
                DocumentActivity.start(DashboardContactFolderActivity.this, "Contact Folder");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }
}

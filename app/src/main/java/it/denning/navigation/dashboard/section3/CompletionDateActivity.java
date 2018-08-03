package it.denning.navigation.dashboard.section3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.CompletionTrackingModel;
import it.denning.model.CourtDiaryCourt;
import it.denning.model.DashboardModel;
import it.denning.model.S3;
import it.denning.model.ThirdItemModel;
import it.denning.navigation.add.utils.courtdiarycourtlist.CourtDiaryCourtListAdapter;
import it.denning.navigation.dashboard.util.DashboardForthItemAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.activities.base.MySearchBaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-16.
 */

public class CompletionDateActivity extends BaseActivity implements OnItemClickListener{
    private CompletionDateAdapter adapter;
    private String filter = "";
    private int page = 1;
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    Point size;
    String url;
    int position;
    List<ThirdItemModel> headerModel;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    protected TextView subTitle;

    @BindView(R.id.general_search)
    protected SearchView searchView;

    @BindView(R.id.forth_gridview)
    GridView headerGridView;


    @Override
    protected int getContentResId() {
        return R.layout.activity_completion_date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();

        initActionBar();

        setupHeader();

        setupRecyclerView();

        setupSearchView();

        setupEndlessScroll();

        loadData();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.completion_date_title);
        subTitle.setText("Sub - Sale");

        position = getIntent().getIntExtra("position", 0);
        headerModel = ((S3)getIntent().getSerializableExtra("model")).items;
        url = headerModel.get(position).api;

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    private void onHeaderClick(int position) {
        url = headerModel.get(position).api;
        searchData("");
    }

    private void setupHeader() {
        final DashboardForthItemAdapter itemAdapter = new DashboardForthItemAdapter(this, headerModel, position);
        ViewGroup.LayoutParams layoutParams = headerGridView.getLayoutParams();
        layoutParams.height = (size.x/4-50)*2; //this is in pixels
        headerGridView.setLayoutParams(layoutParams);
        headerGridView.setAdapter(itemAdapter);
        headerGridView.setColumnWidth(size.x/4-9);
        headerGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onHeaderClick(position);
                itemAdapter.curPosition = position;
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CompletionDateAdapter(new ArrayList<CompletionTrackingModel>());
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
                KeyboardUtils.hideKeyboard(CompletionDateActivity.this);
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
                KeyboardUtils.hideKeyboard(CompletionDateActivity.this);
                searchData("");
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
        CompletionTrackingModel[] contacts = new Gson().fromJson(jsonElement, CompletionTrackingModel[].class);
        if (contacts.length > 0) {
            page++;
        }
        adapter.swapItems(Arrays.asList(contacts));
    }

    private void manageError(String error) {
        hideActionBarProgress();
        ErrorUtils.showError(this, error);
    }

    private void loadData() {
        String _url = this.url + "?search=" + filter + "&page=" + page;

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(_url, new CompositeCompletion() {
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
    public void onClick(View view, int position) {
        KeyboardUtils.hideKeyboard(this);
//        Intent intent = new Intent();
//        intent.putExtra("model", adapter.getModels().get(position));
//        setResult(Activity.RESULT_OK, intent);
//        finish();
    }

    @OnClick(R.id.back_btn)
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }
}

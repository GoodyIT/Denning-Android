package it.denning.navigation.dashboard.section1;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

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
import it.denning.model.MatterModel;
import it.denning.model.SearchResultModel;
import it.denning.model.ThreeItemModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.SearchActivity;
import it.denning.search.matter.MatterActivity;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class FileListingActivity extends GeneralActivity implements OnItemClickListener {

    @BindView(R.id.badge_all)
    TextView badgeAll;
    @BindView(R.id.badge_today)
    TextView badgeToday;
    @BindView(R.id.badge_thisweek)
    TextView badgeThisWeek;

    @BindView(R.id.button_all)
    Button btnAll;
    @BindView(R.id.button_today)
    Button btnToday;
    @BindView(R.id.button_thisweek)
    Button btnThisWeek;
    List<Button> btnArray = new ArrayList<>();

    @BindView(R.id.dashboard_search)
    SearchView searchView;

    private EndlessRecyclerViewScrollListener scrollListener;

    FileListingAdapter fileListingAdapter;
    ArrayList<SearchResultModel> modelArrayList;
    ThreeItemModel fileList;
    String filter = "", _url;
    int page = 1;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_file_listing);
        ButterKnife.bind(this);

        initFields();
        setupList();
//        setupEndlessScroll();
        setupSearchView();

        fetchHeader();
        loadData();
    }

    public void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                hideKeyboard();
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
                searchData("");
                hideKeyboard();
                return false;
            }
        });
    }

    void searchData(String query) {
        page = 1;
        filter = query;
        fileListingAdapter.clear();
        loadData();
    }

    void initFields() {
        btnArray.add(btnAll);
        btnArray.add(btnToday);
        btnArray.add(btnThisWeek);
        _url = getIntent().getStringExtra("api");
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        fileListingAdapter = new FileListingAdapter(modelArrayList, getApplicationContext(), this);
        dashboardList.setHasFixedSize(true);
        dashboardList.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        dashboardList.setItemAnimator(new DefaultItemAnimator());
//        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setAdapter(fileListingAdapter);
        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    void setupEndlessScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!isLoading) {
                    loadData();
                }
            }
        };
        dashboardList.clearOnScrollListeners();
        dashboardList.addOnScrollListener(scrollListener);
    }

    void fetchHeader() {
        String url  = DIConstants.DASHBOARD_S1_MATTERLISTING_GET_URL;
        NetworkManager.getInstance().sendPrivateGetRequestWithoutError(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageHeaderResponse(jsonElement.getAsJsonObject());
            }
        });
    }

    void manageHeaderResponse(JsonObject jsonObject) {
        fileList = new Gson().fromJson(jsonObject, ThreeItemModel.class);
        if (fileList.items.size() > 0) {
            badgeAll.setText(fileList.items.get(0).value);
            badgeToday.setText(fileList.items.get(1).value);
            badgeThisWeek.setText(fileList.items.get(2).value);
        }
    }

    void resetViews() {
        for (Button button : btnArray) {
            button.setTextColor(Color.GRAY);
        }
    }

    void setStatus(Button button) {
        button.setTextColor(Color.RED);
    }

    void didTapButton(int index) {
        resetViews();
        setStatus(btnArray.get(index));
        _url = fileList.items.get(index).api;
        page = 1;
        fileListingAdapter.clear();
        loadData();
    }

    @OnClick(R.id.button_all)
    void onTapAll() {
        didTapButton(0);
    }

    @OnClick(R.id.button_today)
    void onTapToday() {
        didTapButton(1);
    }

    @OnClick(R.id.button_thisweek)
    void onTapThisWeek() {
        didTapButton(2);
    }

    void loadData() {
        isLoading = true;
        showActionBarProgress();
        String url  = _url + "?search=" + filter + "&page=" + page;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                displayResult(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                isLoading = false;
                hidewActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void displayResult(JsonElement jsonElement) {
        isLoading = false;
        hidewActionBarProgress();
        SearchResultModel[] models = new Gson().fromJson(jsonElement, SearchResultModel[].class);
        if (models.length > 0) {
            page++;
        }
        fileListingAdapter.swapItems(Arrays.asList(models));
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        SearchResultModel model = fileListingAdapter.getModel().get(position);
        showActionBarProgress();
        String url = "v1/app/matter/" + model.key;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                MatterModel model = new Gson().fromJson(jsonElement, MatterModel.class);
                MatterActivity.start(FileListingActivity.this, model);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hidewActionBarProgress();
                ErrorUtils.showError(FileListingActivity.this, error);
            }
        });
    }
}
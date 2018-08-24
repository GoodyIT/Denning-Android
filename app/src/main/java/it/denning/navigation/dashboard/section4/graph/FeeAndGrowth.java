package it.denning.navigation.dashboard.section4.graph;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.BillModel;
import it.denning.model.GraphModel;
import it.denning.model.ThreeItemModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.activities.base.MyBaseActivity;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 08/11/2017.
 */

public class FeeAndGrowth extends MyBaseActivity {
    @BindView(R.id.dashboard_second_layout)
    LinearLayout linearLayout;
    ArrayList<GraphModel> modelArrayList;
    FeesAndGrowthAdapter feesAndGrowthAdapter;

    private String _url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        initActionBar();
        setupRecyclerView();

        fetchTask(_url);
    }

    private void initFields() {
        toolbarTitle.setText(R.string.fee_matter_growth);

        _url = getIntent().getStringExtra("api");
    }

    void setupRecyclerView() {
        modelArrayList = new ArrayList<>();
        feesAndGrowthAdapter = new FeesAndGrowthAdapter(modelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(feesAndGrowthAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.VERTICAL, false));
    }

    void fetchTask(final String _url) {
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(_url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                manageResponse(jsonElement.getAsJsonObject());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
            }
        });
    }

    void manageResponse(JsonObject jsonObject) {
        ThreeItemModel threeItem = new Gson().fromJson(jsonObject, ThreeItemModel.class);
        feesAndGrowthAdapter.swapItems(threeItem.graphs);
    }
}
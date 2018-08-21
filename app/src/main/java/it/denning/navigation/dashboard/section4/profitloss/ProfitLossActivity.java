package it.denning.navigation.dashboard.section4.profitloss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.ItemModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossActivity extends BaseActivity implements OnItemClickListener {
    ProfitLossAdapter profitLossAdapter;
    ArrayList<ItemModel> modelArrayList;

    @Override
    protected int getContentResId() {
        return R.layout.activity_general;
    }

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context, String api) {
        Intent intent = new Intent(context, ProfitLossActivity.class);
        intent.putExtra("api", api);
        context.startActivity(intent);
    }

    private String _url;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.profit_loss_title));
        _url = getIntent().getStringExtra("api");

        setupRecyclerView();
        loadData();
    }

    void setupRecyclerView() {
        profitLossAdapter = new ProfitLossAdapter(new ArrayList<ItemModel>(), getApplicationContext(), this);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(profitLossAdapter);

    }

    void loadData() {
        String url  = DISharedPreferences.getInstance(getApplicationContext()).getServerAPI() + _url;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplication(), error);
            }
        });
    }

    private void manageResponse(JsonArray jsonArray) {
        hideActionBarProgress();
        ItemModel[] itemModels = new Gson().fromJson(jsonArray, ItemModel[].class);
        profitLossAdapter.swapItems(Arrays.asList(itemModels));
    }

    @Override
    public void onClick(View view, int position) {
        ItemModel model = modelArrayList.get(position);
        Intent i = new Intent(this, ProfitLossDetailActivity.class);
        i.putExtra("api", model.api);
        startActivity(i);
    }
}

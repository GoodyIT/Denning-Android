package it.denning.navigation.dashboard.section4.profitloss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.ProfitLossDetailModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossDetailActivity extends BaseActivity {
    @BindView(R.id.year_textView)
    TextView topYear;
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        NetworkManager.getInstance().clearQueue();
        finish();
    }

    ProfitLossDetailAdapter adapter;
    String curYear;
    String _url, baseUrl;

    @Override
    protected int getContentResId() {
        return R.layout.activity_profit_loss_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
        setupRecyclerView();
        loadData();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.profit_loss_title));
        _url = getIntent().getStringExtra("api");
        int lastIndex = _url.lastIndexOf('/');
        baseUrl = _url.substring(0, lastIndex+1);
        curYear = _url.substring(lastIndex+1);
        topYear.setText(curYear);
    }

    @OnClick(R.id.prev_button)
    void didTapPrev() {
        curYear = Integer.toString(Integer.parseInt(curYear) - 1);
        topYear.setText(curYear);
        loadData();
    }

    @OnClick(R.id.next_button)
    void didTapNext() {
        curYear = Integer.toString(Integer.parseInt(curYear) + 1);
        topYear.setText(curYear);
        loadData();
    }

    void setupRecyclerView() {
        adapter = new ProfitLossDetailAdapter(null);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        String url  = baseUrl + curYear;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                manageResponse(jsonElement.getAsJsonObject());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(ProfitLossDetailActivity.this, error);
            }
        });
    }

    private void manageResponse(JsonObject jsonObject) {
        ProfitLossDetailModel model = new Gson().fromJson(jsonObject, ProfitLossDetailModel.class);
        adapter.setItem(model);
    }
}

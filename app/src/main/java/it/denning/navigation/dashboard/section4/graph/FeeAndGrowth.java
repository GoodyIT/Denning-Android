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
import it.denning.model.GraphModel;
import it.denning.model.ThreeItemModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 08/11/2017.
 */

public class FeeAndGrowth extends GeneralActivity {
    @BindView(R.id.dashboard_second_layout)
    LinearLayout linearLayout;
    ArrayList<GraphModel> modelArrayList;
    FeesAndGrowthAdapter feesAndGrowthAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_graph);
        ButterKnife.bind(this);

        setupList();
        _url = getIntent().getStringExtra("api");
        fetchTask(_url);
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        feesAndGrowthAdapter = new FeesAndGrowthAdapter(modelArrayList);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(feesAndGrowthAdapter);
        dashboardList.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.VERTICAL, false));

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
        dashboardList.setItemViewCacheSize(0);
    }

    void fetchTask(final String _url) {


        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading", true, false);
        new Thread(new Runnable() {
            public void run() {
                String url  = DISharedPreferences.getInstance(getApplicationContext()).getServerAPI() + _url;
                Request request = new Request.Builder()
                        .url(url)
                        .header("Content-Type", "application/json")
                        .addHeader("webuser-sessionid", DISharedPreferences.getInstance(getApplicationContext()).getSessionID())
                        .addHeader("webuser-id", DISharedPreferences.getInstance(getApplicationContext()).getEmail())
                        .build();

                try {
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    pd.dismiss();
                    if (response != null) {
                        if (!response.isSuccessful()) {
                            Snackbar.make(linearLayout, response.message(), Snackbar.LENGTH_LONG).show();
                        } else {
                            try {
                                final ThreeItemModel threeItem = ThreeItemModel.getThreeItemFromResponse(new JSONObject(response.body().string()));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displayResult(threeItem);

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void displayResult(ThreeItemModel threeItem) {
        feesAndGrowthAdapter.swapItems(threeItem.graphs);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }
}
package it.denning.navigation.dashboard.section4.profitloss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.ProfitLossDetailModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossDetailActivity extends GeneralActivity{
    @BindView(R.id.year_textView)
    TextView topYear;

    ProfitLossDetailApdater adapter;
    String curYear;
    ProfitLossDetailModel model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_profit_loss_detail);
        ButterKnife.bind(this);

        _url = getIntent().getStringExtra("api");
        parseURL();
        setupList();
        new FetchResult().execute();
    }

    @Override
    public void parseURL() {
        super.parseURL();
        int lastIndex = _url.lastIndexOf('/');
        baseUrl = _url.substring(0, lastIndex+1);
        curYear = _url.substring(lastIndex+1);
        topYear.setText(curYear);
    }

    @OnClick(R.id.prev_button)
    void didTapPrev() {
        curYear = Integer.toString(Integer.parseInt(curYear) - 1);
        topYear.setText(curYear);
        new FetchResult().execute();
    }

    @OnClick(R.id.next_button)
    void didTapNext() {
        curYear = Integer.toString(Integer.parseInt(curYear) + 1);
        topYear.setText(curYear);
        new FetchResult().execute();
    }

    void setupList() {
        adapter = new ProfitLossDetailApdater(null);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(adapter);

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    private class FetchResult extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url  = DISharedPreferences.getInstance(getApplicationContext()).getServerAPI() + baseUrl + curYear;
            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .addHeader("webuser-sessionid", DISharedPreferences.getInstance(getApplicationContext()).getSessionID())
                    .addHeader("webuser-id", DISharedPreferences.getInstance(getApplicationContext()).getEmail())
                    .build();

            try {
                Call call = client.newCall(request);
//                    request_tag = (String)call.request().tag();
                Response response = call.execute();
//                pd.dismiss();
                if (response != null) {
                    if (!response.isSuccessful()) {
                        Snackbar.make(linearLayout, response.message(), Snackbar.LENGTH_LONG).show();
                    } else {
                        JSONObject jsonObject = null;
                        try {
                           jsonObject = new JSONObject(response.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject != null) {
                            model = ProfitLossDetailModel.getProfitLossDetailFromResponse(jsonObject);

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayResult(model);
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void displayResult(ProfitLossDetailModel newList) {
        adapter.setItem(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }
}

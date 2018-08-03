package it.denning.navigation.dashboard.section4.profitloss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.ItemModel;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossActivity extends GeneralActivity  implements OnItemClickListener {
    ProfitLossAdapter profitLossAdapter;
    ArrayList<ItemModel> modelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_profit_loss);
        ButterKnife.bind(this);

        setupList();
        _url = getIntent().getStringExtra("api");
//        fetchTask();
        new FetchResult().execute();
    }

    void setupList() {
        modelArrayList = new ArrayList<>();
        profitLossAdapter = new ProfitLossAdapter(modelArrayList, getApplicationContext(), this);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(profitLossAdapter);

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
            String url  = DISharedPreferences.getInstance(getApplicationContext()).getServerAPI() + _url;
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
                        try {
                            final ArrayList<ItemModel> newmodelArrayList = ItemModel.getItemModelArrayFromResponse(new JSONArray(new JSONObject(response.body().string()).getString("items")));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayResult(newmodelArrayList);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    void displayResult(ArrayList<ItemModel> newList) {
        profitLossAdapter.swapItems(newList);

    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        ItemModel model = modelArrayList.get(position);
        Intent i = new Intent(this, ProfitLossDetailActivity.class);
        i.putExtra("api", model.api);
        startActivity(i);
    }
}

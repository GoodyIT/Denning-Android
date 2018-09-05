package it.denning.navigation.dashboard.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import okhttp3.OkHttpClient;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class GeneralActivity extends AppCompatActivity {
    private static final String STATE_SCROLL_POSITION = "ACTIVITY.STATE_SCROLL_POSITION";

    public String curBalanceFilter, curTopFilter = "", _url, baseUrl, filter;
    public String[] filterArray = {"Client", "Disbursement", "FD", "Advance", "Office", "Other"};
    public String[] balanceFilterArray = {"client", "disb", "fdeposit", "advance", "office", "other"};
    public String request_tag;
    public static OkHttpClient client = null;
    public LinearLayoutManager linearLayoutManager;


    @BindView(R.id.dashboard_list)
    public RecyclerView dashboardList;
    @BindView(R.id.dashboard_second_layout)
    public LinearLayout linearLayout;

    @BindView(R.id.toolbar_progressbar)
    ProgressBar progressBar;

    public void showActionBarProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hidewActionBarProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (client == null) {
            client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).retryOnConnectionFailure(true).writeTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
        }
        filter = "";
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        RecyclerView.LayoutManager lm = dashboardList.getLayoutManager();
        Parcelable scrollState = lm.onSaveInstanceState();
        outState.putParcelable(STATE_SCROLL_POSITION, scrollState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            dashboardList.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));
        }
    }

    public void parseURL() {
        int lastIndex = _url.lastIndexOf('/');
        curBalanceFilter = _url.substring(lastIndex+1);
        String _temp = _url.substring(0, lastIndex);
        lastIndex = _temp.lastIndexOf('/');
        curTopFilter = _temp.substring(lastIndex+1);
        baseUrl = _temp.substring(0, lastIndex);
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

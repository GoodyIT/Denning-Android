package it.denning.navigation.dashboard.section4.fileledgers;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.BankReconModel;
import it.denning.model.Ledger;
import it.denning.model.LedgerDetail;
import it.denning.model.NewLedgerModel;
import it.denning.navigation.dashboard.section2.CollectionAdapter;
import it.denning.navigation.dashboard.util.GeneralActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.accounts.transaction.TransactionDetailActivity;
import it.denning.search.utils.OnItemClickListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class FileLedgerDetailActivity extends GeneralActivity implements OnItemClickListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.tabs)
    TabLayout filterTabbar;
    @BindView(R.id.collection_subtitle)
    TextView subTitle;
    @BindView(R.id.footer_balance)
    TextView footerValue;
    @BindView(R.id.segmented)
    SegmentedGroup segmentedGroup;

    CollectionAdapter collectionAdapter;
    List<LedgerDetail> originalArrayList;
    BankReconModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_fileledger_detail);
        ButterKnife.bind(this);

        _url = getIntent().getStringExtra("api");
        Bundle bundle = getIntent().getExtras();
        model = (BankReconModel) bundle.getSerializable("model");

            segmentedGroup.setOnCheckedChangeListener(this);
        updateHeaderAndFooter();
        parseURL();
        setupList();
        setupFilter();

        loadBalance();
        fetchTask();
    }

    void updateHeaderAndFooter() {
        String _subTitle = "";
        if (model.accountNo.split("/").length == 2) {
            _subTitle = model.accountNo.split("/")[0];
        } else {
            _subTitle = model.accountNo;
        }
        subTitle.setText(_subTitle + " " + model.accountName);

        if (model.credit.equals("0.00")) {
            footerValue.setText(model.debit);
        } else {
            footerValue.setText(model.credit);
        }
    }

    public void loadBalance() {
        String url  = baseUrl + "/"+ curTopFilter;
        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                NewLedgerModel newLedgerModel = new Gson().fromJson(jsonElement, NewLedgerModel.class);
                updateLedgerBalance(newLedgerModel);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hidewActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void updateLedgerBalance(NewLedgerModel newModel) {
        for (Ledger ledger: newModel.ledgerArrayList) {
            if (ledger.accountName.toLowerCase().equals( curBalanceFilter)) {
                footerValue.setText(ledger.currentBalance);
                break;
            }
        }
    }

    void setupList() {
        collectionAdapter = new CollectionAdapter(new ArrayList<LedgerDetail>(), getApplicationContext(), this);
        linearLayoutManager  = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(collectionAdapter);

        dashboardList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setupFilter() {
        filterArray = new String[]{"All", "Client", "Disbursement", "FD", "Advance", "Other", "Receivable"};
        balanceFilterArray = new String[]{"all", "client", "disb", "fd", "advance", "other", "recv"};
        for (int i = 0; i < filterArray.length; i++) {
            filterTabbar.addTab(filterTabbar.newTab().setText(filterArray[i]));
            filterTabbar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    applyFilter(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
        for (int i = 0; i < balanceFilterArray.length; i++) {
            if (balanceFilterArray[i].equals(curBalanceFilter)) {
                filterTabbar.getTabAt(i).select();
            }
        }
    }

    void applyFilter(TabLayout.Tab tab) {
        curBalanceFilter = balanceFilterArray[tab.getPosition()];
        footerValue.setText("");
        fetchTask();
    }

    void fetchTask() {
        showActionBarProgress();
        String url  = baseUrl + "/"+ curTopFilter + "/"  + curBalanceFilter;

        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hidewActionBarProgress();
                LedgerDetail[] ledgerDetail = new Gson().fromJson(jsonElement, LedgerDetail[].class);
                originalArrayList = Arrays.asList(ledgerDetail);
                displayResult(Arrays.asList(ledgerDetail));
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hidewActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void displayResult(List<LedgerDetail> newList) {
        collectionAdapter.swapItems(newList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        hideKeyboard();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        LedgerDetail model = collectionAdapter.getModels().get(position);
        TransactionDetailActivity.start(this, model);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        ArrayList<LedgerDetail> newArray = new ArrayList<>();
        switch (checkedId) {
            case R.id.first_segment:
                newArray.addAll(originalArrayList);
                break;
            case R.id.second_segment:
                for (LedgerDetail obj : originalArrayList) {
                    if (obj.isDebit.equals("1")) {
                        newArray.add(obj);
                    }
                }
                break;
            case R.id.third_segment:
                for (LedgerDetail obj : originalArrayList) {
                    if (obj.isDebit.equals("0")) {
                        newArray.add(obj);
                    }
                }
                break;
        }
        collectionAdapter.swapItems(newArray);
    }
}

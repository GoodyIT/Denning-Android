package it.denning.search.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.Accounts;
import it.denning.model.Ledger;
import it.denning.model.LedgerDetail;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.accounts.transaction.TransactionDetailActivity;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 30/04/2017.
 */

public class AccountsDetailActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, OnItemClickListener{
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.account_detail_list)
    RecyclerView accountDetailList;
    @BindView(R.id.account_ledger_balance) TextView ledgerBalance;
    @BindView(R.id.account_detail_segmented)
    SegmentedGroup debitOrCreditFilter;
    @BindView(R.id.account_detail_layout)
    LinearLayout accountDetailLayout;
    @BindView(R.id.tablayout)
    TabLayout filterTabLayout;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    String accountName;
    List<LedgerDetail> LedgerDetailArrayList = new ArrayList<>();
    AccountsDetailAdapter LedgerDetailAdapter;
    Accounts accounts;
    Ledger selectedLedger;
    Integer selectedIndex;

    public static void start(Context context, String accountName) {
        Intent i = new Intent(context, AccountsDetailActivity.class);
        i.putExtra("accountName", accountName);
        context.startActivity(i);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_account_detail;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
        setupFilter();
        setupRecyclerView();

        getLedgerDetail();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.accounts_title);

        accounts = DISharedPreferences.accounts;
        accountName = getIntent().getStringExtra("accountName");
        for (int i = 0; i < accounts.ledgerArrayList.size(); i++) {
            selectedLedger = accounts.ledgerArrayList.get(i);
            if (accountName.equals(selectedLedger.accountName)) {

                selectedIndex = i;
                break;
            }
        }

        displayTitleInfo();
    }

    private void setupFilter() {
        for (int i = 0; i < accounts.ledgerArrayList.size(); i++) {
            filterTabLayout.addTab(filterTabLayout.newTab().setText(accounts.ledgerArrayList.get(i).accountName));
            filterTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    getAccountFromFilter(tab);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private void getAccountFromFilter(TabLayout.Tab tab) {
        selectedIndex = tab.getPosition();
        NetworkManager.getInstance().clearQueue();
        getLedgerDetail();
    }

    private void updateSearchResult() {
        LedgerDetailAdapter.updateAdapter(LedgerDetailArrayList);
    }

    private void manageResponse(JsonArray jsonArray) {
        hideActionBarProgress();
        LedgerDetail[] LedgerDetails = new Gson().fromJson(jsonArray, LedgerDetail[].class);
        LedgerDetailArrayList = Arrays.asList(LedgerDetails);
        updateSearchResult();
        displayTitleInfo();
    }

    private void getLedgerDetail() {
        Ledger ledger = accounts.ledgerArrayList.get(selectedIndex);
        String url= ledger.urlDetail;

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
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }


    private void setupRecyclerView() {
        LedgerDetailAdapter = new AccountsDetailAdapter(LedgerDetailArrayList, this, this);
        LinearLayoutManager accountDetailLayout = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        accountDetailList.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        accountDetailList.setLayoutManager(accountDetailLayout);
        accountDetailList.setHasFixedSize(true);
        accountDetailList.setItemAnimator(new DefaultItemAnimator());
        accountDetailList.setAdapter(LedgerDetailAdapter);
    }

    private void displayTitleInfo() {
        toolbarSubTitle.setText("(" + accounts.fileNo + ") (" + accounts.fileName + ")");
        ledgerBalance.setText(selectedLedger.getBalance());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        ArrayList<LedgerDetail> newArray = new ArrayList<>();
        switch (checkedId) {
            case R.id.account_filter_all:
                newArray.addAll(LedgerDetailArrayList);
                break;
            case R.id.account_filter_debit:
                for (LedgerDetail LedgerDetail : LedgerDetailArrayList) {
                    if (LedgerDetail.isDebit.equals("1")) {
                        newArray.add(LedgerDetail);
                    }
                }
                break;
            case R.id.account_filter_credit:
                for (LedgerDetail LedgerDetail : LedgerDetailArrayList) {
                    if (LedgerDetail.isDebit.equals("0")) {
                        newArray.add(LedgerDetail);
                    }
                }
                break;
        }
        LedgerDetailAdapter.updateAdapter(newArray);
    }

    @Override
    public void onClick(View view, int position) {
        LedgerDetail model = LedgerDetailAdapter.getModels().get(position);
        TransactionDetailActivity.start(this, model);
    }
}

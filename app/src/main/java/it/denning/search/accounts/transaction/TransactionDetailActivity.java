package it.denning.search.accounts.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;

import it.denning.R;
import it.denning.model.LedgerDetail;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 29/04/2017.
 */

public class TransactionDetailActivity extends MyBaseActivity{

    LedgerDetail model;

    public static void start(Context context, LedgerDetail model) {
        Intent i = new Intent(context, TransactionDetailActivity.class);
        i.putExtra("model", model);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.transaction_detail_title);
        model =  (LedgerDetail)getIntent().getSerializableExtra("model");
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        TransactionDetailAdapter adapter = new TransactionDetailAdapter(model, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

}

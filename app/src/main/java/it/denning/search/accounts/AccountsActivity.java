package it.denning.search.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;

import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.Accounts;
import it.denning.search.utils.OnDetailItemClickListener;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 29/04/2017.
 */

public class AccountsActivity extends MyBaseActivity implements OnDetailItemClickListener{

    Accounts accounts;

    public static void start(Context context) {
        Intent i = new Intent(context, AccountsActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.accounts_title));
        accounts = DISharedPreferences.accounts;

        if (accounts != null) {
            setupRecyclerView(accounts);
        }
    }

    private void setupRecyclerView(Accounts accounts) {
        AccountsAdapter adapter = new AccountsAdapter(accounts, this);
        adapter.setItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, String type, String code) {
        AccountsDetailActivity.start(this, type);
    }

}

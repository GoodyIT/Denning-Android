package it.denning.search.bank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.Bank;
import it.denning.model.LegalFirm;
import it.denning.model.MatterModel;
import it.denning.search.legal_firm.LegalFirmAdapter;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 27/04/2017.
 */

public class BankActivity extends MyBaseActivity {

    private int title;

    public static void start(Context context, int title) {
        Intent intent = new Intent(context, BankActivity.class);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        initActionBar();
    }

    private void initFields() {
        title = getIntent().getIntExtra("title", -1);
        toolbarTitle.setText(title);

        Bank bank = DISharedPreferences.bank;

        if (bank != null) {
            setupRecyclerView(bank);
            DISharedPreferences.bank = null;
        }
    }

    private void setupRecyclerView(Bank bank) {
        BankAdapter bankAdapter = new BankAdapter(bank, this);
        LinearLayoutManager bankLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bankAdapter);
    }
}

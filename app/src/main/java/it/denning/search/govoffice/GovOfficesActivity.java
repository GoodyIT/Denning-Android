package it.denning.search.govoffice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.Bank;
import it.denning.model.GovOffice;
import it.denning.search.bank.BankAdapter;

/**
 * Created by denningit on 28/04/2017.
 */

public class GovOfficesActivity extends AppCompatActivity {
    @BindView(R.id.search_bank_list)
    RecyclerView govOfficeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bank);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        GovOffice govOffice = (GovOffice) intent.getSerializableExtra("govOffice");

        if (govOffice != null) {
            displaySearchResult(govOffice);
        }
    }

    private void displaySearchResult(GovOffice govOffice) {
        GovOfficesAdapter govOfficesAdapter = new GovOfficesAdapter(govOffice, this);
        LinearLayoutManager bankLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);

        govOfficeList.setLayoutManager(bankLayoutManager);
        govOfficeList.setHasFixedSize(true);
        govOfficeList.setItemAnimator(new DefaultItemAnimator());
        try {
            govOfficeList.setAdapter(govOfficesAdapter);
        } catch (Exception e) {
            Log.i("", e.getLocalizedMessage());
        }

    }
}

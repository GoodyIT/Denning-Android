package it.denning.search.legal_firm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import it.denning.model.Contact;
import it.denning.model.LegalFirm;
import it.denning.model.MatterModel;
import it.denning.search.contact.SearchContactAdapter;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 26/04/2017.
 */

public class LegalFirmActivity extends MyBaseActivity {
    LegalFirmAdapter legalFirmAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, LegalFirmActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.legal_firm_title));
        Intent intent = getIntent();
        if (DISharedPreferences.legalFirm != null) {
            setupRecyclerView(DISharedPreferences.legalFirm);
        }
    }

    private void setupRecyclerView(LegalFirm legalFirm) {
        legalFirmAdapter = new LegalFirmAdapter(legalFirm, this);

        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(legalFirmAdapter);
    }
}

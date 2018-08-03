package it.denning.search.MatterCode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;

import it.denning.R;
import it.denning.model.MatterCodeModel;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2017-12-12.
 */

public class MatterCodeActivity extends MyBaseActivity {
    private MatterCodeModel matterCodeModel;

    public static void start(Context context, MatterCodeModel matterCodeModel) {
        Intent intent = new Intent(context, MatterCodeActivity.class);
        intent.putExtra("matterCodeModel", matterCodeModel);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.matter_code_title));
        matterCodeModel = (MatterCodeModel) getIntent().getSerializableExtra("matterCodeModel");
        if (matterCodeModel != null) {
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        MatterCodeAdapter adapter = new MatterCodeAdapter(matterCodeModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        linearLayoutManager.setItemPrefetchEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
    }
}

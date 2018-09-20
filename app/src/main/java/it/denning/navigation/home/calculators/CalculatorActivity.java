package it.denning.navigation.home.calculators;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DISharedPreferences;
import it.denning.model.News;
import it.denning.navigation.home.calculators.incometax.IncomeTaxActivity;
import it.denning.navigation.home.calculators.legalcost.LegalCostActivity;
import it.denning.navigation.home.calculators.loanamortisation.LoanAmortisationActivity;
import it.denning.navigation.home.calculators.realproperty.RealPropertyActivity;
import it.denning.navigation.home.news.NewsActivity;
import it.denning.navigation.home.news.NewsAdapter;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class CalculatorActivity extends BaseActivity implements OnItemClickListener {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private CalculatorAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CalculatorActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_general_customize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.calculators_title));
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new CalculatorAdapter(Arrays.asList(getResources().getStringArray(R.array.calculators)), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int position) {
        switch (position) {
            case 0:
                LegalCostActivity.start(this);
                break;
            case 1:
                IncomeTaxActivity.start(this);
                break;
            case 2:
                RealPropertyActivity.start(this);
                break;
            case 3:
                LoanAmortisationActivity.start(this);
                break;
        }
    }
}

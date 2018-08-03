package it.denning.navigation.add.utils.billselection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.BillModel;
import it.denning.model.TaxInvoiceItem;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class TaxInvoiceSelectionActivity extends BaseActivity implements OnItemClickListener {
    @BindView(R.id.tablayout)
    TabLayout filterTabbar;
    @BindView(R.id.toolbar_title)
    TextView    title;
    @BindView(R.id.recycler_list)
    public RecyclerView recyclerView;
    @BindView(R.id.amount_textView)
    TextView totalAmount;

    TaxInvoiceSelectionAdapter adapter;
    List<TaxInvoiceItem> modelArrayList = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);;
    public String[] filterArray ;
    private boolean hasCallback = false;
    BillModel billModel;
    int selectedIndex;
    List<String> listOfTotalPrice = new ArrayList<>();

    public static void start(Context context, String title, int index, BillModel model) {
        Intent intent = new Intent(context, TaxInvoiceSelectionActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("index", index);
        intent.putExtra("model", model);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_tax_selection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupList();
        setupFilter();
    }

    void setupList() {
        filterArray = new String[]{"Fees", "Disb GST", "Disb", "GST"};
        adapter = new TaxInvoiceSelectionAdapter(modelArrayList, getApplicationContext(), this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        dashboardList.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyboardUtils.hideKeyboard(v);
                return false;
            }
        });
        recyclerView.setItemViewCacheSize(0);
    }

    private void initFields() {
        hasCallback = getIntent().getBooleanExtra("hasCallback", false);
        selectedIndex = getIntent().getIntExtra("index", 0);
        billModel = (BillModel) getIntent().getSerializableExtra("model");
        String _title = getIntent().getStringExtra("title");
        title.setText(_title);
        listOfTotalPrice.add(billModel.analysis.decFees);
        listOfTotalPrice.add(billModel.analysis.decDisbGST);
        listOfTotalPrice.add(billModel.analysis.decDisb);
        listOfTotalPrice.add(billModel.analysis.decGST);

        updateData();
    }

    private void updateData() {
        totalAmount.setText(listOfTotalPrice.get(selectedIndex));
        switch (selectedIndex) {
            case 0:
                modelArrayList = billModel.analysis.Fees;
                break;
            case 1:
                modelArrayList = billModel.analysis.DisbGST;
                break;
            case 2:
                modelArrayList = billModel.analysis.Disb;
                break;
            case 3:
                modelArrayList = billModel.analysis.GST;
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void setupFilter() {
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
    }

    void applyFilter(TabLayout.Tab tab) {
        selectedIndex = tab.getPosition();
        updateData();
        adapter.clear();
        adapter.swapItems(modelArrayList);
    }

    @OnClick(R.id.back_btn)
    void goBack() {
        KeyboardUtils.hideKeyboard(this);
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        TaxInvoiceItem model = adapter.getModels().get(position);
        if (hasCallback) { // Called from Add Receipt
            Intent intent = new Intent();
            intent.putExtra("model", model);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else { // Open document

        }
    }

}

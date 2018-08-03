package it.denning.search.template;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.gigamole.navigationtabstrip.NavigationTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.CodeDescription;
import it.denning.model.Template;
import it.denning.search.utils.bootstrap.CustomBootstrapStyle;
import it.denning.search.utils.desc.GeneralDescActivity;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-24.
 */

public class TemplateActivity extends BaseActivity{
    private static final int templateRequestCode = 1;
    private static final int typeRequestCode = 2;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toolbar_sub_title)
    TextView toolbarSubTitle;

    @BindView(R.id.template_top)
    NavigationTabStrip topFilter;

    @BindView(R.id.category_btn)
    BootstrapButton categoryBtn;

    @BindView(R.id.type_btn)
    BootstrapButton typeBtn;

    @BindView(R.id.searchview)
    SearchView searchView;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private LinearLayoutManager linearLayoutManager;
    private TemplateAdapter adapter;
    private List<Template> modelList = new ArrayList<>();
    private String name, code, title;
    private String curCategory = "", curType = "";
    private int page = 1;


    public static void start(Context context, String code, String title) {
        Intent intent = new Intent(context, TemplateActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_template;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        setupRecyclerView();

        setupEndlessScroll();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.template_title));
        code = getIntent().getStringExtra("code");
        title = getIntent().getStringExtra("title");
        name = DIHelper.separateNameIntoTwo(title)[1];
        toolbarSubTitle.setText(name + "  " + code);

        topFilter.setTabIndex(0);

        categoryBtn.setBootstrapBrand(new CustomBootstrapStyle(this));
        typeBtn.setBootstrapBrand(new CustomBootstrapStyle(this));
    }

    private void setupRecyclerView() {
        adapter = new TemplateAdapter(modelList);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadTemplate();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadTemplate() {

    }

    @OnClick(R.id.category_btn)
    void selectCategory() {
        Intent i = new Intent(this, GeneralDescActivity.class);
        i.putExtra("title", "Category");
        i.putExtra("url", DIConstants.SEARCH_TEMPLATE_CATEGORY );
        startActivityForResult(i, templateRequestCode);
    }

    @OnClick(R.id.type_btn)
    void selectType() {
        if (curCategory.trim().length() == 0) {
            DIAlert.showSimpleAlert(this, R.string.alert_category_not_selected);
            return;
        }

        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.type_title);
        i.putExtra("url", DIConstants.SEARCH_TEMPLATE_TYPE + curCategory + "&search=");
        i.putExtra("code", "strTypeCode");
        i.putExtra("value", "strTypeName");
        startActivityForResult(i, typeRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == templateRequestCode) {
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("desc");
                curCategory = value;
                categoryBtn.setBootstrapText(new BootstrapText.Builder(this).addText(value + "{fa-chevron-down}").build());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == typeRequestCode) {
            if(resultCode == Activity.RESULT_OK){
                CodeDescription model= (CodeDescription) data.getSerializableExtra("model");
                curType = model.code;
                typeBtn.setMarkdownText(model.description);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}

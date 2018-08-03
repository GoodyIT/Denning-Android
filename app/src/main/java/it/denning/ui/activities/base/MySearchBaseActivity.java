package it.denning.ui.activities.base;

import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-12.
 */

public abstract class MySearchBaseActivity extends BaseActivity {
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.general_search)
    protected SearchView searchView;

    @OnClick(R.id.back_btn)
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_search_general;
    }
}

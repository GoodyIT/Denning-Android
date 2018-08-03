package it.denning.ui.activities.base;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.network.NetworkManager;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-12.
 */

public abstract class MyBaseActivity extends BaseActivity {
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    protected void onBack() {
        KeyboardUtils.hideKeyboard(this);
        NetworkManager.getInstance().clearQueue();
        finish();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_general;
    }
}

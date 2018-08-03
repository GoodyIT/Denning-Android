package it.denning.navigation.home.comingsoon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class ComingSoonActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_coming_soon;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ComingSoonActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.coming_soon));
    }
}

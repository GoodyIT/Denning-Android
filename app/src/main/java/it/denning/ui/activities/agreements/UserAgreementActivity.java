package it.denning.ui.activities.agreements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.Agreement;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.authorization.BaseAuthActivity;
import it.denning.utils.KeyboardUtils;

public class UserAgreementActivity extends BaseAuthActivity {
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 1000;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.user_agreement_textview)
    TextView userAgreementTextView;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UserAgreementActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, String content) {
        Intent intent = new Intent(context, UserAgreementActivity.class);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_user_agreement;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        initUserAgreementWebView();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.user_agreement_title);

        userAgreementTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void initUserAgreementWebView() {
        userAgreementTextView.setText(getIntent().getStringExtra("content"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (!DISharedPreferences.getInstance().isUserAgreement()) {
            getMenuInflater().inflate(R.menu.agreement, menu);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_agree) {
            updateAgreement();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAgreement() {
        final JsonObject param = new JsonObject();
        param.addProperty("email", "");
        param.addProperty("MAC", DIHelper.getMAC());
        param.addProperty("deviceName", DIHelper.getDeviceName());

        showProgress();
        NetworkManager.getInstance().sendPublicPutRequest(DIConstants.kDIAgreementUrl, param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageUpdateResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageError(String error) {
        hideProgress();
        DIAlert.showSimpleAlertWithCompletion(this, R.string.warning_title, error, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                finish();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private void manageUpdateResponse(JsonElement jsonElement) {
        hideProgress();
        Agreement agreement = new Gson().fromJson(jsonElement, Agreement.class);
        if (agreement.code.equals("200")) {
            DISharedPreferences.getInstance().setUserAgreement();
            gotoMainActivity();
        } else {

        }
    }

    private void gotoMainActivity() {
        MainActivity.start(UserAgreementActivity.this);
        finish();
    }
}
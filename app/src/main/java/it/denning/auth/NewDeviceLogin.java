package it.denning.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.chat.QBChatService;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.auth.branch.FirmBranchActivity;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 20/04/2017.
 */

public class NewDeviceLogin extends BaseActivity {
    protected @BindView(R.id.new_device_phone)
    TextView phoneNumberView;
    protected @BindView(R.id.new_device_tac)
    EditText tacInputView;
    protected @BindView(R.id.new_device_continue)
    Button continueBtn;
    protected @BindView(R.id.new_device_resend) Button resendBtn;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NewDeviceLogin.class);
        context.startActivity(intent);
    }

    private String email;

    @BindView(R.id.new_device_layout)
    RelativeLayout newDeviceLayout;

    @Override
    protected int getContentResId() {
        return R.layout.activity_new_device;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.new_device_title);

        email = getIntent().getStringExtra(DIConstants.ACTIVITY_BUNDLE_KEY);
    }

    @OnClick(R.id.new_device_continue)
    protected void continueLogin() {
        proceedTac();
    }

    @OnClick(R.id.new_device_resend)
    protected void resendTAC() {
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("hpNumber", DISharedPreferences.getInstance(this).getPhoneNumber());
        json.addProperty("reason", "from new device login");

        showProgress();

        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SMS_NEW, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                Snackbar.make(newDeviceLayout, "Successfully requested TAC code.", Snackbar.LENGTH_LONG).show();
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ;
            }
        });
    }

    public void proceedTac() {
        JsonObject json = new JsonObject();
        json.addProperty("email", DISharedPreferences.getInstance(this).getEmail());
        json.addProperty("activationCode", tacInputView.getText().toString());

        showProgress();

        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SMS_NEW, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonObject());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageResponse(JsonObject jsonObject) {
        hideProgress();
        FirmURLModel firmURLModel = new Gson().fromJson(jsonObject, FirmURLModel.class);
        DISharedPreferences.getInstance(this).saveUserInfoFromNewDeviceLogin(firmURLModel);
        if (firmURLModel.statusCode == 200) {
            manageUserType(firmURLModel);
        } else {
            ChangePassword.start(this);
        }
    }

    private void manageUserType(FirmURLModel firmURLModel) {
        DISharedPreferences.getInstance().saveUserInfoFromResponse(firmURLModel);
        DISharedPreferences.documentView = "nothing";
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            manageFirmURL(firmURLModel.catDenning);
        } else {
            // go to main activity - remove signin activity
            finish();
            MainActivity.start(this);
        }
    }

    private void manageFirmURL(List<FirmModel> firmArray) {
        if(firmArray.size() == 1) {
            DISharedPreferences.getInstance(this).saveServerAPI(firmArray.get(0));
            staffSignIn();
        } else {
            finish();
            FirmBranchActivity.start(this);
        }
    }

    private void staffSignIn() {
        NetworkManager.getInstance().staffSignIn(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                FirmURLModel firmURLModel = new Gson().fromJson(jsonElement.getAsJsonObject(), FirmURLModel.class);
                if (firmURLModel.statusCode == 200) {
                    DISharedPreferences.getInstance().saveUserInfoFromResponse(firmURLModel);
                    DISharedPreferences.getInstance().saveSessionID(firmURLModel.sessionID);
                    MainActivity.start(NewDeviceLogin.this);
                    finish();
                } else {
                    ErrorUtils.showError(NewDeviceLogin.this, getApplicationContext().getResources().getString(R.string.alert_no_access_to_firm));
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(NewDeviceLogin.this, error);
            }
        });
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(getApplicationContext(), error);
        if (QBChatService.getInstance().isLoggedIn()) {
            try {
                QBChatService.getInstance().logout();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }
}

package it.denning.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.jivesoftware.smack.SmackException;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.auth.branch.FirmBranchActivity;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.helpers.ServiceManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by denningit on 20/04/2017.
 */

public class ChangePassword extends BaseActivity {
    protected @BindView(R.id.change_password_new)
    EditText newPasswordView;

    protected @BindView(R.id.change_password_confirm)
    EditText confirmPasswordView;

    protected @BindView(R.id.change_password_continue)
    Button continueBtn;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private RelativeLayout changePasswordLayout;
    private MaterialDialog dialog;

    public static void start(Context context) {
        Intent myIntent = new Intent(context, ChangePassword.class);
        context.startActivity(myIntent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.change_password_title);

        changePasswordLayout = (RelativeLayout) findViewById(R.id.change_password_layout);
    }

    @OnClick(R.id.change_password_continue)
    void changePassword() {
        if (newPasswordView.getText().toString().matches("\\w*") || confirmPasswordView.getText().toString().trim().length() == 0) {
            Snackbar.make(changePasswordLayout, "You cannot set empty password", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!newPasswordView.getText().toString().equals(confirmPasswordView.getText().toString())) {
            Snackbar.make(changePasswordLayout, "The passwords don't match", Snackbar.LENGTH_LONG).show();
            return;
        }
        _changePassword();
    }

    private void manageResponse(JsonObject jsonObject) {
        FirmURLModel firmURLModel = new Gson().fromJson(jsonObject, FirmURLModel.class);
        DISharedPreferences.getInstance(this).saveUserInfoFromNewDeviceLogin(firmURLModel);
        DISharedPreferences.getInstance().saveUserPassword(newPasswordView.getText().toString());
        if (firmURLModel.statusCode == 200) {
            manageUserType(firmURLModel);
        } else {
            MainActivity.start(this);
        }
    }

    private void manageUserType(FirmURLModel firmURLModel) {
        DISharedPreferences.documentView = "nothing";
        if (DISharedPreferences.getInstance(this).getUserType().equals(DISharedPreferences.STAFF_USER)) {
            manageFirmURL(firmURLModel.catDenning);
        } else {
            // go to main activity - remove signin activity
            MainActivity.start(this);
        }
    }

    private void manageFirmURL(List<FirmModel> firmArray) {
        if(firmArray.size() == 1) {
            DISharedPreferences.getInstance(this).saveServerAPI(firmArray.get(0));
            staffSignIn();
        } else {
            FirmBranchActivity.start(this);
        }
    }

    private void staffSignIn() {
        NetworkManager.getInstance().staffSignIn(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                FirmURLModel firmURLModel = new Gson().fromJson(jsonElement.getAsJsonObject(), FirmURLModel.class);
                if (firmURLModel.statusCode == 200) {
                    DISharedPreferences.getInstance().saveSessionID(firmURLModel.sessionID);
                    MainActivity.start(getApplicationContext());
                } else {
                    ErrorUtils.showError(getApplicationContext(), getApplicationContext().getResources().getString(R.string.alert_no_access_to_firm));
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void manageErrors(String error) {
        dialog.dismiss();
        ErrorUtils.showError(ChangePassword.this, error);
        if (QBChatService.getInstance().isLoggedIn()) {
            try {
                QBChatService.getInstance().logout();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    private void _changePassword() {
        JsonObject json = new JsonObject();
        json.addProperty("email", DISharedPreferences.getInstance().getEmail());
        json.addProperty("password", newPasswordView.getText().toString());

        dialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SET_NEW_PASSWORD, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonObject());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageErrors(error);
            }
        });
    }
}

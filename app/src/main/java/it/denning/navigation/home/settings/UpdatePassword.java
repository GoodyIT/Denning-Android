package it.denning.navigation.home.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.chat.QBChatService;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.jivesoftware.smack.SmackException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class UpdatePassword extends BaseActivity {

    @BindView(R.id.change_password_old)
    EditText oldPasswordView;

    protected @BindView(R.id.change_password_new)
    EditText newPasswordView;

    protected @BindView(R.id.change_password_confirm)
    EditText confirmPasswordView;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.right_btn)
    protected Button btnRight;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private RelativeLayout changePasswordLayout;
    private MaterialDialog dialog;

    public static void start(Context context) {
        Intent myIntent = new Intent(context, UpdatePassword.class);
        context.startActivity(myIntent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_update_password;
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
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("Change");
        btnRight.setTextColor(getResources().getColor(R.color.black));
    }

    @OnClick(R.id.right_btn)
    void changePassword() {
        if (newPasswordView.getText().toString().matches("\\w*") || confirmPasswordView.getText().toString().trim().length() == 0) {
            Snackbar.make(changePasswordLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (oldPasswordView.getText().toString().equals(newPasswordView.getText().toString())) {
            Snackbar.make(changePasswordLayout, "Wrong old password", Snackbar.LENGTH_LONG).show();
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
        finish();
    }

    private void manageErrors(String error) {
        dialog.dismiss();
        ErrorUtils.showError(UpdatePassword.this, error);
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

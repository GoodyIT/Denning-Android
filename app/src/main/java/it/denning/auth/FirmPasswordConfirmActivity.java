package it.denning.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.DocumentModel;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.navigation.home.upload.UploadActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.document.DocumentActivity;
import it.denning.search.document.PersonalDocumentActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 20/04/2017.
 */

public class FirmPasswordConfirmActivity extends BaseActivity {
    protected @BindView(R.id.new_device_phone)
    TextView branchView;
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

    public static void start(Context context, String branch, String firmName) {
        Intent intent = new Intent(context, FirmPasswordConfirmActivity.class);
        intent.putExtra("branch", branch);
        intent.putExtra("firmName", firmName);
        context.startActivity(intent);
    }

    @BindView(R.id.new_device_layout)
    RelativeLayout newDeviceLayout;

    @Override
    protected int getContentResId() {
        return R.layout.activity_firm_password_confirm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.password_confirm_title);
        String branch = getIntent().getStringExtra("branch");
        String firmName = getIntent().getStringExtra("firmName");
        branchView.setText(branch + "(" + firmName + ")");
    }

    @OnClick(R.id.new_device_continue)
    protected void continueLogin() {
        proceedTac();
    }

    @OnClick(R.id.new_device_resend)
    protected void resendTAC() {
        DIAlert.showSimpleAlert(this, R.string.alert_resend_passcode);
    }

    public void proceedTac() {
        JsonObject json = new JsonObject();
        json.addProperty("email", DISharedPreferences.getInstance(this).getEmail());
        json.addProperty("password", tacInputView.getText().toString());
        json.addProperty("sessionID", DISharedPreferences.getInstance().getSessionID());

        showProgress();
        NetworkManager.getInstance().sendPrivatePostRequest(DIConstants.CLIENT_FIRST_SIGNIN_URL, json, new CompositeCompletion() {
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

    private DocumentModel saveSessionAndGetFolder(JsonObject response) {
        DISharedPreferences.getInstance(this).saveSessionID(response.get("sessionID").getAsString());
        return new Gson().fromJson(response, DocumentModel.class);
    }

    private void manageResponse(JsonObject jsonObject) {
        hideProgress();
        DocumentModel documentModel = saveSessionAndGetFolder(jsonObject);
        if (DISharedPreferences.documentView.equals("upload")) {
            UploadActivity.start(FirmPasswordConfirmActivity.this, null, R.string.client_upload_title);
        } else {
            if (documentModel.folders.size() == 0) {
                ErrorUtils.showError(FirmPasswordConfirmActivity.this, "There is no shared folder for you");
            } else {
                DISharedPreferences.documentModel = documentModel;
                PersonalDocumentActivity.start(FirmPasswordConfirmActivity.this, "Documents");
            }
        }
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(getApplicationContext(), error);
    }
}

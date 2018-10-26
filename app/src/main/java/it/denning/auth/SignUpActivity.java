package it.denning.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.qb.commands.rest.QBSignUpCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.LegalFirm;
import it.denning.model.StaffModel;
import it.denning.navigation.add.utils.solicitorlist.SolicitorListActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.authorization.BaseAuthActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.MediaUtils;
import it.denning.utils.helpers.ServiceManager;
import rx.Subscriber;

/**
 * Created by denningit on 03/05/2017.
 */

public class SignUpActivity extends BaseAuthActivity {
    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.signup_username)
    EditText userName;
    @BindView(R.id.signup_email) EditText userEmail;
    @BindView(R.id.signup_phone) EditText userPhone;
    @BindView(R.id.signup_lawyer)
    CheckBox isLawyerChk;
    @BindView(R.id.signup_firm_btn) Button firmSelectBtn;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.signup_layout)
    RelativeLayout signupLayout;
    StaffModel selectedLawfirm;
    private SignUpSuccessAction signUpSuccessAction;
    private UpdateUserSuccessAction updateUserSuccessAction;
    private int MY_REQUEST_CODE = 1;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private QBUser qbUser;

    @Override
    protected int getContentResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        initFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addActions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeActions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DIConstants.REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                selectedLawfirm = (StaffModel)data.getSerializableExtra("lawfirm");
                // do something with the result
                firmSelectBtn.setText(selectedLawfirm.name);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    void initFields() {
        selectedLawfirm = null;

        signUpSuccessAction = new SignUpSuccessAction();
        updateUserSuccessAction = new UpdateUserSuccessAction();

        firmSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLawfirm();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        ccp.registerCarrierNumberEditText(userPhone);
    }

    private void selectLawfirm() {
        Intent intent = new Intent(this, LawfirmActivity.class);
        startActivityForResult(intent, DIConstants.REQUEST_CODE);
    }

    private void addActions() {
        addAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION, signUpSuccessAction);
        addAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION, updateUserSuccessAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.SIGNUP_SUCCESS_ACTION);
        removeAction(QBServiceConsts.UPDATE_USER_SUCCESS_ACTION);

        updateBroadcastActionList();
    }

    private void signup() {
        if (userEmail.getText().toString().trim().length() == 0 || userName.getText().toString().trim().length() == 0 || userPhone.getText().toString().trim().length() == 0) {
            Snackbar.make(signupLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (isLawyerChk.isChecked() && selectedLawfirm == null) {
            Snackbar.make(signupLayout, "Please select lawfirm you belong to if you are an laywer.", Snackbar.LENGTH_LONG).show();
            return;
        }

        String phone = ccp.getFullNumberWithPlus();
        final JsonObject json = new JsonObject();
        json.addProperty("email", userEmail.getText().toString());
        json.addProperty("name", userName.getText().toString());
        json.addProperty("hpNumber", phone);
        json.addProperty("isLawyer", isLawyerChk.isChecked());
        if (isLawyerChk.isChecked()) {
            json.addProperty("firmCode", selectedLawfirm.code);
        } else {
            json.addProperty("firmCode", 0);
        }

        showProgress();
        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SIGNUP_URL, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement.getAsJsonObject());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(SignUpActivity.this, error);
            }
        });
    }

    private void manageResponse(JsonObject jsonObject) {
        DISharedPreferences.getInstance(this).saveUserEmail(userEmail.getText().toString());
        signupQB(userName.getText().toString(), userEmail.getText().toString());
    }

    private void signupQB(String name, String email) {
        qbUser = new QBUser();
        qbUser.setFullName(name);
        qbUser.setEmail(email);
        qbUser.setPassword(DIConstants.QB_PASSWORD);

        appSharedHelper.saveUsersImportInitialized(false);
        DataManager.getInstance().clearAllTables();
//        Uri uri = Uri.parse("android.resource://it.denning/raw/ic_launcher");
//        final File file = MediaUtils.getCreatedFileFromUri(uri);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                QBSignUpCommand.start(SignUpActivity.this, qbUser, null);
//            }
//        });
        QBUsers.signUpSignInTask(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {
                hideProgress();
                appSharedHelper.saveUsersImportInitialized(false);
                gotoMain(user);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgress();
                ErrorUtils.showError(SignUpActivity.this, e.getMessage());
            }
        });
    }

    protected void performUpdateUserSuccessAction(Bundle bundle) {
        QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
        gotoMain(user);
    }

    protected void gotoMain(QBUser user) {
        appSharedHelper.saveFirstAuth(true);
        appSharedHelper.saveSavedRememberMe(true);
        startMainActivity(user, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                SignInActivity.start(SignUpActivity.this);
                finish();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {

            }
        });

        // senalyticsHelper.pushAnalyticsData(SignUpActivity.this, user, "User Sign Up");
    }

    private void _updateUser(QBUser user, File file) {
        ServiceManager.getInstance().updateUser(user, file).subscribe(new Subscriber<QMUser>() {
            @Override
            public void onCompleted() {
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
            }

            @Override
            public void onNext(QMUser qmUser) {
                hideProgress();
                gotoMain(qmUser);
                finish();
            }
        });
    }

    private void performSignUpSuccessAction(Bundle bundle) throws IOException {
        File image = (File) bundle.getSerializable(QBServiceConsts.EXTRA_FILE);
        QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
       // _updateUser(user, image);
        hideProgress();
        gotoMain(user);
        finish();
    }

    private class SignUpSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            appSharedHelper.saveUsersImportInitialized(false);
            performSignUpSuccessAction(bundle);
//            final QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    gotoMain(user);
//                }
//            });
        }
    }

    private class UpdateUserSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            performUpdateUserSuccessAction(bundle);
        }
    }
}

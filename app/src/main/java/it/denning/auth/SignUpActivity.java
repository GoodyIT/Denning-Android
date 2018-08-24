package it.denning.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.qb.commands.rest.QBSignUpCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.Accounts;
import it.denning.model.LegalFirm;
import it.denning.search.accounts.AccountsActivity;
import it.denning.ui.activities.authorization.BaseAuthActivity;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.helpers.GoogleAnalyticsHelper;
import it.denning.utils.helpers.ServiceManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    @BindView(R.id.signup_phonecode) Button phoneCodeBtn;

    @BindView(R.id.signup_layout)
    RelativeLayout signupLayout;
    LegalFirm selectedLawfirm;
    private SignUpSuccessAction signUpSuccessAction;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType jsonType
            = MediaType.parse("application/json; charset=utf-8");
    private QBUser qbUser;

    @Override
    protected int getContentResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        selectedLawfirm = null;

        signUpSuccessAction = new SignUpSuccessAction();

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DIConstants.REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                selectedLawfirm = (LegalFirm)data.getSerializableExtra("lawfirm");
                // do something with the result
                firmSelectBtn.setText(selectedLawfirm.name);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    private void selectLawfirm() {
        Intent intent = new Intent(this, LawfirmActivity.class);
        intent.putExtra("title", R.string.select_firm);
        startActivityForResult(intent, DIConstants.REQUEST_CODE);
    }

    private void signup() {
        if (userEmail.getText().toString().matches("\\w*") || userName.getText().toString().matches("\\w*") || userPhone.getText().toString().trim().length() == 0) {
            Snackbar.make(signupLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }

        showProgress();
        new signupTask().execute();
    }

    private class signupTask extends AsyncTask<String, Void, String> {

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {

            proceedSignup();

            return "";
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private void proceedSignup() {
        String phone = "+60" + userPhone.getText().toString();
        JsonObject json = new JsonObject();
        json.addProperty("email", userEmail.getText().toString());
        json.addProperty("name", userName.getText().toString());
        json.addProperty("hpNumber", phone);
        json.addProperty("isLawyer", isLawyerChk.isChecked());
        json.addProperty("firmCode", selectedLawfirm.code);
        json.addProperty("ipWAN", DIHelper.getIPWAN());
        json.addProperty("ipLAN", DIHelper.getIPLAN());
        json.addProperty("OS", DIHelper.getOS());
        json.addProperty("device", DIHelper.getDevice());
        json.addProperty("deviceName", DIHelper.getDeviceName());
        json.addProperty("MAC", DIHelper.getMAC(this));
        Request request = new Request.Builder()
                .url(DIConstants.AUTH_SIGNUP_URL)
                .header("Content-Type", "application/json")
                .addHeader("webuser-sessionid", "{334E910C-CC68-4784-9047-0F23D37C9CF9}")
                .addHeader("webuser-id", "SkySea@denning.com.my")
                .post(RequestBody.create(jsonType, json.toString()))
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.isSuccessful()) {
            hideProgress();

            final Response myResponse = response;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ErrorUtils.showError(getApplicationContext(), myResponse.message());
                }
            });
        } else {

            DISharedPreferences.getInstance(this).saveUserEmail(userEmail.getText().toString());
            signupQB(userName.getText().toString(), userEmail.getText().toString());
        }
    }

    private void signupQB(String name, String email) {
        qbUser.setFullName(name);
        qbUser.setEmail(email);
        qbUser.setPassword(DIConstants.QB_PASSWORD);

        appSharedHelper.saveUsersImportInitialized(false);
        DataManager.getInstance().clearAllTables();
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              QBSignUpCommand.start(SignUpActivity.this, qbUser, null);
                          }
                      });
    }

    protected void performUpdateUserSuccessAction(Bundle bundle) {
        QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
        appSharedHelper.saveFirstAuth(true);
        appSharedHelper.saveSavedRememberMe(true);
        startMainActivity(user, null);

        // send analytics data
        GoogleAnalyticsHelper.pushAnalyticsData(SignUpActivity.this, user, "User Sign Up");
    }

    private void performSignUpSuccessAction(Bundle bundle) {
        File image = (File) bundle.getSerializable(QBServiceConsts.EXTRA_FILE);
        QBUser user = (QBUser) bundle.getSerializable(QBServiceConsts.EXTRA_USER);
        ServiceManager.getInstance().updateUser(user, image).subscribe(new Subscriber<QMUser>() {
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
                finish();
            }
        });
    }

    private class SignUpSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            appSharedHelper.saveUsersImportInitialized(false);
            performSignUpSuccessAction(bundle);
        }
    }

    private class UpdateUserSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            performUpdateUserSuccessAction(bundle);
        }
    }
}

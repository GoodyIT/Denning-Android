package it.denning.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
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
import it.denning.general.MyCallbackInterface;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.authorization.BaseAuthActivity;
import it.denning.ui.activities.settings.SettingsActivity;
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

public class SignInActivity extends BaseAuthActivity {
    protected @BindView(R.id.signin_email)
    EditText emailEditText;
    protected @BindView(R.id.signin_password)
    EditText passwordEditTExt;
    protected RelativeLayout signinLayout;
    protected @BindView(R.id.signin_signinbtn)
    Button signinBtn;
    @BindView(R.id.signin_signupbtn) Button signupBtn;
    protected @BindView(R.id.signin_forgetpasswordBtn) Button forgetPasswordBtn;

    @Override
    public void onBackPressed() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @OnClick(R.id.btn_back)
    void onBackToMain() {
        onBackPressed();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        signinLayout = (RelativeLayout) findViewById(R.id.login_layout);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });

        // set the previous sign in email
        emailEditText.setText(DISharedPreferences.getInstance(this).getEmail());
    }

    public void forgetPassword() {
        Intent myIntent = new Intent(this, ForgetPassword.class);
        startActivity(myIntent);
    }

    public void signup() {
        Intent myIntent = new Intent(this, SignUpActivity.class);
        startActivity(myIntent);
    }

    public void signin() {
        KeyboardUtils.hideKeyboard(this);
        String email = emailEditText.getText().toString();
        String password = passwordEditTExt.getText().toString();
        DISharedPreferences.getInstance().saveUserPassword(password);
        if (email.matches("\\w*") || password.trim().length() == 0) {
            Snackbar.make(signinLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }
        showProgress();

        final JsonObject param = new JsonObject();
        param.addProperty("email", email);
        param.addProperty("password", password);

        if (isChatInitializedAndUserLoggedIn()) {
            ServiceManager.getInstance().logout(new Subscriber<Void>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    ErrorUtils.showError(SignInActivity.this, e);
                    hideProgress();
                }

                @Override
                public void onNext(Void aVoid) {
                    _signin(param);
                }
            });
        } else {
            _signin(param);
        }
    }

    void _signin(JsonObject param) {
        NetworkManager.getInstance().mainLogin(param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                processAfterLogin(jsonElement.getAsJsonObject(), emailEditText.getText().toString());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(SignInActivity.this, error);
            }
        });
    }

    void processAfterLogin(final JsonObject jsonObject, final String email) {
        boolean ownerUser =  QBSessionManager.getInstance().getSessionParameters() != null && email.equals(QBSessionManager.getInstance().getSessionParameters().getUserEmail());
        if (!ownerUser) {
            DataManager.getInstance().clearAllTables();
        }
        login(email, DIConstants.QB_PASSWORD, new MyCallbackInterface(){
            @Override
            public void nextFunction() {
                gotoNextStepAfterLogin(jsonObject);
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    void gotoNextStepAfterLogin(JsonObject jsonObject) {
        hideProgress();
        FirmURLModel firmURLModel = new Gson().fromJson(jsonObject, FirmURLModel.class);
        DISharedPreferences.getInstance().saveUserInfoFromResponse(firmURLModel);
        DISharedPreferences.getInstance().saveUserPassword(passwordEditTExt.getText().toString());
        if (firmURLModel.statusCode == 250) {
            NewDeviceLogin.start(this);
        } else if (firmURLModel.statusCode  == 280) {
           ChangePassword.start(this);
        } else {
            manageUserType(firmURLModel);
        }
    }

    private void manageUserType(FirmURLModel firmURLModel) {
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
                    MainActivity.start(SignInActivity.this);
                } else {
                    ErrorUtils.showError(SignInActivity.this, getApplicationContext().getResources().getString(R.string.alert_no_access_to_firm));
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(SignInActivity.this, error);
            }
        });
    }
}

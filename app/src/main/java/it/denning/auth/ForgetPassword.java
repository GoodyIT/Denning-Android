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
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.utils.KeyboardUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by denningit on 21/04/2017.
 */

public class ForgetPassword extends AppCompatActivity {
    RelativeLayout forgetLayout;
    @BindView(R.id.forgot_requestbtn)
    Button requestSMSBtn;
    @BindView(R.id.forget_TAC)
    EditText TACEditText;
    @BindView(R.id.forgot_verifybtn) Button verifyBtn;
    @BindView(R.id.forget_phone) EditText phoneEditText;
    @BindView(R.id.forget_email) EditText emailEditText;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

        forgetLayout = (RelativeLayout) findViewById(R.id.forget_layout);

        requestSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSMS();
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTAC();
            }
        });

        // set the previous sign in email
        emailEditText.setText(DISharedPreferences.getInstance(this).getEmail());
    }

    private void requestSMS() {
        if (phoneEditText.getText().toString().matches("\\w*") || emailEditText.getText().toString().trim().length() == 0) {
            Snackbar.make(forgetLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }

        proceedRequestSMS();
    }

    private void proceedRequestSMS() {
        JsonObject json = new JsonObject();
        json.addProperty("email", emailEditText.getText().toString());
        json.addProperty("hpNumber", phoneEditText.getText().toString());
        json.addProperty("reason", "from Forget Password form");

        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SIGNIN_URL, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                Snackbar.make(forgetLayout, "SMS is sent to your phone", Snackbar.LENGTH_LONG).show();
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void verifyTAC() {
        if (phoneEditText.getText().toString().matches("\\w*") || emailEditText.getText().toString().trim().length() == 0) {
            Snackbar.make(forgetLayout, "Please input the all the fields", Snackbar.LENGTH_LONG).show();
            return;
        }

        proceedVerifyTAC();
    }


    private void proceedVerifyTAC() {
        JsonObject json = new JsonObject();
        json.addProperty("email", emailEditText.getText().toString());
        json.addProperty("hpNumber", phoneEditText.getText().toString());
        json.addProperty("activationCode", TACEditText.getText().toString());

        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.AUTH_SIGNIN_URL, json, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                Snackbar.make(forgetLayout, "Email is sent", Snackbar.LENGTH_LONG).show();
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });

    }
}

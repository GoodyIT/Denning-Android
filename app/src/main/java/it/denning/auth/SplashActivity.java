package it.denning.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.MyCallbackInterface;
import it.denning.model.Agreement;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.agreements.UserAgreementActivity;
import it.denning.ui.activities.authorization.BaseAuthActivity;

public class SplashActivity extends BaseAuthActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 2000;

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        loadAgreement();
    }

    private void loadAgreement() {
        final JsonObject param = new JsonObject();
        param.addProperty("email", "");
        param.addProperty("MAC", DIHelper.getMAC(this));
        param.addProperty("deviceName", DIHelper.getDeviceName());

//        showProgress();
        NetworkManager.getInstance().sendPublicPostRequest(DIConstants.kDIAgreementUrl, param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageError(String error) {
//        hideProgress();
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

    private void manageResponse(JsonElement jsonElement) {
//        hideProgress();
        Agreement agreement = new Gson().fromJson(jsonElement, Agreement.class);
        if (agreement.code.equals("200")) {
            gotoMainActivity();
        } else {
            UserAgreementActivity.start(this, agreement.strItemDescription, "splash");
            finish();
        }
    }

    private void gotoMainActivity() {
        Log.v(TAG, "startMainActivity();");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.start(SplashActivity.this);
                finish();
            }
        }, DELAY_FOR_OPENING_LANDING_ACTIVITY);
    }
}
package it.denning.general;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonElement;

import it.denning.R;
import it.denning.auth.SignInActivity;
import it.denning.ui.activities.authorization.LoginActivity;

/**
 * Created by denningit on 2018-01-12.
 */

public class DIAlert {
    public static void showSimpleAlertWithCompletion(Context context, int title, int message, final MyCallbackInterface callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(R.string.dlg_ok)
                .icon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if (callback != null) {
                            callback.nextFunction();
                        }
                    }
                })
                .show();
    }

    public static void showConfirm(Context context, int title, int message, final MyCallbackInterface callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(R.string.dlg_ok)
                .negativeText(R.string.dlg_cancel)
                .icon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if (callback != null) {
                            callback.nextFunction();
                        }
                    }
                })
                .show();
    }

    public static void showSimpleAlertWithCompletion(Context context, int title, String message, final MyCallbackInterface callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(R.string.dlg_ok)
                .icon(context.getResources().getDrawable(R.mipmap.ic_launcher))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if (callback != null) {
                            callback.nextFunction();
                        }
                    }
                })
                .show();
    }

    public static void showSimpleAlertWithCompletion(Context context, int message, final MyCallbackInterface callback) {
        showSimpleAlertWithCompletion(context, R.string.app_name, message, callback);
    }

    public static void showSimpleAlert(Context context, int title, int message) {
        showSimpleAlertWithCompletion(context, title, message, null);
    }

    public static void showSimpleAlert(Context context, int title, String message) {
        showSimpleAlertWithCompletion(context, title, message, null);
    }

    public static void showSimpleAlertAndGotoLogin(final Context context, int title, int message) {
        showSimpleAlertWithCompletion(context, title, message, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                SignInActivity.start(context);
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {

            }
        });
    }

    public static void showSimpleAlert(Context context, int message) {
        showSimpleAlertWithCompletion(context, message, null);
    }

    public static void showSimpleAlert(Context context, String message) {
        showSimpleAlert(context, R.string.app_name, message);
    }
}

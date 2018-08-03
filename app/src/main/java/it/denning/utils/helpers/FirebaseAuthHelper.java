package it.denning.utils.helpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.quickblox.q_municate_core.utils.ConstsCore;

import java.util.Collections;

import it.denning.R;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class FirebaseAuthHelper {

    private static final String TAG = FirebaseAuthHelper.class.getSimpleName();

    public static final int RC_SIGN_IN = 456;
    public static final String EXTRA_FIREBASE_ACCESS_TOKEN = "extra_firebase_access_token";

    public void loginByPhone(Activity activity) {
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                        .setTheme(R.style.FirebaseStyle)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void getIdTokenForCurrentUser(final RequestFirebaseIdTokenCallback callback) {
        if (getCurrentFirebaseUser() == null) {
            Log.v(TAG, "Getting Token error. ERROR = Current Firebse User is null");
            SharedHelper.getInstance().saveFirebaseToken(null);
            callback.onError(new NullPointerException("Current Firebse User is null"));
            return;
        }

        getCurrentFirebaseUser().getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String accessToken = task.getResult().getToken();
                    Log.v(TAG, "Token got successfully. TOKEN = " + accessToken);
                    SharedHelper.getInstance().saveFirebaseToken(accessToken);
                    callback.onSuccess(accessToken);
                } else {
                    Log.v(TAG, "Getting Token error. ERROR = " + task.getException().getMessage());
                    callback.onError(task.getException());
                }
            }
        });
    }

    public static void refreshInternalFirebaseToken() {
        Log.i(TAG, "refreshInternalFirebaseToken() synchronously start " + new Date(System.currentTimeMillis()));
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        getCurrentFirebaseUser().getIdToken(false).addOnCompleteListener(new Executor(){
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        }, new OnCompleteListener<GetTokenResult>() {
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String accessToken = task.getResult().getToken();
                    Log.v(TAG, "Token got successfully. TOKEN = " + accessToken);
                    SharedHelper.getInstance().saveFirebaseToken(accessToken);
                } else {
                    Log.v(TAG, "Getting Token error. ERROR = " + task.getException().getMessage());
                }

                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(ConstsCore.HTTP_TIMEOUT_IN_SECONDS, TimeUnit.MILLISECONDS);
            Log.i(TAG, "refreshInternalFirebaseToken() synchronously DONE " + new Date(System.currentTimeMillis()));
        } catch (InterruptedException e) {
            Log.i(TAG, "refreshInternalFirebaseToken() synchronously DONE_BY_TIMEOUT " + new Date(System.currentTimeMillis()));
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedHelper.getInstance().saveFirebaseToken(null);
    }

    public interface RequestFirebaseIdTokenCallback {

        void onSuccess(String accessToken);

        void onError(Exception e);
    }
}

package it.denning.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.helper.Lo;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLogoutAndDestroyChatCommand;

import it.denning.ui.activities.base.BaseActivity;

public class ActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityLifecycleHandler.class.getSimpleName();

    private int numberOfActivitiesInForeground;
    private boolean chatDestroyed = false;

    @SuppressLint("LongLogTag")
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("ActivityLifecycleHandler", "onActivityCreated " + activity.getClass().getSimpleName());
    }

    @SuppressLint("LongLogTag")
    public void onActivityStarted(Activity activity) {
        Log.d("ActivityLifecycleHandler", "onActivityStarted " + activity.getClass().getSimpleName());
        boolean activityLogeable = isActivityLogeable(activity);
        chatDestroyed = chatDestroyed && !isLoggedIn();
        Log.d(TAG, "onActivityStarted , chatDestroyed=" + chatDestroyed + ", numberOfActivitiesInForeground= "+numberOfActivitiesInForeground);
        if (numberOfActivitiesInForeground == 0 && activityLogeable) {
            AppSession.getSession().updateState(AppSession.ChatState.FOREGROUND);
            if (chatDestroyed) {
                boolean isLoggedIn = AppSession.getSession().isLoggedIn();
                Log.d(TAG, "isSessionExist()" + isLoggedIn);
                boolean canLogin = chatDestroyed && isLoggedIn;
                boolean networkAvailable = ((BaseActivity) activity).isNetworkAvailable();
                Log.d(TAG, "networkAvailable" + networkAvailable);
                if (canLogin) {
                    if (QBProvider.FIREBASE_PHONE.equals(QBSessionManager.getInstance().getSessionParameters().getSocialProvider())
                            && !QBSessionManager.getInstance().isValidActiveSession()){

                        ((BaseActivity) activity).renewFirebaseToken();
                    }

                    QBLoginChatCompositeCommand.start(activity);
                }
            }
        }

        if (activityLogeable) {
            Log.d("ActivityLifecycle", "++numberOfActivitiesInForeground");
            ++numberOfActivitiesInForeground;
        }

    }

    @SuppressLint("LongLogTag")
    public void onActivityResumed(Activity activity) {
        Log.d("ActivityLifecycleHandler", "onActivityResumed " + activity.getClass().getSimpleName() + " count of activities = " +  numberOfActivitiesInForeground);
    }

    public boolean isActivityLogeable(Activity activity) {
        return (activity instanceof Loggable);
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        //Count only our app logeable activity
        if (activity instanceof Loggable) {
            Log.d("ActivityLifecycle", "--numberOfActivitiesInForeground");
            --numberOfActivitiesInForeground;
        }
        Lo.g("onActivityStopped" + numberOfActivitiesInForeground);

        if (numberOfActivitiesInForeground == 0 && activity instanceof Loggable) {
            AppSession.getSession().updateState(AppSession.ChatState.BACKGROUND);
            boolean isLoggedIn = isLoggedIn();
            Log.d(TAG, "isLoggedIn= " + isLoggedIn);
            if (!isLoggedIn) {
                return;
            }
            chatDestroyed = ((Loggable) activity).isCanPerformLogoutInOnStop();
            Log.d(TAG, "onDestroy chatDestroyed= " + chatDestroyed);
            if (chatDestroyed) {
                QBLogoutAndDestroyChatCommand.start(activity, true);
            }
        }
    }

    private boolean isLoggedIn() {
        return QBChatService.getInstance().isLoggedIn();
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
}
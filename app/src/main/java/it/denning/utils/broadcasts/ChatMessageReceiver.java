package it.denning.utils.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_user_service.model.QMUser;

import it.denning.ui.activities.call.CallActivity;
import it.denning.utils.SystemUtils;
import it.denning.utils.helpers.notification.ChatNotificationHelper;

public class ChatMessageReceiver extends BroadcastReceiver {

    private static final String TAG = ChatMessageReceiver.class.getSimpleName();
    private static final String callActivityName = CallActivity.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "--- onReceive() ---");

        String activityOnTop = SystemUtils.getNameActivityOnTopStack();

        if (!SystemUtils.isAppRunningNow() && !callActivityName.equals(activityOnTop)) {
            ChatNotificationHelper chatNotificationHelper = new ChatNotificationHelper(context);

            String message = intent.getStringExtra(QBServiceConsts.EXTRA_CHAT_MESSAGE);
            QMUser user = (QMUser) intent.getSerializableExtra(QBServiceConsts.EXTRA_USER);
            String dialogId = intent.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID);

            chatNotificationHelper.saveOpeningDialogData(user.getId(), dialogId);
            chatNotificationHelper.sendChatNotification(message, user.getId(), dialogId);
        }
    }
}
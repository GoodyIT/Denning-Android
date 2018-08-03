package it.denning.fcm;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

import it.denning.utils.helpers.notification.ChatNotificationHelper;


public class FcmPushListenerService extends QBFcmPushListenerService {
    private String TAG = FcmPushListenerService.class.getSimpleName();

    private ChatNotificationHelper chatNotificationHelper;

    public FcmPushListenerService() {
        this.chatNotificationHelper = new ChatNotificationHelper(this);
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);

        String userId = (String) data.get(ChatNotificationHelper.USER_ID);
        String pushMessage = (String) data.get(ChatNotificationHelper.MESSAGE);
        String dialogId = (String) data.get(ChatNotificationHelper.DIALOG_ID);
        String pushMessageType = (String) data.get(ChatNotificationHelper.MESSAGE_TYPE);

        Log.v(TAG, "sendPushMessage\n" + "Message: " + pushMessage + "\nUser ID: " + userId + "\nDialog ID: " + dialogId);

        Bundle extras = new Bundle();
        extras.putString(ChatNotificationHelper.USER_ID, userId);
        extras.putString(ChatNotificationHelper.MESSAGE, pushMessage);
        extras.putString(ChatNotificationHelper.DIALOG_ID, dialogId);
        extras.putString(ChatNotificationHelper.MESSAGE_TYPE, pushMessageType);

        chatNotificationHelper.parseChatMessage(extras);
    }
}

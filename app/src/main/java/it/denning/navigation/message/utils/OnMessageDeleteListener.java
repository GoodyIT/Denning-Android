package it.denning.navigation.message.utils;

import android.view.View;

import com.quickblox.chat.model.QBChatDialog;

/**
 * Created by hothongmee on 14/11/2017.
 */

public interface OnMessageDeleteListener {
    public void onMessageDelete(View v, QBChatDialog dialog);
}

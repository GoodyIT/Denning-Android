package it.denning.navigation.message.utils;

import android.view.View;

import com.quickblox.q_municate_user_service.model.QMUser;

/**
 * Created by hothongmee on 12/11/2017.
 */

public interface OnMessageItemClickListener {
    void onMessageItemClick(View view, int position, QMUser user);
}


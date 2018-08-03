package it.denning.navigation.message.utils;

import android.view.View;

import com.quickblox.q_municate_user_service.model.QMUser;

/**
 * Created by hothongmee on 14/11/2017.
 */

public interface OnMessageFavClickListener {
    public void onFavClick(View view, int sectionIndex, int itemIndex, QMUser user, boolean isAdd);
}

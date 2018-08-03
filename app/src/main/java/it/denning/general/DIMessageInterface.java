package it.denning.general;

import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.List;

import it.denning.model.ChatContactModel;

/**
 * Created by hothongmee on 11/11/2017.
 */

public interface DIMessageInterface {
    public void onSuccess(ChatContactModel chatContactModel);

    public void onError(String error);
}

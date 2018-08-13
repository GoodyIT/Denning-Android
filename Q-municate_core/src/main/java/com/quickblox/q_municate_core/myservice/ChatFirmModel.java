package com.quickblox.q_municate_core.myservice;

import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hothongmee on 10/11/2017.
 */

public class ChatFirmModel {
    public String firmCode;
    public String firmName;
    public List<ChatUserModel> users;

    public List<QMUser> chatUsers = new ArrayList<>();
}

package it.denning.model;

import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

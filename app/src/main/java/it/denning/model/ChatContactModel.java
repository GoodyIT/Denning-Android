package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hothongmee on 10/11/2017.
 */

public class ChatContactModel {
    public String dtExpire;
    public boolean isExpire;

    @SerializedName("favourite_staff")
    public List<ChatFirmModel> favoriteStaffContacts;

    @SerializedName("favourite_client")
    public List<ChatFirmModel> favoriteClientContacts;

    @SerializedName("staff")
    public List<ChatFirmModel> staffContacts;

    @SerializedName("client")
    public List<ChatFirmModel> clientContacts;

    @SerializedName("denningPeople")
    public List<ChatFirmModel> denningContacts;

    public List<ChatFirmModel> getFavoriteClientContacts() {
        return favoriteClientContacts != null ? favoriteClientContacts : new ArrayList<ChatFirmModel>();
    }

    public List<ChatFirmModel> getFavoriteStaffContacts() {
        return favoriteStaffContacts != null ? favoriteStaffContacts : new ArrayList<ChatFirmModel>();
    }

    public List<ChatFirmModel> getStaffContacts() {
        return staffContacts != null ? staffContacts : new ArrayList<ChatFirmModel>();
    }

    public List<ChatFirmModel> getClientContacts() {
        return clientContacts != null ? clientContacts : new ArrayList<ChatFirmModel>();
    }

    public List<ChatFirmModel> getDenningContacts() {
        return denningContacts != null ? denningContacts : new ArrayList<ChatFirmModel>();
    }
}

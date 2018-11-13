package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class StaffOnlineModel implements Serializable {
    public String API;
    public String device;
    public String inTime;
    public String name;
    public String totalHour;
    public String outTime;
    public String status;
    public Boolean onlineApp;
    public Boolean onlineExe;
    public Boolean onlineWeb;
}

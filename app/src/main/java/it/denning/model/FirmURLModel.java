package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 20/04/2017.
 */

public class FirmURLModel{
    public String email;

    public String hpNumber;

    public Integer lockMinute;

    public String sessionID;

    public String status;

    public Integer statusCode;

    public String userType;

    public String avatar_url;

    public String name;

    @SerializedName("catDenning")
    public List<FirmModel> catDenning;

    @SerializedName("catPersonal")
    public List<FirmModel> catPersonal;

}

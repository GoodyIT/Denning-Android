package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 27/04/2017.
 */

public class PartyGroup implements Serializable {
    @SerializedName("PartyName")
    public String partyName;
    public Boolean isRepresent;
    public ArrayList<StaffModel> party;
    public String reference;
    public StaffModel solicitor;
}

package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 18/04/2017.
 */

public class Event implements Serializable{
    public String FileNo;
    public String URL;
    public String code;

    @SerializedName("description")
    public String caseName;
    public String caseNo;
    public String eventStart;
    public String eventEnd;
    public String eventType;
    public String location;
    public String counsel;
    public String imageData;
    public String reminder1;
    public String reminder2;
    public String title;


}

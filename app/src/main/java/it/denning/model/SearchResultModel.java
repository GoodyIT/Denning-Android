package it.denning.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 24/04/2017.
 */

public class SearchResultModel extends ParentModel implements Serializable {

    @SerializedName("Desc")
    public String description;

    @SerializedName("Form")
    public String form;

    @SerializedName("Header")
    public String header;

    @SerializedName("IndexData")
    public String indexData;

    @SerializedName("Score")
    public String score;

    @SerializedName("Title")
    public String title;

    @SerializedName("code")
    public String searchCode;

    public String key;

    @SerializedName("SortDate")
    public String sortDate;

    @SerializedName("JsonDesc")
    public String jsonDesc;

    @SerializedName("JSON")
    public String json;

    public String row_number;

    public JsonObject getJsonDesc() {
        return new Gson().fromJson(jsonDesc, JsonObject.class);
    }
}

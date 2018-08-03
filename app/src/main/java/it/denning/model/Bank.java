package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 27/04/2017.
 */

public class Bank implements Serializable {
    public HQ hq;
    public String code;
    public String name;
    public String IDNo;

    @SerializedName("phoneMobile")
    public String mobilePhone;

    @SerializedName("phoneOffice")
    public String officePhone;

    @SerializedName("phoneHome")
    public String homePhone;

    @SerializedName("phoneFax")
    public String fax;

    @SerializedName("emailAddress")
    public String email;

    @SerializedName("address")
    public AddressModel address;

    public ArrayList<SearchResultModel> relatedMatter;

    public String getName() {
        return hq.name;
    }

    public String getIDNo() {
        return hq.IDNo;
    }
}

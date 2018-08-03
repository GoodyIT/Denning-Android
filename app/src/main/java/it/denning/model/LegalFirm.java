package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by denningit on 26/04/2017.
 */

public class LegalFirm implements Serializable{
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
    public String faxPhone;

    @SerializedName("emailAddress")
    public String email;
    public AddressModel address;
    public List<SearchResultModel> relatedMatter;
}

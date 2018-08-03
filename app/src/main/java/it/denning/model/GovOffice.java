package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 28/04/2017.
 */

public class GovOffice implements Serializable {
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

    public AddressModel address;
    public ArrayList<SearchResultModel> relatedMatters;
}

package it.denning.model;

import android.location.Address;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 25/04/2017.
 */

public class Contact implements Serializable{
    public String code;
    public String name;

    @SerializedName("dateBirth")
    public String dateOfBirth;
    public String citizenship;
    public String IDNo;
    public CodeDescription idType;

    @SerializedName("taxFileNo")
    public String tax;

    @SerializedName("phoneMobile")
    public String mobilePhone;

    @SerializedName("phoneOffice")
    public String officePhone;

    @SerializedName("phoneHome")
    public String homePhone;

    @SerializedName("phoneFax")
    public String fax;

    @SerializedName("irdBranch")
    public CodeDescription IRDBranch;

    public CodeDescription occupation;

    @SerializedName("emailAddress")
    public String email;

    public AddressModel address;
    public String contactPerson;
    public String website;
    public String registeredOffice;
    public String title;
    public String InviteToDenning;
    public String KPLama;
    public ArrayList<SearchResultModel> relatedMatter;

    public Contact() {
        idType = IRDBranch = occupation = new CodeDescription();
        address = new AddressModel();
    }

    public String getIdTypeCode() {
        return idType != null ? idType.getCode() : "";
    }

    public String getIdTypeDesc() {
        return idType != null ? idType.getDesc() : "";
    }

    public String getAddress1() {
        return address != null ? address.line1 : "";
    }

    public String getAddress2() {
        return address != null ? address.line2 : "";
    }

    public String getAddress3() {
        return address != null ? address.line3: "";
    }

    public String getPostCode() {
        return address != null ? address.postCode: "";
    }

    public String getTown() {
        return address != null ? address.city: "";
    }

    public String getState() {
        return address != null ? address.state: "";
    }

    public String getCountry() {
        return address != null ? address.country: "";
    }

    public String getOccupationCode() {
        return occupation != null ? occupation.getCode() : "";
    }

    public String getOccupationDesc() {
        return occupation != null ? occupation.getDesc() : "";
    }

    public String getIRDBranchCode() {
        return IRDBranch != null ? IRDBranch.getCode() : "";
    }

    public String getIRDBranchDesc() {
        return IRDBranch != null ? IRDBranch.getDesc() : "";
    }
}

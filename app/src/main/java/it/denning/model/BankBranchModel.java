package it.denning.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-31.
 */

public class BankBranchModel implements Serializable {
    public HQ HQ;
    public CAC CAC;
    public String code;
    public String name;
    public String IDNo;

    @SerializedName("emailAddress")
    public String email;

    public String dateBirth;

    public CodeDescription idTyp;

    public String title;

    public String webSite;

    @SerializedName("phoneMobile")
    public String mobilePhone;

    @SerializedName("phoneOffice")
    public String officePhone;

    @SerializedName("phoneHome")
    public String homePhone;

    @SerializedName("phoneFax")
    public String fax;

    public AddressModel address;

}

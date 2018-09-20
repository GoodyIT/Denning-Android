package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2017-12-02.
 */

public class StaffModel implements Serializable{
    public String IDNo;
    public AddressModel address;
    public String citizenship;
    public String code;
    public String dateBirth;
    public String emailAddress;
    public CodeDescription idTyp;
    public String name;
    public String phoneFax;
    public String phoneHome;
    public String phoneOffice;
    public String title;
    public String webSite;
    public ChatStatus chatStatus;
    public String nickName;
    public String userID;
    public String KPLama;

    public String getCode() {
        return code == null ? "" : code;
    }
}

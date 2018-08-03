package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-19.
 */

public class AccountType implements Serializable{
    public String ID;
    public String description;
    public String code;
    public String shortDesc;

    public AccountType() {
        this.ID = "";
        this.description = "";
        this.code = "";
        this.shortDesc = "";
    }

    public String getShorDesc() {
        return shortDesc != null ? shortDesc: "";
    }
}

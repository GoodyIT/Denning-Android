package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 27/04/2017.
 */

public class BankGroup implements Serializable {
    public StaffModel bank;
    public String groupName;
    public String bankName;

    public String getCode() {
        return  bank.code;
    }

    public String getBankName() {
        if (bank == null) {
            return "";
        }
        return bank.name;
    }

    public String getBankCode() {
        if (bank == null) {
            return "";
        }
        return bank.code;
    }

    public String getGroupName() {
        return groupName != null ? groupName : "";
    }

    // data missing
}

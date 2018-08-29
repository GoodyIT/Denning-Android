package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 29/04/2017.
 */

public class BankReconModel implements Serializable {
    public String accountName;
    public String accountNo;
    public String credit;
    public String debit;
    public String lastMovement;
    public String pid;
    public String API;

    public BankReconModel() {
        this.accountName = "";
        this.accountNo = "";
        this.credit = "";
        this.debit = "";
    }
}

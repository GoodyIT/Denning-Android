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

public class Ledger implements Serializable {
    public String accountName;
    public String urlDetail;
    public String availableBalance;
    public String currentBalance;
    public String lastStatementDate;

    public String getBalance() {
        return availableBalance;
    }
}

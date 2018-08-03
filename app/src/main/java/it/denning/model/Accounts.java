package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 29/04/2017.
 */

public class Accounts implements Serializable {
    public String fileName;
    public String fileNo;

    @SerializedName("accountType")
    public ArrayList<Ledger> ledgerArrayList;
}

package it.denning.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class NewLedgerModel implements Serializable {
    @SerializedName("accountType")
    public ArrayList<Ledger> ledgerArrayList;
    public String fileName;
    public String fileNo;
}

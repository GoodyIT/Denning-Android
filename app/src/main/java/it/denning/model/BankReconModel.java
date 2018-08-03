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

    public static BankReconModel getBankReconFromResponse(JSONObject jsonObject) {
        BankReconModel ledger = new BankReconModel();
        try {
            ledger.accountName = DIHelper.getSafeString(jsonObject.getString("accountName"));
            ledger.accountNo = DIHelper.getSafeString(jsonObject.getString("accountNo"));
            ledger.credit = DIHelper.getSafeString(jsonObject.getString("credit"));
            ledger.debit = DIHelper.getSafeString(jsonObject.getString("debit"));
            ledger.lastMovement = DIHelper.getSafeString(jsonObject.getString("lastMovement"));
            ledger.pid = DIHelper.getSafeString(jsonObject.getString("pid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            ledger.API = DIHelper.getSafeString(jsonObject.getString("API"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ledger;
    }

    public static ArrayList<BankReconModel> getBankReconArrayFromResponse(JSONArray jsonArray) {
        ArrayList<BankReconModel> ledgerArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ledgerArrayList.add(BankReconModel.getBankReconFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ledgerArrayList;
    }
}

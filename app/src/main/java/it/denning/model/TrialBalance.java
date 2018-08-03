package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class TrialBalance implements Serializable {
    public String APIcredit;
    public String APIdebit;
    public String accountName;
    public String accountType;
    public String credit;
    public String debit;
    public String isBalance;

    public static TrialBalance getTrialBalanceFromResponse(JSONObject jsonObject) throws JSONException {
        TrialBalance model = new TrialBalance();

        model.APIcredit = jsonObject.getString("APIcredit");
        model.APIdebit = jsonObject.getString("APIdebit");
        model.accountName = jsonObject.getString("accountName");
        model.accountType = jsonObject.getString("APIcredit");
        model.APIcredit = jsonObject.getString("accountType");
        model.credit = jsonObject.getString("credit");
        model.debit = jsonObject.getString("debit");
        model.isBalance = jsonObject.getString("isBalance");

        return model;
    }

    public static ArrayList<TrialBalance> getTrialBalanceArrayFromResponse(JSONArray jsonArray) {
        ArrayList<TrialBalance> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(TrialBalance.getTrialBalanceFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}

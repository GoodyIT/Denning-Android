package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossDetailModel implements Serializable {
    public String expenses;
    public String profitLoss;
    public String revenue;
    public String theYear;

    public static ProfitLossDetailModel getProfitLossDetailFromResponse(JSONObject jsonObject) {
        ProfitLossDetailModel model = new ProfitLossDetailModel();

        try {
            model.expenses = DIHelper.getSafeString(jsonObject.getString("expenses"));
            model.profitLoss = DIHelper.getSafeString(jsonObject.getString("profitLoss"));
            model.revenue = DIHelper.getSafeString(jsonObject.getString("revenue"));
            model.theYear = DIHelper.getSafeString(jsonObject.getString("theYear"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static ArrayList<ProfitLossDetailModel> getLegalFirmArrayFromResponse(JSONArray jsonArray) {
        ArrayList<ProfitLossDetailModel> modelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                modelArrayList.add(ProfitLossDetailModel.getProfitLossDetailFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return modelArrayList;
    }
}

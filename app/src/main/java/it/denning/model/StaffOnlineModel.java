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

public class StaffOnlineModel implements Serializable {
    public String API;
    public String device;
    public String inTime;
    public String name;
    public String totalHour;
    public String outTime;
    public String status;


    public static StaffOnlineModel getStaffOnlineFromResponse(JSONObject jsonObject) throws JSONException {
        StaffOnlineModel model = new StaffOnlineModel();

        model.API = DIHelper.getSafeString(jsonObject.getString("API"));
        model.device = DIHelper.getSafeString(jsonObject.getString("device"));
        model.inTime = DIHelper.getSafeString(jsonObject.getString("inTime"));
        model.name = DIHelper.getSafeString(jsonObject.getString("name"));
        model.totalHour = DIHelper.getSafeString(jsonObject.getString("totalHour"));
        try {
            model.outTime = DIHelper.getSafeString(jsonObject.getString("outTime"));
        }catch (JSONException e) {
            e.printStackTrace();
        }
        model.status = DIHelper.getSafeString(jsonObject.getString("status"));

        return model;
    }

    public static ArrayList<StaffOnlineModel> getStaffOnlineArrayFromResponse(JSONArray jsonArray) {
        ArrayList<StaffOnlineModel> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(StaffOnlineModel.getStaffOnlineFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}

package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hothongmee on 06/11/2017.
 */

public class VisibleModel {
    public String iStyle;
    public String isVisible;
    public String sessionAPI;
    public String sessionID;
    public String sessionName;

    public static VisibleModel getVisibleModelFromResponse(JSONObject jsonObject) {
        VisibleModel model = new VisibleModel();

        try {
            model.isVisible = jsonObject.getString("isVisible");
            model.iStyle = jsonObject.getString("iStyle");
            model.sessionAPI = jsonObject.getString("sessionAPI");
            model.sessionID = jsonObject.getString("sessionID");
            model.sessionName = jsonObject.getString("sessionName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static ArrayList<VisibleModel> getVisibleModelArrayFromResponse(JSONArray jsonArray) {
        ArrayList<VisibleModel> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                arrayList.add(VisibleModel.getVisibleModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}

package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class ThirdItemModel implements Serializable {
    public String api;
    public String id;
    public String label;
    public String value;
    public String nextLevelForm;

    public static ThirdItemModel getThirdItemModelFromResponse(JSONObject jsonObject) {
        ThirdItemModel thirdItemModel = new ThirdItemModel();

        try {
            thirdItemModel.value = jsonObject.getString("value");
            thirdItemModel.api = jsonObject.getString("api");
            thirdItemModel.id = jsonObject.getString("id");
            thirdItemModel.label = jsonObject.getString("label");
            thirdItemModel.nextLevelForm = jsonObject.getString("nextLevelForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return thirdItemModel;
    }

    public static ArrayList<ThirdItemModel> getThirdItemModelArrayFromResponse(JSONArray jsonArray) {
        ArrayList<ThirdItemModel> thirdItemModelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                thirdItemModelArrayList.add(ThirdItemModel.getThirdItemModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return thirdItemModelArrayList;
    }
}

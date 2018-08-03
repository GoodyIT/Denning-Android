package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class SecondItemModel implements Serializable {
    public String OR;
    public String RM;
    public String api;
    public String deposited;
    public String id;
    public String label;
    public String nextLevelForm;

    public static SecondItemModel getSecondItemModelFromResponse(JSONObject jsonObject) {
        SecondItemModel secondItemModel = new SecondItemModel();

        try {
            secondItemModel.OR = jsonObject.getString("OR");
            secondItemModel.RM = jsonObject.getString("RM");
            secondItemModel.api = jsonObject.getString("api");
            secondItemModel.deposited = jsonObject.getString("deposited");
            secondItemModel.id = jsonObject.getString("id");
            secondItemModel.label = jsonObject.getString("label");
            secondItemModel.nextLevelForm = jsonObject.getString("nextLevelForm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secondItemModel;
    }

    public static ArrayList<SecondItemModel> getSecondItemModelArrayFromResponse(JSONArray jsonArray) {
        ArrayList<SecondItemModel> secondItemModelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                secondItemModelArrayList.add(SecondItemModel.getSecondItemModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return secondItemModelArrayList;
    }
}

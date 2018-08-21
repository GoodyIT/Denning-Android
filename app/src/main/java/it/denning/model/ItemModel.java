package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class ItemModel implements Serializable{
    public String api;
    public String value;
    public String itemId;
    public String label;

    public static ItemModel getItemModelFromResponse(JSONObject jsonObject) {
        ItemModel itemModel = new ItemModel();

        try {
            itemModel.api = jsonObject.getString("api");
            itemModel.value = jsonObject.getString("value");
            itemModel.itemId = jsonObject.getString("id");
            itemModel.label = jsonObject.getString("label");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemModel;
    }

    public static ArrayList<ItemModel> getItemModelArrayFromResponse(JSONArray jsonArray) {
        ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                itemModelArrayList.add(ItemModel.getItemModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return itemModelArrayList;
    }
}

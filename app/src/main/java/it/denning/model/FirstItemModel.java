package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class FirstItemModel implements Serializable {
    public String formName;
    public String background;
    public String footerAPI;
    public String footerDescription;
    public String footerDescriptionColor;
    public String footerValue;
    public String footerValueColor;
    public String icon;
    public String id;
    public String mainAPI;
    public String mainValue;
    public String mainValueColor;
    public String nextLevelForm;
    public String title;
    public String titleColor;

    public static FirstItemModel getFirstItemModelFromResponse(JSONObject jsonObject) {
        FirstItemModel firstItemModel = new FirstItemModel();

        try {
            firstItemModel.background = jsonObject.getString("background");
            firstItemModel.footerAPI = jsonObject.getString("footerAPI");
            firstItemModel.footerDescription = jsonObject.getString("footerDescription");
            firstItemModel.footerDescriptionColor = jsonObject.getString("footerDescriptionColor");
            firstItemModel.footerValue = jsonObject.getString("footerValue");
            firstItemModel.footerValueColor = jsonObject.getString("footerValueColor");
            firstItemModel.icon = jsonObject.getString("icon");
            firstItemModel.id = jsonObject.getString("id");
            firstItemModel.mainAPI = jsonObject.getString("mainAPI");
            firstItemModel.mainValue = jsonObject.getString("mainValue");
            firstItemModel.mainValueColor = jsonObject.getString("mainValueColor");
            firstItemModel.nextLevelForm = jsonObject.getString("nextLevelForm");
            firstItemModel.title = jsonObject.getString("title");
            firstItemModel.titleColor = jsonObject.getString("titleColor");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return firstItemModel;
    }

    public static ArrayList<FirstItemModel> getFirstItemModelArrayFromResponse(JSONArray jsonArray) {
        ArrayList<FirstItemModel> firstItemModelArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                firstItemModelArrayList.add(FirstItemModel.getFirstItemModelFromResponse(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return firstItemModelArrayList;
    }
}

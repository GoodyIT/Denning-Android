package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class S2 implements Serializable {
    public String isVisible;
    public String style;
    public String title;
    public ArrayList<SecondItemModel> items;

    public static S2 getS2FromResponse(JSONObject jsonObject) {
        S2 s2 = new S2();

        try {
            s2.isVisible = jsonObject.getString("isVisible");
            s2.style = jsonObject.getString("style");
            s2.title = jsonObject.getString("title");
            s2.items = SecondItemModel.getSecondItemModelArrayFromResponse(jsonObject.getJSONArray("items"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s2;
    }
}

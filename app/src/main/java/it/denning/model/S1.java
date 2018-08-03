package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class S1 implements Serializable {
    public String isVisible;
    public String style;
    public String title;
    public ArrayList<FirstItemModel> items;

    public static S1 getS1FromResponse(JSONObject jsonObject) {
        S1 s1 = new S1();

        try {
            s1.isVisible = jsonObject.getString("isVisible");
            s1.style = jsonObject.getString("style");
            s1.title = jsonObject.getString("title");
            s1.items = FirstItemModel.getFirstItemModelArrayFromResponse(jsonObject.getJSONArray("items"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s1;
    }
}

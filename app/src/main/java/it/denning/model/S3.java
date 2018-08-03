package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class S3 implements Serializable {
    public String isVisible;
    public String style;
    public String title;
    public ArrayList<ThirdItemModel> items;

    public static S3 getS3FromResponse(JSONObject jsonObject) {
        S3 s3 = new S3();

        try {
            s3.isVisible = jsonObject.getString("isVisible");
            s3.style = jsonObject.getString("style");
            s3.title = jsonObject.getString("title");
            s3.items = ThirdItemModel.getThirdItemModelArrayFromResponse(jsonObject.getJSONArray("items"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s3;
    }
}

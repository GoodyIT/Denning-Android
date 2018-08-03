package it.denning.model;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 18/04/2017.
 */

public class News {
    public String URL;
    public String category;
    public String code;
    public String imageData;

    @SerializedName("shortDesc")
    public String shortDescription;
    public String theDateTime;
    public String title;
    public JsonObject img;

    public News() {

    }

    public Bitmap getImage() {
        return DIHelper.base64ToBitmap(img.get("base64").getAsString());
    }
}

package it.denning.model;

import com.google.gson.JsonObject;

/**
 * Created by denningit on 2017-12-09.
 */

public class AdsModel {
    public String URL;
    public String code;
    public JsonObject img;

    public String getBase64Image() {
        return img.get("base64").getAsString();
    }
}

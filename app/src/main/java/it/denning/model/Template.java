package it.denning.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by denningit on 2017-12-24.
 */

public class Template {
    public String code;
    public String dtCreatedDate;
    public String strSource;
    public String strLangauge;
    public String strDescription;
    public String generateAPI;
    public String intVersionID;
    public String strDocumentName;
    public String eOutput;
    public String generateBody;

    public JsonObject getJsonGenerateBody() {
        return new Gson().fromJson(generateBody, JsonObject.class);
    }
}


package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hothongmee on 08/11/2017.
 */

public class CodeDescription implements Serializable {
    public String code;
    public String description;

    public CodeDescription() {

    }

    public CodeDescription(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDesc() {
        return description != null ? description : "";
    }

    public String getCode() {
      return   code != null ? code : "";
    }
}

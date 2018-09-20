package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 27/04/2017.
 */

public class MatterProperty implements Serializable {
    public String code;
    public String fullTitle;
    public TypeValue title;
    public TypeValue lotPT;
    public String address;
    public TypeValue area;

    public String getCode() {
        return code == null ? "" : code;
    }
}

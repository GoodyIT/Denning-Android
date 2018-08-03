package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 27/04/2017.
 */

public class SolicitorGroup implements Serializable {
    public String groupName;
    public StaffModel solicitor;
    public String reference;

    public String getCode() {
        if (solicitor != null) {
            return solicitor.code;
        }

        return  "";
    }

    public String getName() {
        if (solicitor != null) {
            return solicitor.name;
        }

        return  "";
    }

    public String getGroupName() {
        return groupName != null ? groupName : "";
    }
}

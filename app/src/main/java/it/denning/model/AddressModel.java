package it.denning.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hothongmee on 08/11/2017.
 */

public class AddressModel implements Serializable {
    public String city;
    public String country;
    public String fullAddress;
    public String line1;
    public String line2;
    public String line3;
    public String postCode;
    public String state;

    public static AddressModel getAddressFromResponse(JSONObject jsonObject) throws JSONException {
        AddressModel model = new AddressModel();

        model.city = jsonObject.getString("city");
        model.country = jsonObject.getString("country");
        model.fullAddress = jsonObject.getString("fullAddress");
        model.line1 = jsonObject.getString("line1");
        model.line2 = jsonObject.getString("line2");
        model.line3 = jsonObject.getString("line3");
        model.postCode = jsonObject.getString("postCode");
        model.state = jsonObject.getString("state");

        return  model;
    }
}

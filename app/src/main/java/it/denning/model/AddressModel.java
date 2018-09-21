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
}

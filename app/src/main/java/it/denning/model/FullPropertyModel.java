package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-31.
 */

public class FullPropertyModel implements Serializable {
    public String code;
    public String fullTitle;
    public String address;
    public String projectName;
    public String spaCondoName;
    public TypeValue spaParcel;

    public String getFullAddress() {
        return address != null ? address : "";
    }

    public String getParcelType() {
        return spaParcel != null ? spaParcel.type : "";
    }

    public String getParcelValue() {
        return spaParcel != null ? spaParcel.value : "";
    }
}

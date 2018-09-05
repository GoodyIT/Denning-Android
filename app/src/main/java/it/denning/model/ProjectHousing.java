package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2017-12-12.
 */

public class ProjectHousing implements Serializable {
    public String code;
    public StaffModel developer;
    public String licenseNo;
    public String masterTitle;
    public String name;
    public String phase;
    public StaffModel proprietor;

    public String getDevCode() {
        return developer == null ? "" : developer.code;
    }

    public String getDevname() {
        return developer == null ? "" : developer.name;
    }

    public String getProprietorCode() {
        return proprietor == null ? "" : proprietor.code;
    }

    public String getProprietorname() {
        return proprietor == null ? "" : proprietor.name;
    }
}

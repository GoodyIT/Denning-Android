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
}

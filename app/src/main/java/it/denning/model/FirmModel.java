package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2017-12-12.
 */

public class FirmModel extends ParentModel implements Serializable{
    public String APIServer;
    public StaffModel LawFirm;
    public String category;
    public String theCode;
}

package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-02-05.
 */

public class OfficeDiaryModel implements Serializable{
    public String appointmentDetails;
    public CodeDescription attendedStatus;
    public String caseName;
    public String caseNo;
    public String code;
    public String endDate;
    public String fileNo1;
    public String place;
    public String remarks;
    public StaffModel staffAssigned;
    public String staffAttended;
    public String startDate;
}

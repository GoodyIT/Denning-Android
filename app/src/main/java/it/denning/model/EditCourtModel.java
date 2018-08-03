package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-02-03.
 */

public class EditCourtModel implements Serializable {
    public CodeDescription attendedStatus;
    public String code;
    public String caseNo;
    public String caseName;
    public Coram coram;
    public StaffModel counselAssigned;
    public String counselAttended;
    public CourtDiaryCourt court;
    public String courtDecision;
    public String enclosureDetails;
    public String enclosureNo;
    public String fileNo1;
    public String hearingStartDate;
    public String hearingEndDate;
    public String hearingType;
    public String nextStartDate;
    public String nextEndDate;
    public CodeDescription nextDateType;
    public String opponentCounsel;
    public String previousDate;
    public String remarks;

    public String getCoramName() {
        return coram != null ? coram.name : "";
    }

    public String getAttendedStatusCode() {
        return attendedStatus != null ? attendedStatus.code : "";
    }

    public String getAttendedStatusDesc() {
        return attendedStatus != null ? attendedStatus.description : "";
    }

    public String getCoramCode() {
        return coram != null ? coram.code : "";
    }

    public String getCounselAssignedCode() {
        return counselAssigned != null ? counselAssigned.code : "";
    }

    public String getCounselAssignedName() {
        return counselAssigned != null ? counselAssigned.name : "";
    }

    public String getCourtCode() {
        return court != null ? court.code : "";
    }

    public String getNextDateTypeCode() {
        return nextDateType != null ? nextDateType.code : "";
    }

    public String getNextDateTypeDesc() {
        return nextDateType != null ? nextDateType.description : "";
    }
}

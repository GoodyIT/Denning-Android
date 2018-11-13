package it.denning.model;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 2018-01-26.
 */

public class LeaveRecordModel {
    private String code;
    private String dtEndDate;
    private String dtStartDate;
    private String decLeaveLength;
    private String intAL;
    private String intPYL;
    private String intTaken;
    private CodeDescription clsLeaveStatus;
    private AttendanceInfo clsStaff;
    private CodeDescription clsTypeOfLeave;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setDtEndDate(String dtEndDate) {
        this.dtEndDate = DIHelper.convertToSimpleDateFormat(dtEndDate);
    }

    public String getDtEndDate() {
        return DIHelper.getLeaveAppDate(this.dtEndDate);
    }

    public void setDtStartDate(String dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public String getDtStartDate() {
        return DIHelper.getLeaveAppDate(this.dtStartDate);
    }

    public void setDecLeaveLength(String decLeaveLength) {
        this.decLeaveLength = decLeaveLength;
    }

    public String getDecLeaveLength() {
        return this.decLeaveLength.isEmpty() ? "0" : this.decLeaveLength;
    }

    public void setIntAL(String intAL) {
        this.intAL = intAL;
    }

    public String getIntAL() {
        return this.intAL;
    }

    public void setIntPYL(String intPYL) {
        this.intPYL = intPYL;
    }

    public String getIntPYL() {
        return this.intPYL;
    }

    public void setIntTaken(String intTaken) {
        this.intTaken = intTaken;
    }

    public String getIntTaken() {
        return intTaken;
    }

    public void setClsLeaveStatus(CodeDescription clsLeaveStatus) {
        this.clsLeaveStatus = clsLeaveStatus;
    }

    public CodeDescription getClsLeaveStatus() {
        return clsLeaveStatus;
    }

    public void setClsStaff(AttendanceInfo clsStaff) {
        this.clsStaff = clsStaff;
    }

    public AttendanceInfo getClsStaff() {
        return clsStaff;
    }

    public String getStaffName() {
        return clsStaff != null ? clsStaff.strName : "";
    }

    public void setClsTypeOfLeave(CodeDescription clsTypeOfLeave) {
        this.clsTypeOfLeave = clsTypeOfLeave;
    }

    public CodeDescription getClsTypeOfLeave() {
        return clsTypeOfLeave;
    }

    public String getTypeOfLeaveCode() {
        return clsTypeOfLeave != null ? clsTypeOfLeave.code : "";
    }
}

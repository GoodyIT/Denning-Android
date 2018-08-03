package it.denning.model;

import java.io.Serializable;

public class StaffLeaveModel implements Serializable{

    String code;

    String dtDateApproved;

    String dtDateEntered;

    String dtDateSubmitted;

    String dtDateUpdated;

    String dtEndDate;

    String dtStartDate;

    String decLeaveLength;

    String strLogBalancedLeave;

    String strLogEntitlement;

    String strLogUsedToDate;

    String strManagerRemarks;

    String strRemarks;

    String strStaffRemarks;

    AttendanceInfo clsApprovedBy;

    AttendanceInfo clsEnteredBy;

    AttendanceInfo clsStaff;

    AttendanceInfo clsUpdatedBy;

    CodeDescription clsLeaveStatus;

    CodeDescription clsTypeOfLeave;

    public String getCode() {
        return this.code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getDtDateApproved() {
        return this.dtDateApproved;
    }

    public void setDtDateApproved(String value) {
        this.dtDateApproved = value;
    }

    public String getDtDateEntered() {
        return this.dtDateEntered;
    }

    public void setDtDateEntered(String value) {
        this.dtDateEntered = value;
    }

    public String getDtDateSubmitted() {
        return this.dtDateSubmitted;
    }

    public void setDtDateSubmitted(String value) {
        this.dtDateSubmitted = value;
    }

    public String getDtDateUpdated() {
        return this.dtDateUpdated;
    }

    public void setDtDateUpdated(String value) {
        this.dtDateUpdated = value;
    }

    public String getDtEndDate() {
        return this.dtEndDate;
    }

    public void setDtEndDate(String value) {
        this.dtEndDate = value;
    }

    public String getDtStartDate() {
        return this.dtStartDate;
    }

    public void setDtStartDate(String value) {
        this.dtStartDate = value;
    }

    public String getDecLeaveLength() {
        return this.decLeaveLength;
    }

    public void setDecLeaveLength(String value) {
        this.decLeaveLength = value;
    }

    public String getStrLogBalancedLeave() {
        return this.strLogBalancedLeave;
    }

    public void setStrLogBalancedLeave(String value) {
        this.strLogBalancedLeave = value;
    }

    public String getStrLogEntitlement() {
        return this.strLogEntitlement;
    }

    public void setStrLogEntitlement(String value) {
        this.strLogEntitlement = value;
    }

    public String getStrLogUsedToDate() {
        return this.strLogUsedToDate;
    }

    public void setStrLogUsedToDate(String value) {
        this.strLogUsedToDate = value;
    }

    public String getStrManagerRemarks() {
        return this.strManagerRemarks;
    }

    public void setStrManagerRemarks(String value) {
        this.strManagerRemarks = value;
    }

    public String getStrRemarks() {
        return this.strRemarks;
    }

    public void setStrRemarks(String value) {
        this.strRemarks = value;
    }

    public String getStrStaffRemarks() {
        return this.strStaffRemarks;
    }

    public void setStrStaffRemarks(String value) {
        this.strStaffRemarks = value;
    }

    public AttendanceInfo getClsApprovedBy() {
        return this.clsApprovedBy;
    }

    public void setClsApprovedBy(AttendanceInfo value) {
        this.clsApprovedBy = value;
    }

    public AttendanceInfo getClsEnteredBy() {
        return this.clsEnteredBy;
    }

    public void setClsEnteredBy(AttendanceInfo value) {
        this.clsEnteredBy = value;
    }

    public AttendanceInfo getClsStaff() {
        return this.clsStaff;
    }

    public void setClsStaff(AttendanceInfo value) {
        this.clsStaff = value;
    }

    public AttendanceInfo getClsUpdatedBy() {
        return this.clsUpdatedBy;
    }

    public void setClsUpdatedBy(AttendanceInfo value) {
        this.clsUpdatedBy = value;
    }

    public CodeDescription getClsLeaveStatus() {
        return this.clsLeaveStatus;
    }

    public void setClsLeaveStatus(CodeDescription value) {
        this.clsLeaveStatus = value;
    }

    public CodeDescription getClsTypeOfLeave() {
        return this.clsTypeOfLeave;
    }

    public void setClsTypeOfLeave(CodeDescription value) {
        this.clsTypeOfLeave = value;
    }
}
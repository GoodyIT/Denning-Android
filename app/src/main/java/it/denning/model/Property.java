package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2017-12-12.
 */

public class Property implements Serializable {
    public String code;
    public String accBuildingNo;
    public String accParcelNo;
    public String accStoreyNo;
    public String address;
    public String approvingAuthority;
    public TypeValue area;
    public String buildingNo;
    public String daerah;
    public StaffModel developer;
    public String fullTitle;
    public String landUse;
    public String leaseExpiryDate;
    public TypeValue lotPT;
    public MasterTitle masterTitle;
    public TypeValue mukim;
    public String negeri;
    public String parcelNo;
    public ProjectHousing project;
    public String propertyID;
    public CodeDescription propertyType;
    public StaffModel proprietor;
    public String restrictionAgainst;
    public CodeDescription restrictionInInterest;
    public String spaAccParcelNo;
    public TypeValue spaArea;
    public String spaBuildingNo;
    public String spaCondoName;
    public TypeValue spaParcel;
    public String spaStoreyNo;
    public String storeyNo;
    public String tenure;
    public TypeValue title;
    public CodeDescription titleIssued;
    public String totalShare;
    public String unitShare;

    public String getPropertyTypeCode() {
        return propertyType != null ? propertyType.code : "";
    }

    public String getPropertyTypeDesc() {
        return propertyType != null ? propertyType.description : "";
    }

    public String getTitleIssuedCode() {
        return titleIssued != null ? titleIssued.code : "";
    }

    public String getTitleIssuedDesc() {
        return titleIssued != null ? titleIssued.description : "";
    }

    public String getTitleType() {
        return title != null ? title.type : "";
    }

    public String getTitleValue() {
        return title != null ? title.value : "";
    }

    public String getLotPTType() {
        return lotPT != null ? lotPT.type : "";
    }

    public String getLotPTValue() {
        return lotPT != null ? lotPT.value : "";
    }

    public String getMukimType() {
        return mukim != null ? mukim.type : "";
    }

    public String getMukimValue() {
        return mukim != null ? mukim.value : "";
    }

    public String getAreaType() {
        return area != null ? area.type : "";
    }

    public String getAreaValue() {
        return area != null ? area.value : "";
    }

    public String getMasterTitleCode() {
        return masterTitle != null ? masterTitle.code : "";
    }

    public String getMasterTitleFullTitle() {
        return masterTitle != null ? masterTitle.fullTitle : "";
    }

    public String getRestrictionInInterestCode() {
        return restrictionInInterest != null ? restrictionInInterest.code : "";
    }

    public String getRestrictionInInterestValue() {
        return restrictionInInterest != null ? restrictionInInterest.description : "";
    }

    public String getSPAParcelType() {
        return spaParcel != null ? spaParcel.type : "";
    }

    public String getSPAParcelValue() {
        return spaParcel != null ? spaParcel.value : "";
    }

    public String getSPAAreaType() {
        return spaArea != null ? spaArea.type : "";
    }

    public String getSPAAreaValue() {
        return spaArea != null ? spaArea.value : "";
    }

    public String getProjectCode() {
        return project != null ? project.code : "";
    }

    public String getProjectName() {
        return project != null ? project.name : "";
    }

    public String getDeveloperCode() {
        return developer != null ? developer.code : "";
    }

    public String getDeveloperName() {
        return developer != null ? developer.name : "";
    }

    public String getProprietorCode() {
        return proprietor != null ? proprietor.code : "";
    }

    public String getProprietorName() {
        return proprietor != null ? proprietor.name : "";
    }

    public String getProjectMasterTitle() {
        return project != null ? project.masterTitle : "";
    }
}

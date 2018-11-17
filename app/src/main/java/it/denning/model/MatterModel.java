package it.denning.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by denningit on 27/04/2017.
 */

public class MatterModel implements Serializable {
    public String systemNo;

    @SerializedName("dateOpen")
    public String openDate;

    @SerializedName("referenceNo")
    public String ref;

    public CodeDescription fileStatus;
    public String locationBox;
    public String locationPocket;
    public String locationPhysical;
    public String dateClose;
    public String team;
    private String manualNo;

    public MatterCodeModel matter;
    public StaffModel legalAssistant;
    public MatterBranch branch;
    public StaffModel partner;
    public StaffModel primaryClient;
    public StaffModel clerk;

    @SerializedName("courtInfo")
    public Court court;

    public String remarks;

    public ArrayList<PartyGroup> partyGroup;
    public ArrayList<SolicitorGroup> solicitorsGroup;
    public ArrayList<MatterProperty> propertyGroup;
    public ArrayList<BankGroup> bankGroup;
    public ArrayList<FormulaModel> RMGroup;
    public ArrayList<FormulaModel> dateGroup;
    public ArrayList<LabelValue> textGroup;

    public String clientName() {
        return primaryClient.name;
    }

    public MatterModel() {
        textGroup = new ArrayList<>();
        dateGroup = new ArrayList<>();
        RMGroup = new ArrayList<>();
        bankGroup = new ArrayList<>();
        propertyGroup = new ArrayList<>();
        solicitorsGroup = new ArrayList<>();
        solicitorsGroup = new ArrayList<>();
        partyGroup = new ArrayList<>();
        court = new Court();
        clerk = new StaffModel();
        primaryClient = new StaffModel();
        partner = new StaffModel();
        branch = new MatterBranch();
        legalAssistant = new StaffModel();
        matter = new MatterCodeModel();
        fileStatus = new CodeDescription();
    }

    public String getFileStatusDesc() {
        if (fileStatus == null) {
            return "";
        }

        return fileStatus.description;
    }

    public String getFileStatusCode() {
        return fileStatus == null ? "" : fileStatus.getCode();
    }

    public CodeDescription getFileStatus() {
         return fileStatus == null ? new CodeDescription() : fileStatus;
    }

    public String getPrimaryClientName() {
        if (primaryClient == null) {
            return "";
        }

        return primaryClient.name;
    }

    public String getPrimaryClientCode() {
        return primaryClient == null ? "" : primaryClient.getCode();
    }

    public String getContactCode() {
        if (primaryClient == null) {
            return "";
        }

        return primaryClient.code;
    }

    public String getMatterFormName() {
        if (matter == null) {
            return "";
        }

        return  matter.formName;
    }

    public String getMatterDesc() {
        return matter != null ? matter.description : "";
    }

    public String getMatterFormCode() {
        if (matter == null) {
            return "";
        }

        return  matter.code;
    }

    public String getPartnerName() {
        if (partner == null) {
            return "";
        }

        return partner.nickName;
    }

    public String getPartnerCode() {
        return partner == null ? "" : partner.getCode();
    }

    public String getLegalAssistantName() {
        if (legalAssistant == null) {
            return  "";
        }

        return legalAssistant.nickName;
    }

    public String getLegalAssistantCode() {
        return legalAssistant != null ? legalAssistant.code : "";
    }

    public String getClerkName() {
        if (clerk == null) {
            return "";
        }

        return clerk.nickName;
    }

    public String getClerkCode() {
        return clerk != null ? clerk.code : "";
    }

    public String getCourt() {
        if (court == null) {
            return  "";
        }

        return court.Court;
    }

    public String getCourtPlace() {
        if (court == null) {
            return  "";
        }

        return court.Place;
    }

    public String getCourtCaseNo() {
        if (court == null) {
            return  "";
        }

        return court.CaseNo;
    }

    public String getCourtJudge() {
        if (court == null) {
            return  "";
        }

        return court.Judge;
    }

    public String getCourtSAR() {
        if (court == null) {
            return  "";
        }

        return court.SAR;
    }

    public String getCourtTypeNo() {
        return court == null ? "" :  court.PartyType;
    }

    public String getManualNo() {
        return manualNo == null ? "" : manualNo;
    }

    public void setManualNo(String manualNo) {
        this.manualNo = manualNo;
    }

    public String getBranchName() {
        return branch != null ? branch.city : "";
    }

    public String getBranchCode() {
        return branch != null ? branch.code : "";
    }

    public String getLACode() {
        return legalAssistant == null ? "" : legalAssistant.getCode();
    }
}

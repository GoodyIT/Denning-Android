package it.denning.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by denningit on 2018-01-16.
 */

public class MatterSimple implements Serializable {
    public String dateOpen;
    public String systemNo;
    public String referenceNo;
    public String manualNo;
    public MatterCodeModel matter;
    public CodeStrDescription presetBill;

    @SerializedName("partyGroup")
    public List<PartyGroup> partyGroups;

    public StaffModel primaryClient;
    public String rentalMonth;
    public String rentalPrice;
    public String spaLoan;
    public String spaPrice;
}

package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-17.
 */

public class BillModel implements Serializable {
    public String aging;
    public TaxInvoiceCalcModel analysis;
    public String documentNo;
    public String fileNo;
    public String isRental;
    public String issueBy;
    public String issueDate;
    public StaffModel issueTo1stCode;
    public String issueToName;
    public MatterCodeModel matter;
    public PresetCode presetCode;
    public String primaryClient;
    public String propertyTitle;
    public String relatedDocumentNo;
    public String rentalMonth;
    public String rentalPrice;
    public String spaAdj;
    public String spaLoan;
    public String spaPrice;

    public String getPresetCodeDesc() {
        return presetCode == null ? "" : presetCode.description;
    }
}

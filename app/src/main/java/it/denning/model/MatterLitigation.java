package it.denning.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by denningit on 2018-02-03.
 */

public class MatterLitigation implements Serializable {
    public Court courtInfo;
    public String dateOpen;
    public MatterCodeModel matter;
    public String manualNo;
    public List<PartyGroup> partyGroup;
    public StaffModel primaryClient;
    public String referenceNo;
    public String systemNo;

    public String getCaseNo() {
        return courtInfo != null ? courtInfo.CaseNo : "";
    }

    public String getCaseName() {
        return courtInfo != null ? courtInfo.CaseName : "";
    }
}

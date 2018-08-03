package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by denningit on 29/04/2017.
 */

public class LedgerDetail implements Serializable {
    public String ChequeNo;
    public String bankAcc;
    public String amountCR;
    public String amountDR;
    public String amount;
    public String code;
    public String date;
    public String fileNo;
    public String fileName;
    public String description;
    public String displayAmount;
    public String documentNo;
    public String drORcr;
    public String recdPaid;
    public String issuedBy;
    public String updatedBy;
    public String paymentMode;
    public String taxInvoice;
    public String isDebit;

    public LedgerDetail() {
        this.ChequeNo = "";
        this.bankAcc = "";
        this.amountCR = "";
        this.amountDR = "";
        this.amount = "";
    }
}

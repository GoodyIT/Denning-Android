package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-20.
 */

public class ReceiptModel implements Serializable{
    public String amount;
    public AccountType accountType;
    public String description;
    public String documentNo;
    public String fileNo;
    public String invoiceNo;
    public String receivedFrom;
    public String receivedFromName;
    public String remarks;
    public Payment payment;

    public ReceiptModel() {
        this.amount = "";
        this.accountType = new AccountType();
        this.description = "";
        this.documentNo = "";
        this.fileNo = "";
        this.invoiceNo = "";
        this.receivedFrom = "";
        this.receivedFromName = "";
        this.remarks = "";
        this.payment = new Payment();
    }


}

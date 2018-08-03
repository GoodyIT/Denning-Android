package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-20.
 */

public class Payment implements Serializable{
    public String bankBranch;
    public String issuerBank;
    public String mode;
    public String referenceNo;
    public String totalAmount;

    public Payment() {
        this.bankBranch = "";
        this.issuerBank = "";
        this.mode = "";
        this.referenceNo = "";
        this.totalAmount = "";
    }
}

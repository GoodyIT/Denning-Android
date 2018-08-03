package it.denning.model;

import java.io.Serializable;

/**
 * Created by denningit on 2018-01-17.
 */

public class TaxInvoiceItem implements Serializable{
    public String code;
    public String description;
    public String amount;
    public String rank;
    public String itemID;
}

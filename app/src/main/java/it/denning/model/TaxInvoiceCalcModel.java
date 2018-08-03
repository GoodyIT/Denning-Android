package it.denning.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by denningit on 2018-01-17.
 */

public class TaxInvoiceCalcModel implements Serializable{
    public List<TaxInvoiceItem> Disb;
    public List<TaxInvoiceItem> DisbGST;
    public List<TaxInvoiceItem> Fees;
    public List<TaxInvoiceItem> GST;

    public String decDisb;
    public String decDisbGST;
    public String decFees;
    public String decGST;
    public String decTotal;
}

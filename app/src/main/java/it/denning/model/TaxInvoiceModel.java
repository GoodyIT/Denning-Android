package it.denning.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import it.denning.general.DIHelper;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class TaxInvoiceModel implements Serializable {
    public String APIpdf;
    public String amount;
    public String fileName;
    public String fileNo;
    public String invoiceNo;
    public String issueToName;

}

package it.denning.navigation.dashboard.section4.viewquotation;

import android.content.Context;
import android.text.InputType;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddSectionItemModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.Mukim;
import it.denning.model.ProjectHousing;
import it.denning.model.Property;
import it.denning.model.BillModel;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */

public class ViewQuotationAdapter extends BaseSectionAdapter {
    private BillModel billModel;

    public ViewQuotationAdapter(Context context, BillModel billModel, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.billModel = billModel;
        titles = Arrays.asList(new String[]{"Quotation Details", "Quotation Analysis"});
        buildModel();
    }

    public String getFileNo() {
        return model.items.get(0).items.get(1).value;
    }

    public String getQuotationNo() {
        return model.items.get(0).items.get(0).value;
    }


    public JsonObject buildReceiptParams() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("documentNo", model.items.get(0).items.get(0).value);

        return NetworkManager.mergeJSONObjects(buildSaveParams(), jsonObject);
    }

    public JsonObject buildSaveParams() {
        JsonObject params = new JsonObject();

        params.addProperty("fileNo", model.items.get(0).items.get(1).value);
        params.addProperty("issueDate", DIHelper.todayWithTime());
        JsonObject issueTo =  new JsonObject();
        issueTo.addProperty("code", billModel.issueTo1stCode.code);
        params.add("issueTo1stCode", issueTo);
        params.addProperty("issueToName", model.items.get(0).items.get(3).value);
        JsonObject matter = new JsonObject();
        matter.addProperty("code", model.items.get(0).items.get(2).code);
        params.add("matter", matter);
        params.addProperty("relatedDocumentNo", model.items.get(0).items.get(0).value);

        return NetworkManager.mergeJSONObjects(buildCalcParams(), params);
    }

    public JsonObject buildBillParams() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("documentNo", model.items.get(0).items.get(0).label);
        return NetworkManager.mergeJSONObjects(buildSaveParams(), jsonObject);
    }

    public JsonObject buildCalcParams() {
        JsonObject jsonObject = new JsonObject();

        String firstValue = "0", secondValue= "0", thirdValue= "0", forthValue= "0";
        if (billModel.isRental.equals("0")) {
            firstValue = DIHelper.toNumber(model.items.get(0).items.get(5).value);
            secondValue = DIHelper.toNumber(model.items.get(0).items.get(6).value);
            thirdValue = "0";
            forthValue = "0";
        } else {
            thirdValue = DIHelper.toNumber(model.items.get(0).items.get(5).value);
            forthValue = DIHelper.toNumber(model.items.get(0).items.get(6).value);
            firstValue = "0";
            secondValue = "0";
        }
        jsonObject.addProperty("isRental", billModel.isRental);
        jsonObject.addProperty("spaPrice", firstValue);
        jsonObject.addProperty("spaLoan", secondValue);
        jsonObject.addProperty("rentalMonth", thirdValue);
        jsonObject.addProperty( "rentalPrice ", forthValue);
        JsonObject presetCode = new JsonObject();
        presetCode.addProperty( "code", model.items.get(0).items.get(4).code);
        jsonObject.add( "presetCode", presetCode);

        return jsonObject;
    }


    public void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();

        LabelValueDetail labelValueDetail = new LabelValueDetail("Quotation No", billModel.documentNo, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("File No.", billModel.fileNo, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Matter", billModel.matter.description, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Quotation to", billModel.primaryClient, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Preset Code", billModel.presetCode.description, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        if (billModel.isRental.equals("0")) {
            labelValueDetail = new LabelValueDetail("Price", billModel.spaPrice, DIConstants.GENERAL_TYPE);
            labelValueDetail.hasDetail = false;
            sectionItemModel.items.add(labelValueDetail);

            labelValueDetail = new LabelValueDetail("Loan", billModel.spaLoan, DIConstants.GENERAL_TYPE);
            labelValueDetail.hasDetail = false;
            sectionItemModel.items.add(labelValueDetail);
        } else {
            labelValueDetail = new LabelValueDetail("Month", billModel.rentalMonth, DIConstants.GENERAL_TYPE);
            labelValueDetail.hasDetail = false;
            sectionItemModel.items.add(labelValueDetail);

            labelValueDetail = new LabelValueDetail("Matter", billModel.rentalPrice, DIConstants.GENERAL_TYPE);
            labelValueDetail.hasDetail = false;
            sectionItemModel.items.add(labelValueDetail);
        }

        model.items.add(sectionItemModel);

        sectionItemModel = new AddSectionItemModel();

        labelValueDetail = new LabelValueDetail("Professional Fees", billModel.analysis.decFees, DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Disb. with GST", billModel.analysis.decDisbGST, DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Disbursement", billModel.analysis.decDisb, DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("GST", billModel.analysis.decGST, DIConstants.GENERAL_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Total", billModel.analysis.decTotal, DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("View Document", "", DIConstants.ONE_BUTTON_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Convert to Tax Invoice", "", DIConstants.ONE_BUTTON_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Issue Receipt", "", DIConstants.ONE_BUTTON_TYPE);
        sectionItemModel.items.add(labelValueDetail);

        model.items.add(sectionItemModel);

        notifyAllSectionsDataSetChanged();
    }

}


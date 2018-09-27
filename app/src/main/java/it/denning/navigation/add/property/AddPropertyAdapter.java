package it.denning.navigation.add.property;

import android.app.Activity;
import android.content.Context;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.MyCallbackInterface;
import it.denning.model.AddSectionItemModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.MasterTitle;
import it.denning.model.Mukim;
import it.denning.model.ProjectHousing;
import it.denning.model.Property;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-19.
 */

public class AddPropertyAdapter extends BaseSectionAdapter {
    private Property property;

    // Sections
    public int MAIN_SECTION = 0;
    public int TITLE_DETAILS =1;
    public int MORE_SARATA = 2;
    public int UNIT_PARCEL_DETAILS = 3;
    public int PROJECT = 4;

    // main section
    public int PROPERTY_TYPE = 0;
    public int INDIVIDUAL_STRATA_TITLE = 1;
    public int PROPERTY_ID = 2;

    // title details
    public int TITLE_TYPE_NO = 0;
    public int LOT_TYPE_NO = 1;
    public int MUKIM_TYPE_VALUE = 2;
    public int DAERAH = 3;
    public int NEGERI = 4;
    public int AREA_VALUE_TYPE = 5;
    public int TENURE = 6;
    public int LEASE_EXPIRY_DATE = 7;
    public int ADDRESS_PLACE = 8;
    public int RESTRICTION_IN_INTEREST = 9;
    public int RESTRICTION_AGAINST = 10;
    public int APPROVING_AUTHORITY = 11;
    public int CATEGORY_OF_LAND_USE = 12;

    // more sarata
    public int PARCEL_NO = 0;
    public int STOREY_NO = 1;
    public int BUILDING_NO = 2;
    public int ACCESSORY_PARCEL_NO = 3;
    public int ACCESSORY_STOREY_NO = 4;
    public int ACCESSORY_BUILDING_NO = 5;
    public int UNITES_OF_SHARES = 6;
    public int TOTAL_SHARES = 7;

    // Unite / Parcel Details
    public int PARCEL_TYPE_NO = 0;
    public int SPA_STOREY_NO = 1;
    public int BUILDING_BLOCK_NO = 2;
    public int APT_CONDO_NAME = 3;
    public int SPA_ACCESSORY_PARCEL_NO = 4;
    public int SPA_AREA_UNIT = 5;

    // project
    public int PROJECT_NAME = 0;
    public int DEVELOPER = 1;
    public int PROPRIETOR = 2;
    public int BLOCK_MASTER_TITLE = 3;

    public AddPropertyAdapter(Context context, Property property, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.property = property;
        titles = Arrays.asList(new String[]{"", "Title Details (if issued)", "More strata title details (if issued)", "Unit / Parcel Details (Per Principal SPA)", "Project"});
        buildModel();
    }

    public void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();

        LabelValueDetail labelValueDetail = new LabelValueDetail("Property Type", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.getPropertyTypeDesc();
            labelValueDetail.code = property.getPropertyTypeCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Individual / Sarata Title", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.getTitleIssuedDesc();
            labelValueDetail.code = property.getTitleIssuedCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("ID (System assigned)", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (property != null) {
            labelValueDetail.value = property.propertyID;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Title Details
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Title Type", "", true);
        if (property != null) {
            labelValueDetail.leftView.value = property.getTitleType();
        }
        labelValueDetail.rightView = new LabelValueDetail("Title No", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getTitleValue();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Lot Type", "", true);
        if (property != null) {
            labelValueDetail.leftView.value = property.getLotPTType();
        }
        labelValueDetail.rightView = new LabelValueDetail("Lot/PT No", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getLotPTValue();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_DETAIL_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Mukim Type", "", true);
        if (property != null) {
            labelValueDetail.leftView.value = property.getMukimType();
        }
        labelValueDetail.rightView = new LabelValueDetail("Mukim", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getMukimValue();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Daerah", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (property != null) {
            labelValueDetail.value = property.daerah;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Negeri", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (property != null) {
            labelValueDetail.value = property.daerah;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Area Value", "", true);
        labelValueDetail.leftView.inputType = InputType.TYPE_CLASS_NUMBER;
        if (property != null) {
            labelValueDetail.leftView.value = property.getAreaValue();
        }
        labelValueDetail.rightView = new LabelValueDetail("Area Type", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getAreaType();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Tenure", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.tenure;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Lease Expiry Date", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = DIHelper.getOnlyDateFromDateTime(property.leaseExpiryDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Address / Place", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.address;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Restriction in Interest", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.getRestrictionInInterestValue();
            labelValueDetail.code = property.getRestrictionInInterestCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Restriction Against", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.restrictionAgainst;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Approving Authority", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.approvingAuthority;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Category of Land Use", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.landUse;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // More Sarata Title Details
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Parcel No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.parcelNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Storey No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.storeyNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Building No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.buildingNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Accessory Parcel No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.accParcelNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Accessory Storey No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.accStoreyNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Accessory Building No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.accBuildingNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Unit of Shares", "", DIConstants.INPUT_TYPE);
        labelValueDetail.inputType = InputType.TYPE_CLASS_NUMBER;
        if (property != null) {
            labelValueDetail.value = property.unitShare;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Total Shares", "", DIConstants.INPUT_TYPE);
        labelValueDetail.inputType = InputType.TYPE_CLASS_NUMBER;
        if (property != null) {
            labelValueDetail.value = property.totalShare;
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Unit Parcel Details
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_LEFT_DETAIL_RIGHT_INPUT_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Parcel Type", "", true);
        if (property != null) {
            labelValueDetail.leftView.value = property.getSPAParcelType();
        }
        labelValueDetail.rightView = new LabelValueDetail("Unit/Parcel No.", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getSPAParcelValue();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Storey No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.spaStoreyNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Building/Block No.", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.spaBuildingNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Apt/Condo Name", "", DIConstants.INPUT_TYPE);
        if (property != null) {
            labelValueDetail.value = property.spaCondoName;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Accessory Parcel No.", "", DIConstants.INPUT_TYPE);
        labelValueDetail.inputType = InputType.TYPE_CLASS_NUMBER;
        if (property != null) {
            labelValueDetail.value = property.spaAccParcelNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_LEFT_INPUT_RIGHT_DETAIL_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Spa Area ", "", true);
        labelValueDetail.leftView.inputType = InputType.TYPE_CLASS_NUMBER;
        if (property != null) {
            labelValueDetail.leftView.value = property.getSPAAreaValue();
        }
        labelValueDetail.rightView = new LabelValueDetail("Measurement Unit", "", false);
        if (property != null) {
            labelValueDetail.rightView.value = property.getSPAAreaType();
        }
        sectionItemModel.items.add(labelValueDetail);
        model.items.add(sectionItemModel);

        // Project
        sectionItemModel = new AddSectionItemModel();
        labelValueDetail = new LabelValueDetail("Project Name", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.getProjectName();
            labelValueDetail.code = property.getProjectCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Developer", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (property != null) {
            labelValueDetail.value = property.getDeveloperName();
            labelValueDetail.code = property.getDeveloperCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Proprietor", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (property != null) {
            labelValueDetail.value = property.getProprietorName();
            labelValueDetail.code = property.getProprietorCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Block/Master Title", "", DIConstants.GENERAL_TYPE);
        if (property != null) {
            labelValueDetail.value = property.getMasterTitleFullTitle();
            labelValueDetail.code = property.getMasterTitleCode();
        }
        sectionItemModel.items.add(labelValueDetail);

        model.items.add(sectionItemModel);

        if (property == null) {
            property = new Property();
        }

        notifyAllSectionsDataSetChanged();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        if (sectionIndex == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        super.updateDataFromInput(input, sectionIndex, itemIndex);

        if (input.trim().length() == 0) {
            return;
        }

        if (sectionIndex == TITLE_DETAILS) {
            if (itemIndex == TITLE_TYPE_NO || itemIndex == LOT_TYPE_NO || itemIndex == MUKIM_TYPE_VALUE) {
                updateLeftRightInput(input, sectionIndex, itemIndex, 1);
            } else if (itemIndex == AREA_VALUE_TYPE) {
                input = DIHelper.addThousandsSeparator(input);
                updateLeftRightInput(input, sectionIndex, itemIndex, 0);
            } else {
                model.items.get(sectionIndex).items.get(itemIndex).value = input;
            }
        } if (sectionIndex == MORE_SARATA) {
            if (itemIndex == UNITES_OF_SHARES || itemIndex == TOTAL_SHARES) {
                input = DIHelper.addThousandsSeparator(input);
            }
            model.items.get(sectionIndex).items.get(itemIndex).value = input;
        } else if (sectionIndex == UNIT_PARCEL_DETAILS) {
            if (itemIndex == PARCEL_TYPE_NO) {
                updateLeftRightInput(input, sectionIndex, itemIndex, 1);
            } else if (itemIndex == SPA_AREA_UNIT) {
                input = DIHelper.addThousandsSeparator(input);
                updateLeftRightInput(input, sectionIndex, itemIndex, 0);
            } else {
                model.items.get(sectionIndex).items.get(itemIndex).value = input;
            }
        } else {
            model.items.get(sectionIndex).items.get(itemIndex).value = input;
        }
    }

    @Override
    public void updateLeftRightInput(String input, final int sectionIndex, final int itemIndex, int twoColumn) {
        if (twoColumn == 0) {
            model.items.get(sectionIndex).items.get(itemIndex).leftView.value = input;
        } else {
            model.items.get(sectionIndex).items.get(itemIndex).rightView.value = input;
        }
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run(){
                //change adapter contents
                notifySectionItemChanged(sectionIndex, itemIndex);
            }
        }, 300);
    }

    public boolean isValidProceed() {
        if (getValue(MAIN_SECTION, PROPERTY_TYPE).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_property_type);
            return false;
        }

        if (getValue(MAIN_SECTION, INDIVIDUAL_STRATA_TITLE).trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_individual_title);
            return false;
        }

        if (getCode(MAIN_SECTION, INDIVIDUAL_STRATA_TITLE).equals("1")) { // Issued
            if (getLeftValue(TITLE_DETAILS, TITLE_TYPE_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_title_type);
                return false;
            }
            if (getRightValue(TITLE_DETAILS, TITLE_TYPE_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_title_no);
                return false;
            }
            if (getLeftValue(TITLE_DETAILS, LOT_TYPE_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_lot_type);
                return false;
            }
            if (getRightValue(TITLE_DETAILS, LOT_TYPE_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_lot_no);
                return false;
            }
        } else {
            if (getRightValue(UNIT_PARCEL_DETAILS, PARCEL_TYPE_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_parcel_type);
                return false;
            }

            if (getValue(MORE_SARATA, PARCEL_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_parcel_no);
                return false;
            }

            if (getValue(MORE_SARATA, STOREY_NO).trim().length() == 0) {
                DIAlert.showSimpleAlert(context, R.string.alert_storey_no);
                return false;
            }
        }

        return true;
    }

    public void updateMukim(Mukim mukim, int sectionIndex) {
        model.items.get(sectionIndex).items.get(MUKIM_TYPE_VALUE).rightView.value = mukim.mukim;
        notifySectionItemChanged(sectionIndex, MUKIM_TYPE_VALUE);
        updateDataAndRefresh(mukim.daerah, sectionIndex, DAERAH);
        updateDataAndRefresh(mukim.negeri, sectionIndex, NEGERI);
    }

    public void updateProjectHousing(ProjectHousing projectHousing, int sectionIndex) {
        updateCodeDescData(new CodeDescription(projectHousing.code, projectHousing.name), sectionIndex, PROJECT_NAME);
        updateCodeDescData(new CodeDescription(projectHousing.getDevCode(), projectHousing.getDevname()), sectionIndex, DEVELOPER);
        updateCodeDescData(new CodeDescription(projectHousing.getProprietorCode(), projectHousing.getProprietorname()), sectionIndex, PROPRIETOR);
        updateDataAndRefresh(projectHousing.masterTitle, sectionIndex, BLOCK_MASTER_TITLE);
    }

    private boolean isEqual(String value1, String value2) {
        value1 = value1 == null ? "" : value1;
        value2 = value2 == null ? "" : value2;

        return value1.equals(value2);
    }

    @Override
    protected JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        List<LabelValueDetail> main = model.items.get(MAIN_SECTION).items;
        JsonObject code = new JsonObject();
        if (!isEqual(main.get(PROPERTY_TYPE).code, property.getPropertyTypeCode())) {
            code.addProperty("code", main.get(PROPERTY_TYPE).code);
            params.add("propertyType", code);
        }

        code = new JsonObject();
        if (!isEqual(main.get(INDIVIDUAL_STRATA_TITLE).code, property.getTitleIssuedCode())) {
            code.addProperty("code", main.get(INDIVIDUAL_STRATA_TITLE).code);
            params.add("titleIssued", code);
        }

        // Title Details
        List<LabelValueDetail> titleDetails = model.items.get(TITLE_DETAILS).items;
        JsonObject newObject = new JsonObject();
        boolean isChanged = false;
        if (!isEqual(titleDetails.get(TITLE_TYPE_NO).leftView.value, property.getTitleType())) {
            newObject.addProperty("type", titleDetails.get(TITLE_TYPE_NO).leftView.value);
            isChanged = true;
        }
        if (!isEqual(titleDetails.get(TITLE_TYPE_NO).rightView.value, property.getTitleValue())) {
            newObject.addProperty("value", titleDetails.get(TITLE_TYPE_NO).rightView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("title", newObject);
        }

        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(titleDetails.get(LOT_TYPE_NO).leftView.value, property.getLotPTType())) {
            newObject.addProperty("type", titleDetails.get(LOT_TYPE_NO).leftView.value);
            isChanged = true;
        }
        if (!isEqual(titleDetails.get(LOT_TYPE_NO).rightView.value, property.getLotPTValue())) {
            newObject.addProperty("value", titleDetails.get(LOT_TYPE_NO).rightView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("lotPT", newObject);
        }

        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(titleDetails.get(MUKIM_TYPE_VALUE).leftView.value, property.getMukimType())) {
            newObject.addProperty("value", titleDetails.get(MUKIM_TYPE_VALUE).leftView.value);
            isChanged = true;
        }
        if (!isEqual(titleDetails.get(MUKIM_TYPE_VALUE).rightView.value, property.getMukimValue())) {
            newObject.addProperty("type", titleDetails.get(MUKIM_TYPE_VALUE).rightView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("mukim", newObject);
        }

        if (!isEqual(titleDetails.get(DAERAH).value, property.daerah)) {
            params.addProperty("daerah", titleDetails.get(DAERAH).value);
        }

        if (!isEqual(titleDetails.get(NEGERI).value, property.negeri)) {
            params.addProperty("negeri", titleDetails.get(NEGERI).value);
        }

        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(titleDetails.get(AREA_VALUE_TYPE).rightView.value, property.getAreaType())) {
            newObject.addProperty("type", titleDetails.get(AREA_VALUE_TYPE).rightView.value);
            isChanged = true;
        }
        if (!isEqual(titleDetails.get(AREA_VALUE_TYPE).leftView.value, property.getAreaValue())) {
            newObject.addProperty("value", titleDetails.get(AREA_VALUE_TYPE).leftView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("area", newObject);
        }

        if (!isEqual(titleDetails.get(TENURE).value, property.tenure)) {
            params.addProperty("tenure", titleDetails.get(TENURE).value);
        }

        if (!isEqual(DIHelper.toMySQLDateFormat(titleDetails.get(LEASE_EXPIRY_DATE).value), property.leaseExpiryDate)) {
            params.addProperty("tenure", DIHelper.toMySQLDateFormat(titleDetails.get(LEASE_EXPIRY_DATE).value));
        }

        if (!isEqual(titleDetails.get(ADDRESS_PLACE).value, property.address)) {
            params.addProperty("address", titleDetails.get(ADDRESS_PLACE).value);
        }

        code = new JsonObject();
        if (!isEqual(titleDetails.get(RESTRICTION_IN_INTEREST).code, property.getRestrictionInInterestCode())) {
            code.addProperty("code", titleDetails.get(RESTRICTION_IN_INTEREST).code);
            params.add("restrictionInInterest", code);
        }

        if (!isEqual(titleDetails.get(RESTRICTION_AGAINST).value, property.restrictionAgainst)) {
            params.addProperty("restrictionAgainst", titleDetails.get(RESTRICTION_AGAINST).value);
        }

        if (!isEqual(titleDetails.get(APPROVING_AUTHORITY).value, property.approvingAuthority)) {
            params.addProperty("approvingAuthority", titleDetails.get(APPROVING_AUTHORITY).value);
        }

        if (!isEqual(titleDetails.get(CATEGORY_OF_LAND_USE).value, property.landUse)) {
            params.addProperty("landUse", titleDetails.get(CATEGORY_OF_LAND_USE).value);
        }

        // More Sarata
        List<LabelValueDetail> moreSarata = model.items.get(MORE_SARATA).items;
        if (!isEqual(moreSarata.get(PARCEL_NO).value, property.parcelNo)) {
            params.addProperty("parcelNo", moreSarata.get(PARCEL_NO).value);
        }

        if (!isEqual(moreSarata.get(STOREY_NO).value, property.storeyNo)) {
            params.addProperty("storeyNo", moreSarata.get(STOREY_NO).value);
        }

        if (!isEqual(moreSarata.get(BUILDING_NO).value, property.buildingNo)) {
            params.addProperty("buildingNo", moreSarata.get(BUILDING_NO).value);
        }

        if (!isEqual(moreSarata.get(ACCESSORY_PARCEL_NO).value, property.accParcelNo)) {
            params.addProperty("accParcelNo", moreSarata.get(ACCESSORY_PARCEL_NO).value);
        }

        if (!isEqual(moreSarata.get(ACCESSORY_STOREY_NO).value, property.accStoreyNo)) {
            params.addProperty("accStoreyNo", moreSarata.get(ACCESSORY_STOREY_NO).value);
        }

        if (!isEqual(moreSarata.get(ACCESSORY_BUILDING_NO).value, property.accBuildingNo)) {
            params.addProperty("accBuildingNo", moreSarata.get(ACCESSORY_BUILDING_NO).value);
        }

        if (!isEqual(moreSarata.get(UNITES_OF_SHARES).value, property.unitShare)) {
            params.addProperty("unitShare", moreSarata.get(UNITES_OF_SHARES).value);
        }

        if (!isEqual(moreSarata.get(TOTAL_SHARES).value, property.totalShare)) {
            params.addProperty("totalShare", moreSarata.get(TOTAL_SHARES).value);
        }

        // Unit / parcel Details
        List<LabelValueDetail> unitParcelDetails = model.items.get(UNIT_PARCEL_DETAILS).items;
        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(unitParcelDetails.get(PARCEL_TYPE_NO).leftView.value, property.getSPAParcelType())) {
            newObject.addProperty("type", unitParcelDetails.get(PARCEL_TYPE_NO).leftView.value);
            isChanged = true;
        }
        if (!isEqual(unitParcelDetails.get(PARCEL_TYPE_NO).rightView.value, property.getSPAParcelValue())) {
            newObject.addProperty("value", unitParcelDetails.get(PARCEL_TYPE_NO).rightView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("spaParcel", newObject);
        }

        if (!isEqual(unitParcelDetails.get(SPA_STOREY_NO).value, property.spaStoreyNo)) {
            params.addProperty("spaStoreyNo", unitParcelDetails.get(SPA_STOREY_NO).value);
        }

        if (!isEqual(unitParcelDetails.get(BUILDING_BLOCK_NO).value, property.accBuildingNo)) {
            params.addProperty("spaBuildingNo", unitParcelDetails.get(BUILDING_BLOCK_NO).value);
        }

        if (!isEqual(unitParcelDetails.get(APT_CONDO_NAME).value, property.spaCondoName)) {
            params.addProperty("spaCondoName", unitParcelDetails.get(APT_CONDO_NAME).value);
        }

        if (!isEqual(unitParcelDetails.get(SPA_ACCESSORY_PARCEL_NO).value, property.spaAccParcelNo)) {
            params.addProperty("spaAccParcelNo", unitParcelDetails.get(SPA_ACCESSORY_PARCEL_NO).value);
        }

        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(unitParcelDetails.get(SPA_AREA_UNIT).leftView.value, property.getSPAAreaType())) {
            newObject.addProperty("value", unitParcelDetails.get(SPA_AREA_UNIT).leftView.value);
            isChanged = true;
        }
        if (!isEqual(unitParcelDetails.get(SPA_AREA_UNIT).rightView.value, property.getSPAAreaValue())) {
            newObject.addProperty("type", unitParcelDetails.get(SPA_AREA_UNIT).rightView.value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("spaArea", newObject);
        }

        // Project
        List<LabelValueDetail> project = model.items.get(PROJECT).items;
        newObject = new JsonObject();
        isChanged = false;
        if (!isEqual(project.get(PROJECT_NAME).code, property.getProjectCode())) {
            newObject.addProperty("code", project.get(PROJECT_NAME).code);
            isChanged = true;
        }
        if (!isEqual(project.get(DEVELOPER).code, property.getDeveloperCode())) {
            code = new JsonObject();
            code.addProperty("code", project.get(DEVELOPER).code);
            newObject.add("developer", code);
            isChanged = true;
        }
        if (!isEqual(project.get(PROPRIETOR).code, property.getProprietorCode())) {
            code = new JsonObject();
            code.addProperty("code", project.get(PROPRIETOR).code);
            newObject.add("proprietor", code);
            isChanged = true;
        }
        if (!isEqual(project.get(BLOCK_MASTER_TITLE).value, property.getProjectMasterTitle())) {
            newObject.addProperty("masterTitle", project.get(BLOCK_MASTER_TITLE).value);
            isChanged = true;
        }
        if (isChanged) {
            params.add("project", newObject);
        }

        return params;
    }

    @Override
    protected JsonObject buildUpdateParam() {
        JsonObject param = new JsonObject();
        param.addProperty("code", property.code);

        return NetworkManager.mergeJSONObjects(param, buildSaveParam());
    }

    public void updateID(String ID) {
        updateDataAndRefresh(ID, MAIN_SECTION, PROPERTY_ID);
    }
}


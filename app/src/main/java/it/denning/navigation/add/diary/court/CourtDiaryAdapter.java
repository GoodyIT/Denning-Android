package it.denning.navigation.add.diary.court;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.List;

import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddSectionItemModel;
import it.denning.model.CodeDescription;
import it.denning.model.EditCourtModel;
import it.denning.model.LabelValueDetail;
import it.denning.navigation.add.diary.BaseDiaryAdapter;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */



public class CourtDiaryAdapter extends BaseDiaryAdapter {
    protected int COURT = 5;
    protected int ENCLOSURE_NO = 6;
    protected int NATURE_OF_HEARING = 7;
    protected int DETAILS = 8;
    protected int COUNCIL_ASSIGNED = 9;
    protected int REMARKS = 10;

    protected int ATTENDED_STATUS = 10;
    protected int COUNSEL_ATTENDED = 11;
    protected int CORAM = 12;
    protected int OPPONENT_COUNSEL = 13;
    protected int COURT_DECISION = 14;
    protected int NEXT_DATE_TYPE = 16;

    // Next Date Details
    protected int NEXT_ENCLOSURE_NO = 2;
    protected int NEXT_NATURE_OF_HEARING = 3;
    protected int NEXT_DETAILS = 4;

    protected EditCourtModel courtModel;

    public CourtDiaryAdapter(Context context, EditCourtModel courtModel, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.courtModel = courtModel;
        FILE_NO = 0;
        CASE_NO = 1;
        CASE_NAME = 2;
        START_DATE_TIME = 3;
        END_DATE_TIME = 4;

        if (courtModel == null) {
            REMARKS = 10;
        } else {
            REMARKS = 15;
        }
        buildModel();
    }

    protected void buildModel() {

        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail = new LabelValueDetail("File No", "", DIConstants.GENERAL_TYPE);
        if (courtModel != null) {
            labelValueDetail.value = courtModel.fileNo1;
        }
        sectionItemModel.items.add(labelValueDetail);
        labelValueDetail = new LabelValueDetail("Case No", "", DIConstants.GENERAL_TYPE);
        if (courtModel != null) {
            labelValueDetail.value = courtModel.caseNo;
        }
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);
        labelValueDetail = new LabelValueDetail("Case Name", "", DIConstants.GENERAL_TYPE);
        if (courtModel != null) {
            labelValueDetail.value = courtModel.caseName;
        }
        labelValueDetail.hasDetail = false;
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Start Date", "", false);
        if (courtModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(courtModel.hearingStartDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (courtModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(courtModel.hearingStartDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("End Date", "", false);
        if (courtModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(courtModel.hearingEndDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (courtModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(courtModel.hearingEndDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Court", "", false);
        if (courtModel != null) {
            labelValueDetail.leftView.value = courtModel.court.typeE;
            labelValueDetail.code = courtModel.court.code;
        }
        labelValueDetail.rightView = new LabelValueDetail("Place", "", false);
        if (courtModel != null) {
            labelValueDetail.rightView.value = courtModel.court.place;
        }
        sectionItemModel.items.add(labelValueDetail);

        sectionItemModel.items.add(new LabelValueDetail("Enclosure No", "", DIConstants.INPUT_TYPE));
        if (courtModel != null) {
            labelValueDetail.value = courtModel.enclosureNo;
        }
        sectionItemModel.items.add(new LabelValueDetail("Nature of Hearing", "", DIConstants.GENERAL_TYPE));
        if (courtModel != null) {
            labelValueDetail.value = courtModel.hearingType;
        }
        sectionItemModel.items.add(new LabelValueDetail("Details", "", DIConstants.GENERAL_TYPE));
        if (courtModel != null) {
            labelValueDetail.value = courtModel.enclosureDetails;
        }
        sectionItemModel.items.add(new LabelValueDetail("Council Assigned", "", DIConstants.GENERAL_TYPE));
        if (courtModel != null) {
            labelValueDetail.value = courtModel.getCounselAssignedName();
            labelValueDetail.code = courtModel.getCounselAssignedCode();
        }

        if (courtModel == null) {
            sectionItemModel.items.add(new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE));
        } else {
            sectionItemModel.items.add(new LabelValueDetail("Attendant Type", courtModel.getAttendedStatusDesc(), courtModel.getAttendedStatusCode(), DIConstants.GENERAL_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Counsel Attended", courtModel.counselAttended, DIConstants.GENERAL_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Coram", courtModel.getCoramName(), courtModel.getCoramCode(), DIConstants.GENERAL_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Opponent's Counsel", courtModel.opponentCounsel, DIConstants.INPUT_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Court Decision", courtModel.courtDecision, DIConstants.GENERAL_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE));
            sectionItemModel.items.add(new LabelValueDetail("Next Date Type", courtModel.getNextDateTypeDesc(), courtModel.getNextDateTypeCode(), DIConstants.GENERAL_TYPE));
        }

        // Next Date Details
        model.items.add(sectionItemModel);
        if (isNextDetailRequired()) {
           addBelowSection();
        }

        notifyAllSectionsDataSetChanged();
    }

    public void updateModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Next Start Date", DIHelper.getOnlyDateFromDateTime(courtModel.hearingStartDate), false);
        labelValueDetail.rightView = new LabelValueDetail("Time", DIHelper.getTime(courtModel.hearingStartDate), false);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Next End Date", DIHelper.getOnlyDateFromDateTime(courtModel.hearingEndDate), false);
        labelValueDetail.rightView = new LabelValueDetail("Time", DIHelper.getTime(courtModel.hearingEndDate), false);
        sectionItemModel.items.add(labelValueDetail);

        sectionItemModel.items.add(new LabelValueDetail("Enclosure No", courtModel.enclosureNo, DIConstants.INPUT_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Next Nature of Hearing", courtModel.hearingType, DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Next Details", courtModel.enclosureDetails, DIConstants.GENERAL_TYPE));

        model.items.add(sectionItemModel);
    }

    public void addBelowSection() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Next Start Date", DIHelper.getOnlyDateFromDateTime(courtModel.nextStartDate), false);
        labelValueDetail.rightView = new LabelValueDetail("Time", DIHelper.getTime(courtModel.nextStartDate), false);
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Next End Date", DIHelper.getOnlyDateFromDateTime(courtModel.nextEndDate), false);
        labelValueDetail.rightView = new LabelValueDetail("Time", DIHelper.getTime(courtModel.nextEndDate), false);
        sectionItemModel.items.add(labelValueDetail);

        sectionItemModel.items.add(new LabelValueDetail("Enclosure No", "", DIConstants.INPUT_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Next Nature of Hearing", "", DIConstants.GENERAL_TYPE));
        sectionItemModel.items.add(new LabelValueDetail("Next Details", "", DIConstants.GENERAL_TYPE));

        model.items.add(sectionItemModel);
    }

    public boolean isNextDetailRequired() {
        return courtModel != null && courtModel.getNextDateTypeCode().equals("0") ? true : false;
    }

    @Override
    protected JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;

        params.addProperty("chkDone", "0");
        JsonObject zeroCode = new JsonObject();
        zeroCode.addProperty("code", "0");
        params.add("attendedStatus", zeroCode);
        params.add("coram", zeroCode);
        JsonObject code = new JsonObject();
        code.addProperty("code", detailsAppointment.get(COUNCIL_ASSIGNED).code);
        params.add("counselAssigned", code);

        code = new JsonObject();
        code.addProperty("code", detailsAppointment.get(COURT).code);
        params.add("court", code);
        params.addProperty("courtDecision", "");
        params.addProperty("enclosureDetails", detailsAppointment.get(DETAILS).value);
        params.addProperty("enclosureNo",  detailsAppointment.get(ENCLOSURE_NO).value);
        params.addProperty("fileNo1", detailsAppointment.get(FILE_NO).value);
        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value  +
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        params.addProperty("hearingStartDate", startDate);
        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value  +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value);
        params.addProperty("hearingEndDate", endDate);
        params.addProperty("hearingType", detailsAppointment.get(NATURE_OF_HEARING).value);
        params.add("nextDateType", zeroCode);
        params.addProperty("opponentCounsel", "");
        params.addProperty("previousDate", "2000-01-01 00:00:00");
        params.addProperty("remarks", detailsAppointment.get(REMARKS).value);

        return params;
    }

    @Override
    public void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        if (isNextDetailRequired()) {
            return;
        }

        super.updateDataFromInput(input, sectionIndex, itemIndex);
    }

    @Override
    public void updateCodeDescData(CodeDescription codeDescription, int sectionIndex, int itemIndex) {
        super.updateCodeDescData(codeDescription, sectionIndex, itemIndex);
        if (sectionIndex == DETAILS_OF_APPOINTMENT && itemIndex == NEXT_DATE_TYPE) {
            updateModel();
            notifySectionInserted(NEXT_DATE_DETAILS);
        }
    }

    @Override
    protected JsonObject buildUpdateParam() {
        JsonObject params = new JsonObject();

        params.addProperty("code", courtModel.code);

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;
        JsonObject code = new JsonObject();
        if (!detailsAppointment.get(ATTENDED_STATUS).code.equals(courtModel.getAttendedStatusCode())) {
            code.addProperty("code", detailsAppointment.get(ATTENDED_STATUS).code);
            params.add("attendedStatus", code);
        }

        if (!detailsAppointment.get(CORAM).code.equals(courtModel.getCoramCode())) {
            code = new JsonObject();
            code.addProperty("code", detailsAppointment.get(CORAM).code);
            params.add("coram", code);
        }

        if (!detailsAppointment.get(COUNCIL_ASSIGNED).code.equals(courtModel.getCounselAssignedCode())) {
            code = new JsonObject();
            code.addProperty("code", detailsAppointment.get(COUNCIL_ASSIGNED).code);
            params.add("counselAssigned", code);
        }

        if (!detailsAppointment.get(COUNSEL_ATTENDED).value.equals(courtModel.counselAttended)) {
            params.addProperty("counselAttended", detailsAppointment.get(COUNSEL_ATTENDED).value);
        }

        if (!detailsAppointment.get(COURT).rightView.code.equals(courtModel.getCourtCode())) {
            code = new JsonObject();
            code.addProperty("code", detailsAppointment.get(COURT).code);
            params.add("court", code);
        }

        if (!detailsAppointment.get(COURT_DECISION).value.equals(courtModel.courtDecision)) {
            params.addProperty("courtDecision", detailsAppointment.get(COURT_DECISION).value);
        }

        if (!detailsAppointment.get(DETAILS).value.equals(courtModel.enclosureDetails)) {
            params.addProperty("enclosureDetails", detailsAppointment.get(DETAILS).value);
        }

        if (!detailsAppointment.get(ENCLOSURE_NO).value.equals(courtModel.enclosureNo)) {
            params.addProperty("enclosureNo", detailsAppointment.get(ENCLOSURE_NO).value);
        }

        if (!detailsAppointment.get(FILE_NO).value.equals(courtModel.fileNo1)) {
            params.addProperty("fileNo1", detailsAppointment.get(FILE_NO).value);
        }

        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value+
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        if (!startDate.equals(courtModel.hearingStartDate)) {
            params.addProperty("hearingStartDate", startDate);
        }

        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value  +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value);
        if (!endDate.equals(courtModel.hearingEndDate)) {
            params.addProperty("hearingEndDate", endDate);
        }

        if (!detailsAppointment.get(NATURE_OF_HEARING).value.equals(courtModel.hearingType)) {
            params.addProperty("hearingType", detailsAppointment.get(NATURE_OF_HEARING).value);
        }

        if (!detailsAppointment.get(OPPONENT_COUNSEL).value.equals(courtModel.opponentCounsel)) {
            params.addProperty("opponentCounsel", detailsAppointment.get(OPPONENT_COUNSEL).value);
        }

        if (!detailsAppointment.get(REMARKS).value.equals(courtModel.remarks)) {
            params.addProperty("remarks", detailsAppointment.get(REMARKS).value);
        }

        if (!detailsAppointment.get(NEXT_DATE_TYPE).code.equals(courtModel.getNextDateTypeCode())) {
            code = new JsonObject();
            code.addProperty("code", detailsAppointment.get(NEXT_DATE_TYPE).code);
            params.add("nextDateType", code);
        }

//        params.addProperty("previousDate", courtModel.previousDate);
        if (model.items.size() > 1) {
            List<LabelValueDetail> nextDateDetails = model.items.get(NEXT_DATE_DETAILS).items;
            String nextStartDate = DIHelper.toMySQLDateFormat2(nextDateDetails.get(NEXT_START_DATE_TIME).leftView.value + " " + nextDateDetails.get(NEXT_START_DATE_TIME).rightView.value)
                    ;
            params.addProperty("nextStartDate", nextStartDate);

            String nexEndDate = DIHelper.toMySQLDateFormat2(nextDateDetails.get(NEXT_END_DATE_TIME).leftView.value  +
                    " " + nextDateDetails.get(NEXT_END_DATE_TIME).rightView.value);
            params.addProperty("nexEndDate", nexEndDate);

            params.addProperty("next_hearingType", nextDateDetails.get(NEXT_NATURE_OF_HEARING).value);
            params.addProperty("next_enclosureNo", nextDateDetails.get(NEXT_ENCLOSURE_NO).value);
            params.addProperty("next_enclosureDetails", nextDateDetails.get(NEXT_ENCLOSURE_NO).value);
        }

        return params;
    }
}


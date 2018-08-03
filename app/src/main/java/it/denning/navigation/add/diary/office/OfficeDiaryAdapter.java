package it.denning.navigation.add.diary.office;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.List;

import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddSectionItemModel;
import it.denning.model.LabelValueDetail;
import it.denning.model.OfficeDiaryModel;
import it.denning.navigation.add.diary.BaseDiaryAdapter;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */



public class OfficeDiaryAdapter extends BaseDiaryAdapter {
    protected int APPOINTMENT = 0;
    protected int START_DATE_TIME = 1;
    protected int END_DATE_TIME = 2;
    protected int PLACE = 3;
    protected int STAFF_ASSIGNED = 7;
    protected int STAFF_ATTENDED = 8;
    protected int REMARKS = 9;
    protected int ATTENDED_STATUS = 9;

    private OfficeDiaryModel officeDiaryModel;

    public OfficeDiaryAdapter(Context context, OfficeDiaryModel officeDiaryModel, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.officeDiaryModel = officeDiaryModel;
        START_DATE_TIME = 1;
        END_DATE_TIME = 2;
        FILE_NO = 4;
        CASE_NO = 5;
        CASE_NAME = 6;
        if (officeDiaryModel != null) {
            REMARKS = 10;
        }
        buildModel();
    }

    protected void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        LabelValueDetail labelValueDetail  = new LabelValueDetail("Appointment", "", DIConstants.GENERAL_TYPE);
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.appointmentDetails;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Start Date", "", false);
        if (officeDiaryModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(officeDiaryModel.startDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (officeDiaryModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(officeDiaryModel.startDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("End Date", "", false);
        if (officeDiaryModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(officeDiaryModel.endDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (officeDiaryModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(officeDiaryModel.endDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Place", "", DIConstants.GENERAL_TYPE);
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.place;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("File No (if relevant)", "", DIConstants.GENERAL_TYPE);
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.fileNo1;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Case No", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.caseNo;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Case Name", "", DIConstants.GENERAL_TYPE);
        labelValueDetail.hasDetail = false;
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.caseName;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Staff Assigned", "", DIConstants.GENERAL_TYPE);
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.staffAssigned.name;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Staff Attended", "", DIConstants.GENERAL_TYPE);
        if (officeDiaryModel != null) {
            labelValueDetail.value = officeDiaryModel.staffAttended;
        }
        sectionItemModel.items.add(labelValueDetail);

        if (officeDiaryModel == null) {
            labelValueDetail = new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE);
            sectionItemModel.items.add(labelValueDetail);
        } else {
            labelValueDetail = new LabelValueDetail("Attendant Type", "", DIConstants.GENERAL_TYPE);
            if (officeDiaryModel != null) {
                labelValueDetail.value = officeDiaryModel.attendedStatus.description;
                labelValueDetail.code = officeDiaryModel.attendedStatus.code;
            }
            sectionItemModel.items.add(labelValueDetail);

            labelValueDetail = new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE);
            if (officeDiaryModel != null) {
                labelValueDetail.value = officeDiaryModel.remarks;
            }
            sectionItemModel.items.add(labelValueDetail);
        }

        model.items.add(sectionItemModel);

        notifyAllSectionsDataSetChanged();
    }

    @Override
    protected JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;
        params.addProperty("appointmentDetails", detailsAppointment.get(APPOINTMENT).value);
        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        params.addProperty("startDate", startDate);
        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value);
        params.addProperty("endDate", endDate);
        params.addProperty("place", detailsAppointment.get(PLACE).value);
        params.addProperty("fileNo1", detailsAppointment.get(FILE_NO).value);
        JsonObject code = new JsonObject();
        code.addProperty("code", detailsAppointment.get(STAFF_ASSIGNED).code);
        params.add("staffAssigned", code);
        code = new JsonObject();
        code.addProperty("code", detailsAppointment.get(STAFF_ATTENDED).code);
        params.add("attendedStatus", code);
        params.addProperty("courtDecision", "");
        params.addProperty("remarks", detailsAppointment.get(REMARKS).value);

        return params;
    }

    @Override
    protected JsonObject buildUpdateParam() {
        JsonObject params = new JsonObject();

        params.addProperty("code", officeDiaryModel.code);

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;
        if (!detailsAppointment.get(APPOINTMENT).value.equals(officeDiaryModel.appointmentDetails)) {
            params.addProperty("appointmentDetails", detailsAppointment.get(APPOINTMENT).value);
        }

        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        if (!startDate.equals(officeDiaryModel.startDate)) {
            params.addProperty("startDate", startDate);
        }

        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value);
        if (!endDate.equals(officeDiaryModel.endDate)) {
            params.addProperty("endDate", endDate);
        }

        if (!detailsAppointment.get(PLACE).value.equals(officeDiaryModel.place)) {
            params.addProperty("place", detailsAppointment.get(PLACE).value);
        }

        if (!detailsAppointment.get(FILE_NO).value.equals(officeDiaryModel.fileNo1)) {
            params.addProperty("fileNo1", detailsAppointment.get(FILE_NO).value);
        }

        JsonObject code = new JsonObject();
        if (!detailsAppointment.get(STAFF_ASSIGNED).code.equals(officeDiaryModel.staffAssigned.code)) {
            code.addProperty("code", detailsAppointment.get(STAFF_ASSIGNED).code);
            params.add("staffAssigned", code);
        }

        if (!detailsAppointment.get(ATTENDED_STATUS).code.equals(officeDiaryModel.attendedStatus.code)) {
            code.addProperty("code", detailsAppointment.get(ATTENDED_STATUS).code);
            params.add("attendedStatus", code);
        }

        if (!detailsAppointment.get(STAFF_ATTENDED).value.equals(officeDiaryModel.staffAttended)) {
            params.addProperty("staffAttended", detailsAppointment.get(STAFF_ATTENDED).value);
        }

        if (!detailsAppointment.get(REMARKS).value.equals(officeDiaryModel.remarks)) {
            params.addProperty("remarks", detailsAppointment.get(REMARKS).value);
        }

        return params;
    }
}


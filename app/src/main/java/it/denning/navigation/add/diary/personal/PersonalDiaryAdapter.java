package it.denning.navigation.add.diary.personal;

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



public class PersonalDiaryAdapter extends BaseDiaryAdapter {
    protected int START_DATE_TIME = 0;
    protected int END_DATE_TIME = 1;
    protected int PLACE = 2;
    protected int DETAILS = 3;
    protected int STAFF_ASSIGNED = 4;
    protected int STAFF_ATTENDED = 5;
    protected int REMARKS = 6;
    protected int ATTENDED_STATUS = 6;

    OfficeDiaryModel personalDiaryModel;

    public PersonalDiaryAdapter(Context context, OfficeDiaryModel personalDiaryModel, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.personalDiaryModel = personalDiaryModel;
        if (personalDiaryModel != null) {
            REMARKS = 7;
        }
        START_DATE_TIME = 0;
        END_DATE_TIME = 1;
        buildModel();
    }

    protected void buildModel() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();

        LabelValueDetail labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("Start Date", "", false);
        if (personalDiaryModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(personalDiaryModel.startDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (personalDiaryModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(personalDiaryModel.startDate);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail();
        labelValueDetail.viewType = DIConstants.TWO_COLUMN_TYPE;
        labelValueDetail.leftView = new LabelValueDetail("End Date", "", false);
        if (personalDiaryModel != null) {
            labelValueDetail.leftView.value = DIHelper.getOnlyDateFromDateTime(personalDiaryModel.endDate);
        }
        labelValueDetail.rightView = new LabelValueDetail("Time", "", false);
        if (personalDiaryModel != null) {
            labelValueDetail.rightView.value = DIHelper.getTime(personalDiaryModel.endDate);
        }
        sectionItemModel.items.add(labelValueDetail);


        labelValueDetail = new LabelValueDetail("Place", "", DIConstants.GENERAL_TYPE);
        if (personalDiaryModel != null) {
            labelValueDetail.value = DIHelper.getTime(personalDiaryModel.place);
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Details", "", DIConstants.GENERAL_TYPE);
        if (personalDiaryModel != null) {
            labelValueDetail.value = personalDiaryModel.appointmentDetails;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Staff Assigned", "", DIConstants.GENERAL_TYPE);
        if (personalDiaryModel != null) {
            labelValueDetail.value = personalDiaryModel.staffAssigned.name;
            labelValueDetail.code = personalDiaryModel.staffAssigned.code;
        }
        sectionItemModel.items.add(labelValueDetail);

        labelValueDetail = new LabelValueDetail("Staff Attended", "", DIConstants.GENERAL_TYPE);
        if (personalDiaryModel != null) {
            labelValueDetail.value = personalDiaryModel.staffAttended;
        }
        sectionItemModel.items.add(labelValueDetail);

        if (personalDiaryModel != null) {
            labelValueDetail = new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE);
            sectionItemModel.items.add(labelValueDetail);
        } else {
            labelValueDetail = new LabelValueDetail("Attendant Type", "", DIConstants.GENERAL_TYPE);
            if (personalDiaryModel != null) {
                labelValueDetail.value = personalDiaryModel.attendedStatus.description;
                labelValueDetail.code = personalDiaryModel.attendedStatus.code;
            }
            sectionItemModel.items.add(labelValueDetail);

            labelValueDetail = new LabelValueDetail("Remarks", "", DIConstants.INPUT_TYPE);
            if (personalDiaryModel != null) {
                labelValueDetail.value = personalDiaryModel.remarks;
            }
            sectionItemModel.items.add(labelValueDetail);
        }

        model.items.add(sectionItemModel);
    }

    @Override
    protected JsonObject buildSaveParam() {
        JsonObject params = new JsonObject();

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;

        params.addProperty("appointmentDetails", detailsAppointment.get(DETAILS).value);
        JsonObject zeroCode = new JsonObject();
        zeroCode.addProperty("code", "0");
        params.add("attendedStatus", zeroCode);
        JsonObject code = new JsonObject();
        code.addProperty("code", detailsAppointment.get(STAFF_ASSIGNED).code);
        params.add("staffAssigned", code);
        params.addProperty("place", detailsAppointment.get(PLACE).value);
        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        params.addProperty("startDate", startDate);
        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value );
        params.addProperty("endDate", endDate);
        params.addProperty("remarks", detailsAppointment.get(REMARKS).value);

        return params;
    }

    @Override
    protected JsonObject buildUpdateParam() {
        JsonObject params = new JsonObject();

        params.addProperty("code", personalDiaryModel.code);

        List<LabelValueDetail> detailsAppointment = model.items.get(DETAILS_OF_APPOINTMENT).items;
        if (!detailsAppointment.get(DETAILS).value.equals(personalDiaryModel.appointmentDetails)) {
            params.addProperty("appointmentDetails", detailsAppointment.get(DETAILS).value);
        }

        String startDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(START_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(START_DATE_TIME).rightView.value);
        if (!startDate.equals(personalDiaryModel.startDate)) {
            params.addProperty("startDate", startDate);
        }

        String endDate = DIHelper.toMySQLDateFormat2(detailsAppointment.get(END_DATE_TIME).leftView.value +
                " " + detailsAppointment.get(END_DATE_TIME).rightView.value);
        if (!endDate.equals(personalDiaryModel.endDate)) {
            params.addProperty("endDate", endDate);
        }

        if (!detailsAppointment.get(PLACE).value.equals(personalDiaryModel.place)) {
            params.addProperty("place", detailsAppointment.get(PLACE).value);
        }

        JsonObject code = new JsonObject();
        if (!detailsAppointment.get(STAFF_ASSIGNED).code.equals(personalDiaryModel.staffAssigned.code)) {
            code.addProperty("code", detailsAppointment.get(STAFF_ASSIGNED).code);
            params.add("staffAssigned", code);
        }

        if (!detailsAppointment.get(STAFF_ATTENDED).value.equals(personalDiaryModel.staffAttended)) {
            params.addProperty("staffAttended", detailsAppointment.get(STAFF_ATTENDED).value);
        }

        if (!detailsAppointment.get(ATTENDED_STATUS).code.equals(personalDiaryModel.attendedStatus.code)) {
            code.addProperty("code", detailsAppointment.get(ATTENDED_STATUS).code);
            params.add("attendedStatus", code);
        }

        if (!detailsAppointment.get(REMARKS).value.equals(personalDiaryModel.remarks)) {
            params.addProperty("remarks", detailsAppointment.get(REMARKS).value);
        }

        return params;
    }
}


package it.denning.navigation.dashboard.section1.staffleave.leavependingapproval;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddSectionItemModel;
import it.denning.model.LabelValueDetail;
import it.denning.model.StaffLeaveModel;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */

public class LeavePendingAppAdapter extends BaseSectionAdapter {
    private final int START_DATE = 0;
    private final int END_DATE = 1;
    private final int TYPE_OF_LEAVE = 2;
    private final int NO_OF_DAYS = 3;
    private final int STAFF_REMARKS = 4;
    private final int SUBMITTED_BY = 5;

    // Sections
    private final int APP_DETAILS = 0;
    private final int APPROVAL_DETAILS = 1;

    Context context;
    StaffLeaveModel staffLeaveModel;
    final private OnSectionItemClickListener itemClickListener;

    public LeavePendingAppAdapter(Context context, StaffLeaveModel staffLeaveModel, OnSectionItemClickListener itemClickListener) {
        super(context, itemClickListener);
        this.itemClickListener = itemClickListener;
        this.staffLeaveModel = staffLeaveModel;
        this.context = context;
        titles =  Arrays.asList(new String[]{"Application Details", "Approval Details"});
        buildUI();
    }

    public void updateData(String input, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).value = input;
    }

    public void updateModel(StaffLeaveModel staffLeaveModel) {
        this.staffLeaveModel = staffLeaveModel;
        model.items = new ArrayList<>();
        notifyAllSectionsDataSetChanged();
    }

    public void buildUI() {
        AddSectionItemModel sectionItemModel = new AddSectionItemModel();
        sectionItemModel.items.add(new LabelValueDetail("Start Date", DIHelper.getOnlyDateFromDateTime(staffLeaveModel.getDtStartDate()),false));
        sectionItemModel.items.add(new LabelValueDetail("End Date", DIHelper.getOnlyDateFromDateTime(staffLeaveModel.getDtEndDate()),false));
        sectionItemModel.items.add(new LabelValueDetail("Type of Leave", staffLeaveModel.getClsTypeOfLeave().description,false));
        sectionItemModel.items.add(new LabelValueDetail("No of Days", staffLeaveModel.getDecLeaveLength(),false));
        sectionItemModel.items.add(new LabelValueDetail("Staff Remarks", staffLeaveModel.getStrStaffRemarks(),false));
        sectionItemModel.items.add(new LabelValueDetail("Submitted By", ((LeavePendingApprovalActivity)context).submittedBy.name,false));
        model.items.add(sectionItemModel);

        sectionItemModel = new AddSectionItemModel();
        if (!isDone() && ((LeavePendingApprovalActivity)context).fromDashboard) {
            LabelValueDetail labelValueDetail = new LabelValueDetail();
            labelValueDetail.viewType = DIConstants.TWO_BUTTON_TYPE;
            labelValueDetail.leftView = new LabelValueDetail("Approve", "", false);
            labelValueDetail.rightView = new LabelValueDetail("Reject", "", false);
            sectionItemModel.items.add(labelValueDetail);
        } else {
            sectionItemModel.items.add(new LabelValueDetail("Status", staffLeaveModel.getClsLeaveStatus().description, false));
        }

        if (isApproved()) {
            sectionItemModel.items.add(new LabelValueDetail("Approved By", staffLeaveModel.getClsApprovedBy().strName, false));
            sectionItemModel.items.add(new LabelValueDetail("Date Approved", DIHelper.getOnlyDateFromDateTime(staffLeaveModel.getDtDateApproved()), false));
        } else if (isRejected()) {
            sectionItemModel.items.add(new LabelValueDetail("Remarks", staffLeaveModel.getStrManagerRemarks(), false));
            sectionItemModel.items.add(new LabelValueDetail("Approved By", staffLeaveModel.getClsApprovedBy().strName, false));
            sectionItemModel.items.add(new LabelValueDetail("Date Approved", DIHelper.getOnlyDateFromDateTime(staffLeaveModel.getDtDateApproved()), false));
        }
        model.items.add(sectionItemModel);

        notifyAllSectionsDataSetChanged();
    }

    public boolean isApproved() {
        return staffLeaveModel.getClsLeaveStatus().code.equals("1");
    }

    public boolean isRejected() {
        return staffLeaveModel.getClsLeaveStatus().code.equals("3");
    }

    public boolean isDone() {
        return  ((LeavePendingApprovalActivity)context).isDone;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 0;
        if (sectionIndex == APP_DETAILS) {
            count = model.items.get(sectionIndex).items.size();
        } else {
            switch (staffLeaveModel.getClsLeaveStatus().code) {
                case "1":
                    count = 3;
                    break;
                case "3":
                    count = 4;
                    break;
                default:
                    count = 1;
                    break;
            }
        }

        return count;
    }

    public JsonObject buildSaveParams(String leaveStatusCode) {
        JsonObject jsonObject = new JsonObject();

        JsonObject clsLeaveStatus = new JsonObject();
        clsLeaveStatus.addProperty("code", leaveStatusCode);
        jsonObject.add("clsLeaveStatus", clsLeaveStatus);
        jsonObject.addProperty("code", staffLeaveModel.getCode());

        return jsonObject;
    }
}


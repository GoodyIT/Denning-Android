package it.denning.navigation.add.diary;

import android.content.Context;

import android.widget.EditText;
import com.google.gson.JsonObject;

import java.util.Arrays;

import it.denning.model.CourtDiaryCourt;
import it.denning.model.MatterLitigation;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-19.
 */



public class BaseDiaryAdapter extends BaseSectionAdapter {
    protected  int FILE_NO = 0;
    protected  int CASE_NO = 1;
    protected  int CASE_NAME = 2;

    // Sections
    protected final int DETAILS_OF_APPOINTMENT = 0;
    protected final int NEXT_DATE_DETAILS = 1;

    public BaseDiaryAdapter(Context context, OnSectionItemClickListener itemClickListener) {
        super(context,  itemClickListener);
        titles = Arrays.asList(new String[]{"Details of Appointment", "Next Date Details"});
    }

    public void updateFileNo(MatterLitigation matterLitigation) {
        updateDataAndRefresh(matterLitigation.systemNo, DETAILS_OF_APPOINTMENT, FILE_NO);
        updateDataAndRefresh(matterLitigation.getCaseNo(), DETAILS_OF_APPOINTMENT, CASE_NO);
        updateDataAndRefresh(matterLitigation.getCaseName(), DETAILS_OF_APPOINTMENT, CASE_NAME);
    }

    public void updateCourt(CourtDiaryCourt court, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).leftView.value = court.typeE;
        model.items.get(sectionIndex).items.get(itemIndex).code = court.code;
        model.items.get(sectionIndex).items.get(itemIndex).rightView.value = court.place;
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateDate(String date, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).leftView.value = date;
        if (itemIndex == START_DATE_TIME) {
            model.items.get(sectionIndex).items.get(itemIndex).rightView.value = "09:00";
            model.items.get(sectionIndex).items.get(END_DATE_TIME).leftView.value = date;
            model.items.get(sectionIndex).items.get(END_DATE_TIME).rightView.value = "17:00";
            notifySectionItemChanged(sectionIndex, END_DATE_TIME);
        } else if (itemIndex == END_DATE_TIME) {
            model.items.get(sectionIndex).items.get(END_DATE_TIME).rightView.value = "17:00";
        }

        if (itemIndex == NEXT_START_DATE_TIME) {
            model.items.get(sectionIndex).items.get(itemIndex).rightView.value = "09:00";
            model.items.get(sectionIndex).items.get(NEXT_END_DATE_TIME).leftView.value = date;
            model.items.get(sectionIndex).items.get(NEXT_END_DATE_TIME).rightView.value = "17:00";
            notifySectionItemChanged(sectionIndex, NEXT_END_DATE_TIME);
        } else if (itemIndex == NEXT_END_DATE_TIME) {
            model.items.get(sectionIndex).items.get(NEXT_END_DATE_TIME).rightView.value = "17:00";
        }

        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    public void updateTime(String time, int sectionIndex, int itemIndex) {
        model.items.get(sectionIndex).items.get(itemIndex).rightView.value = time;
        notifySectionItemChanged(sectionIndex, itemIndex);
    }

    @Override
    protected JsonObject buildSaveParam() {
        return null;
    }

    @Override
    protected JsonObject buildUpdateParam() {
        return null;
    }

}


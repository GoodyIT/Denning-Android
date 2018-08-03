package it.denning.navigation.add.leaveapplication.leaveapplication;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddReceiptModel;
import it.denning.model.AddSectionItemModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.MatterSimple;
import it.denning.model.NameCode;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.add.bill.AddBillAdapter;
import it.denning.navigation.add.matter.AddMatterActivity;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.floatinglabelview.FloatingLabelView;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-19.
 */



public class LeaveApplicationAdapter extends SectioningAdapter {
    private final int START_DATE = 0;
    private final int END_DATE = 1;
    private final int TYPE_OF_LEAVE = 2;
    private final int NO_OF_DAYS = 3;
    private final int STAFF_REMARKS = 4;
    private final int SUBMITTED_BY = 5;

    Context context;
    private AddSectionItemModel model = new AddSectionItemModel(DIConstants.leave_app_labels, DIConstants.leave_app_has_details);
    final private OnSectionItemClickListener itemClickListener;

    public LeaveApplicationAdapter(Context context, OnSectionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    public AddSectionItemModel getModel() {
        return this.model;
    }

    public class GeneralTypeViewHolder extends ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_detail_btn)
        ImageButton detailBtn;
        @BindView(R.id.add_cardview)
        CardView cardView;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class InputTypeViewHolder extends ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_cardview)
        CardView cardView;
        public MyCustomEditTextListener myCustomEditTextListener;
        public InputTypeViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    public class OneButtonViewHolder extends ItemViewHolder {
        @BindView(R.id.last_btn)
        Button button;

        public OneButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return 1;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = model.items.size();
        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return false;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        if (itemIndex == getNumberOfItemsInSection(sectionIndex)-1) {
            return DIConstants.ONE_BUTTON_TYPE;
        }

        if (!model.items.get(itemIndex).hasDetail) {
            if (itemIndex == getNumberOfItemsInSection(sectionIndex)-2) {
                return DIConstants.GENERAL_TYPE;
            }
            return DIConstants.INPUT_TYPE;
        }

        return DIConstants.GENERAL_TYPE;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View itemView = null;
        if (itemUserType == DIConstants.ONE_BUTTON_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_add_last_onebutton, parent, false);
            itemViewHolder =  new OneButtonViewHolder(itemView);
        } else if (itemUserType == DIConstants.INPUT_TYPE) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_input, parent, false);
            itemViewHolder = new InputTypeViewHolder(itemView, new MyCustomEditTextListener());
        } else {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.cardview_add_detail, parent, false);
            itemViewHolder = new GeneralTypeViewHolder(itemView);
        }

        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == DIConstants.ONE_BUTTON_TYPE) {
            displayOneButton((OneButtonViewHolder)viewHolder, sectionIndex, itemIndex);
        } else if (itemType == DIConstants.INPUT_TYPE) {
            displayInput((InputTypeViewHolder) viewHolder, sectionIndex, itemIndex);
        } else {
            displayGeneral((GeneralTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        }
    }

    private void displayOneButton(OneButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final String name = model.items.get(itemIndex).label;
        viewHolder.button.setText(name);
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, name);
            }
        });
    }

    private void displayInput(final InputTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        int inputType = InputType.TYPE_CLASS_TEXT;
        if (itemIndex == NO_OF_DAYS) {
            inputType = InputType.TYPE_CLASS_NUMBER;
        }
        viewHolder.editText.setInputType(inputType);
    }

    private void displayGeneral(final GeneralTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {

        final LabelValueDetail labelValueDetail = model.items.get(itemIndex);
        Log.d("Log general type", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        viewHolder.editText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, labelValueDetail.label);
            }
        });
    }

    private void updateDataFromInput(String input, int sectionIndex, int itemIndex) {
        if (input.trim().length() == 0) {
            return;
        }
        input = input.substring(0, 1).toUpperCase() + input.substring(1);
        model.items.get(itemIndex).value = input;
    }

    public void updateStartDate(String date) {
        model.items.get(START_DATE).value = date;
        notifySectionItemChanged(0, START_DATE);
    }

    public void updateEndDate(String date) {
        model.items.get(END_DATE).value = date;
        notifySectionItemChanged(0, END_DATE);
    }

    public void updateSubmittedBy(NameCode value) {
        model.items.get(SUBMITTED_BY).value = value.name;
        model.items.get(SUBMITTED_BY).code = value.code;
    }

    public void updateTypeOfLeave(CodeDescription codeDescription) {
        model.items.get(TYPE_OF_LEAVE).value = codeDescription.description;
        model.items.get(TYPE_OF_LEAVE).code = codeDescription.code;
        notifySectionItemChanged(0, TYPE_OF_LEAVE);
    }

    public JsonObject buildSaveParams() {
        JsonObject jsonObject = new JsonObject();

        JsonObject clsLeaveStatus = new JsonObject();
        clsLeaveStatus.addProperty("code", "0");
        jsonObject.add("clsLeaveStatus", clsLeaveStatus);

        JsonObject clsStaff = new JsonObject();
        clsStaff.addProperty("code", model.items.get(SUBMITTED_BY).code);
        jsonObject.add("clsStaff", clsStaff);

        JsonObject clsTypeOfLeave = new JsonObject();
        clsTypeOfLeave.addProperty("code", model.items.get(TYPE_OF_LEAVE).code);
        jsonObject.add("clsTypeOfLeave", clsTypeOfLeave);

        jsonObject.addProperty("dtEndDate", DIHelper.toMySQLDateFormat2(model.items.get(END_DATE).value));
        jsonObject.addProperty("dtStartDate", DIHelper.toMySQLDateFormat2(model.items.get(START_DATE).value));
        jsonObject.addProperty("dtDateSubmitted", DIHelper.todayWithTime());
        jsonObject.addProperty("decLeaveLength", model.items.get(NO_OF_DAYS).value);
        jsonObject.addProperty("strStaffRemarks", model.items.get(STAFF_REMARKS).value);

        return jsonObject;
    }

    private class MyCustomEditTextListener implements View.OnFocusChangeListener {
        private int sectionIndex, itemIndex;

        public void updatePosition(int sectionIndex, int itemIndex) {
            this.sectionIndex = sectionIndex;
            this.itemIndex = itemIndex;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            updateDataFromInput(((EditText)v).getText().toString(), sectionIndex, itemIndex);
            KeyboardUtils.hideKeyboard(v);

            android.os.Handler mHandler = ((Activity)context).getWindow().getDecorView().getHandler();
            mHandler.post(new Runnable() {
                public void run(){
                    //change adapter contents
                    if (!hasFocus) {
                        notifySectionItemChanged(sectionIndex, itemIndex);
                    }
                }
            });
        }
    }
}


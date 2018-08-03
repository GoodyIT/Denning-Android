package it.denning.navigation.add.bill;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddBillModel;
import it.denning.model.BillModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.MatterCodeModel;
import it.denning.model.MatterSimple;
import it.denning.model.PartyGroup;
import it.denning.model.StaffModel;
import it.denning.model.TaxInvoiceCalcModel;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddBillAdapter extends SectioningAdapter {
    private AddBillModel model = AddBillModel.build();
    final private OnSectionItemClickListener itemClickListener;
    private String isRental = "0";
    private String issueToFirstCode = "";
    private boolean hasCalculate = true;
    Context context;

    // Sections
    public static final int BILL_DETAILS = 0;
    public static final int BILL_ANALYSIS = 1;

    // BILL_DETAILS
    public static final int CONVERT_QUOTATION = 0;
    public static final int BILL_NO = 1;
    public static final int FILE_NO = 2;
    public static final int MATTER = 3;
    public static final int BILL_TO = 4;
    public static final int PRESET_CODE = 5;
    public static final int PRICE = 6; // month
    public static final int LOAN = 7; // rental
    public static final int CALCULATE = 8;

    // BILL_ANALYSIS
    public static final int PROFESSIONAL_FEES = 0;
    public static final int DISB_WITH_GST = 1;
    public static final int DISB = 2;
    public static final int GST = 3;
    public static final int TOTAL = 4;
    public static final int SAVE_VIEW = 5;
    public static final int ISSUE_RECEIPT = 6;


    public AddBillAdapter(Context context, OnSectionItemClickListener itemClickListener, BillModel billModel, boolean hasCalculate) {
        this.itemClickListener = itemClickListener;
        this.hasCalculate = hasCalculate;
        if (!hasCalculate && billModel != null) {
            updateModelForUpdate(billModel);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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

    public class InputTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.add_detail_edittext)
        MyFloatingEditText editText;
        @BindView(R.id.add_cardview)
        CardView cardView;
        public MyCustomEditTextListener myCustomEditTextListener;
        public InputTypeViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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

    public class OneButtonWhiteViewHolder extends SectioningAdapter.ItemViewHolder
    {
        @BindView(R.id.last_btn)
        Button button;

        public OneButtonWhiteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class TwoButtonViewHolder extends ItemViewHolder {
        @BindView(R.id.add_leftbtn)
        Button leftBtn;
        @BindView(R.id.add_rightbtn)
        Button rightBtn;

        public TwoButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return model.items.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = model.items.get(sectionIndex).items.size();
        if (sectionIndex == 0) {
            count -= 2;
        }
        if (!hasCalculate && sectionIndex == 0) {
            count--;
        }
        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        int type = 0;
        if (sectionIndex == 0) {
            if (!model.items.get(sectionIndex).items.get(itemIndex).hasDetail) {
                type = DIConstants.INPUT_TYPE;
            } else {
                type = DIConstants.GENERAL_TYPE;
            }
            if (itemIndex == getNumberOfItemsInSection(sectionIndex)-1 && hasCalculate) {
                type = DIConstants.ONE_BUTTON_TYPE;
            }
        } else if (sectionIndex == 1) {
            if (itemIndex == 5) {
                type = DIConstants.TWO_BUTTON_TYPE;
            } else if (itemIndex == 6) {
                type = DIConstants.ONE_BUTTON_WHITE_TYPE;
            } else {
                if (!model.items.get(sectionIndex).items.get(itemIndex).hasDetail && itemIndex != 4) {
                    type = DIConstants.INPUT_TYPE;
                } else {
                    type = DIConstants.GENERAL_TYPE;
                }
            }
        }

        return type;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        switch (itemUserType) {
            case DIConstants.ONE_BUTTON_TYPE:
                View itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_add_last_onebutton, parent, false);

                itemViewHolder =  new OneButtonViewHolder(itemView);
                break;
            case DIConstants.ONE_BUTTON_WHITE_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_add_last_onebutton_white, parent, false);

                itemViewHolder =  new OneButtonWhiteViewHolder(itemView);
                break;
            case DIConstants.GENERAL_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_add_detail, parent, false);

                itemViewHolder = new GeneralTypeViewHolder(itemView);
                break;
            case DIConstants.INPUT_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_input, parent, false);
                itemViewHolder = new InputTypeViewHolder(itemView, new MyCustomEditTextListener());
                break;
            case DIConstants.TWO_BUTTON_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_twobutton, parent, false);

                itemViewHolder =  new TwoButtonViewHolder(itemView);
                break;
            default:
                break;
        }

        return itemViewHolder;
    }

    @Override
    public AddBillAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new AddBillAdapter.HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        switch (itemType) {
            case DIConstants.ONE_BUTTON_TYPE:
                displayOneButton((OneButtonViewHolder) viewHolder, sectionIndex, itemIndex);
                break;
            case DIConstants.ONE_BUTTON_WHITE_TYPE:
                displayOneWhiteButton((OneButtonWhiteViewHolder) viewHolder, sectionIndex, itemIndex);
                break;
            case DIConstants.GENERAL_TYPE:
                displayGeneral((GeneralTypeViewHolder)viewHolder, sectionIndex, itemIndex);
                break;
            case DIConstants.INPUT_TYPE:
                displayInput((InputTypeViewHolder) viewHolder, sectionIndex, itemIndex);
                break;
            case DIConstants.TWO_BUTTON_TYPE:
                displayTwoButton((TwoButtonViewHolder) viewHolder, sectionIndex, itemIndex);
                break;
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        if (sectionIndex == BILL_DETAILS) {
            headerViewHolder.firstTitle.setText("Bill Details");
        } else {
            headerViewHolder.firstTitle.setText("Bill Analysis");
        }
    }

    private void displayInput(final InputTypeViewHolder viewHolder, final int sectionIndex, int itemIndex) {
        if (!isRental.equals("0") && itemIndex > 4 && sectionIndex == 0) {
            itemIndex += 2;
        }
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
    }

    private void displayGeneral(final GeneralTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setFocusable(false);

        // Specially enable/disable fields
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);

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
        input = DIHelper.addThousandsSeparator(input);
        model.items.get(sectionIndex).items.get(itemIndex).value = input;
    }

    private void displayOneButton(OneButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        String name = "";
        if (sectionIndex == BILL_DETAILS) {
            name = "Calculator";
        }

        viewHolder.button.setText(name);
        final String _name = name;
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, _name);
            }
        });
    }

    private void displayOneWhiteButton(OneButtonWhiteViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        if (sectionIndex == 1) {
            viewHolder.button.setText("Issue Receipt");
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, sectionIndex, itemIndex, "Issue Receipt");
                }
            });
        }
    }

    private void displayTwoButton(TwoButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        if (sectionIndex == BILL_ANALYSIS) {
            viewHolder.leftBtn.setText("Save");
            viewHolder.rightBtn.setText("View");
            viewHolder.leftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, sectionIndex, itemIndex, "Save");
                }
            });
            viewHolder.rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, sectionIndex, itemIndex, "View");
                }
            });
        }
    }

    public String getFileNo() {
        return model.items.get(BILL_DETAILS).items.get(FILE_NO).value;
    }

    public void updateBillTo(String name, String code) {
        model.items.get(0).items.get(4).value = name;
        model.items.get(0).items.get(4).code = code;
        notifySectionItemChanged(0, 4);
    }

    public void updateMatterSimple(MatterSimple matterSimple) {
        this.isRental = matterSimple.matter.isRental;
        if (matterSimple.partyGroups.size() > 0) {
            PartyGroup partyGroup = matterSimple.partyGroups.get(0);
            String issueToName = "";
            for (StaffModel party : partyGroup.party) {
                issueToName += " " + party.name;
                issueToFirstCode = partyGroup.party.get(0).code;
            }
            updateBillTo(issueToName, issueToFirstCode);
        }

        model.items.get(0).items.get(2).value = matterSimple.systemNo; // SystemNo
        model.items.get(0).items.get(3).value = matterSimple.matter.description;
        model.items.get(0).items.get(3).code = matterSimple.matter.code;
        model.items.get(0).items.get(5).value = matterSimple.presetBill.strDescription;
        model.items.get(0).items.get(5).code = matterSimple.presetBill.code;
        model.items.get(0).items.get(6).value = matterSimple.spaPrice;
        model.items.get(0).items.get(7).value = matterSimple.spaLoan;
        model.items.get(0).items.get(8).value = matterSimple.rentalMonth;
        model.items.get(0).items.get(9).value = matterSimple.rentalPrice;

        notifySectionDataSetChanged(0);
    }

    public void updatePreset(CodeDescription codeDescription) {
        model.items.get(0).items.get(5).value = codeDescription.description;
        model.items.get(0).items.get(5).code = codeDescription.code;

        notifySectionItemChanged(0, 5);
    }

    public void updateMatterCode(MatterCodeModel matterCodeModel) {
        model.items.get(0).items.get(3).value = matterCodeModel.description;
        model.items.get(0).items.get(3).code = matterCodeModel.code;

        notifySectionItemChanged(0, 3);                                                                                                                                                                                                                                                                                                   notifyAllSectionsDataSetChanged();
    }

    public void updateModelForUpdate(BillModel billModel) {
        model.items.get(BILL_DETAILS).items.get(CONVERT_QUOTATION).value = billModel.relatedDocumentNo;
        isRental = billModel.isRental;

        updateBillTo(billModel.issueToName, billModel.issueTo1stCode.code);
        model.items.get(BILL_DETAILS).items.get(FILE_NO).value = billModel.fileNo;
        model.items.get(0).items.get(6).value = billModel.spaPrice;
        model.items.get(0).items.get(7).value = billModel.spaLoan;
        model.items.get(0).items.get(8).value = billModel.rentalMonth;
        model.items.get(0).items.get(9).value = billModel.rentalPrice;
        updateMatterCode(billModel.matter);
        updatePreset(new CodeDescription(billModel.presetCode.code, billModel.presetCode.description));
        updateModelFromSave(billModel);
    }

    public void updateModelFromSave(BillModel billModel) {
        model.items.get(0).items.get(1).value = billModel.documentNo;
        notifySectionItemChanged(0, 1);
        updateBelowData(billModel.analysis);
    }

    public void updateBelowData(TaxInvoiceCalcModel calcModel) {
        model.items.get(1).items.get(0).value = calcModel.decFees;
        model.items.get(1).items.get(1).value = calcModel.decDisbGST;
        model.items.get(1).items.get(2).value = calcModel.decDisb;
        model.items.get(1).items.get(3).value = calcModel.decGST;
        model.items.get(1).items.get(4).value = calcModel.decTotal;

        notifySectionDataSetChanged(1);
    }

    public JsonObject buildCalcParams() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("isRental", isRental);
        jsonObject.addProperty("spaPrice", model.items.get(0).items.get(6).value.replace(",", ""));
        jsonObject.addProperty("spaLoan", model.items.get(0).items.get(7).value.replace(",", ""));
        jsonObject.addProperty("rentalMonth", model.items.get(0).items.get(8).value.replace(",", ""));
        jsonObject.addProperty( "rentalPrice ", model.items.get(0).items.get(9).value.replace(",", ""));
        JsonObject presetCode = new JsonObject();
        presetCode.addProperty( "code", model.items.get(0).items.get(5).code);
        jsonObject.add( "presetCode", presetCode);

        return jsonObject;
    }

    public boolean isConvertQuotationEmpty() {
        return model.items.get(BILL_DETAILS).items.get(CONVERT_QUOTATION).value.matches("\\w*");
    }

    public boolean isFileNoEmpty() {
        return model.items.get(BILL_DETAILS).items.get(FILE_NO).value.matches("\\w*");
    }

    public boolean isPresetSelected() {
        return !model.items.get(0).items.get(5).isEmptyCode() && !model.items.get(0).items.get(2).value.matches("\\w*");
    }

    public JsonObject buildSaveParams() {
        JsonObject params = new JsonObject();

        params.addProperty("fileNo", model.items.get(0).items.get(2).value);
        params.addProperty("issueDate", DIHelper.todayWithTime());
        JsonObject issueTo =  new JsonObject();
        issueTo.addProperty("code", issueToFirstCode);
        params.add("issueTo1stCode", issueTo);
        params.addProperty("issueToName", model.items.get(0).items.get(4).value);
        JsonObject matter = new JsonObject();
        matter.addProperty("code", model.items.get(0).items.get(3).code);
        params.add("matter", matter);
        params.addProperty("relatedDocumentNo", model.items.get(0).items.get(1).value);

        return NetworkManager.mergeJSONObjects(buildCalcParams(), params);
    }

    public JsonObject buildReceiptParams() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("documentNo", model.items.get(0).items.get(0).value);
        return NetworkManager.mergeJSONObjects(buildSaveParams(), jsonObject);
    }


    public String getBillNo() {
        return model.items.get(BILL_DETAILS).items.get(BILL_NO).value;
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
            android.os.Handler mHandler = ((AddBillActivity)context).getWindow().getDecorView().getHandler();
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

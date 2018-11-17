package it.denning.navigation.add.quotation;

import android.content.Context;
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

import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddQuotationModel;
import it.denning.model.BillModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.MatterCodeModel;
import it.denning.model.MatterSimple;
import it.denning.model.PartyGroup;
import it.denning.model.StaffModel;
import it.denning.model.TaxInvoiceCalcModel;
import it.denning.navigation.add.matter.AddMatterActivity;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddQuotationAdapter extends SectioningAdapter {
    private AddQuotationModel model = AddQuotationModel.build();
    final private OnSectionItemClickListener itemClickListener;
    private String isRental = "0";
    private String issueToFirstCode = "";
    Context context;

    // Sections
    private final int QUOTATION_DETAILS = 0;
    private final int QUOTATION_ANALYSIS = 1;

    // QUOTATION_DETAILS
    private final int QUOTATION_NO = 0;
    private final int FILE_NO = 1;
    private final int MATTER = 2;
    private final int QUOTATION_TO = 3;
    private final int PRESET_CODE = 4;
    private final int PRICE = 5;
    private final int LOAN = 6;
    private final int MONTH = 7;
    private final int RENTAL = 8;

    // QUOTATION_ANALYSIS
    private final int PROFESSIONAL_FEES = 0;
    private final int DISB_WITH_GST = 1;
    private final int DISBURSEMENT = 2;
    private final int GST = 3;
    private final int TOTAL = 4;
    private final int VIEW = 5;
    private final int CONVERT_TO_TAX = 6;
    private final int ISSUE_RECEIPT = 7;

    private EditText focusedEditText;

    public void clearFocus() {
        if (focusedEditText == null) {
            return;
        }
        focusedEditText.clearFocus();
    }

    public AddQuotationAdapter(Context context, OnSectionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GeneralTypeViewHolder extends SectioningAdapter.ItemViewHolder {
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
//        public MyTextWatcher myTextWatcher;
        public InputTypeViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
//            this.myTextWatcher = new MyTextWatcher();
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setOnFocusChangeListener(myCustomEditTextListener);
//            editText.addTextChangedListener(this.myTextWatcher);
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

    public class OneButtonViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.last_btn)
        Button button;

        public OneButtonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class TwoButtonViewHolder extends SectioningAdapter.ItemViewHolder {
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

            if (itemIndex == getNumberOfItemsInSection(sectionIndex)-1) {
                type = DIConstants.ONE_BUTTON_TYPE;
            }
        } else if (sectionIndex == 1) {
            if (itemIndex == 5) {
                type = DIConstants.TWO_BUTTON_TYPE;
            } else if (itemIndex == 6) {
                type = DIConstants.ONE_BUTTON_WHITE_TYPE;
            } else if (itemIndex == 7) {
                type = DIConstants.ONE_BUTTON_TYPE;
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
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
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
    public AddQuotationAdapter.HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new AddQuotationAdapter.HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
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
        if (sectionIndex == 0) {
            headerViewHolder.firstTitle.setText("Quotation Details");
        } else {
            headerViewHolder.firstTitle.setText("Quotation Analysis");
        }
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void deleteItem(final int sectionIndex, final int position) {
        model.items.get(sectionIndex).items.get(position).value = "";

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run(){
                //change adapter contents
                notifySectionItemChanged(sectionIndex, position);
                clearFocus();
            }
        }, 300);
    }

    private void displayInput(final InputTypeViewHolder viewHolder, final int sectionIndex, int itemIndex) {
        if (!isRental.equals("0") && itemIndex > 4 && sectionIndex == 0) {
            itemIndex += 2;
        }
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
//        viewHolder.myTextWatcher.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setHint(labelValueDetail.label);
        viewHolder.editText.setFloatingLabelText(labelValueDetail.label);
        final int _itemIndex = itemIndex;
        viewHolder.editText.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(sectionIndex, _itemIndex);
            }
        });
    }

    private void displayGeneral(final GeneralTypeViewHolder viewHolder, final int sectionIndex,final int itemIndex) {
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
        if (sectionIndex == 0) {
            name = "Calculate";
        } else {
            name = "Issue Receipt";
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
            viewHolder.button.setText("Convert To Tax Invoice");
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onClick(v, sectionIndex, itemIndex, "Convert To Tax Invoice");
                }
            });
        }
    }

    private void displayTwoButton(TwoButtonViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        if (sectionIndex == 1) {
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

    public String getQuotationNo() {
        return model.items.get(0).items.get(0).value;
    }

    public String getFileNo() {
        return model.items.get(0).items.get(1).value;
    }

    public void updateQuotationTo(String name, String code) {
        model.items.get(0).items.get(3).value = name;
        model.items.get(0).items.get(3).code = code;
        notifySectionItemChanged(0, 3);
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
            updateQuotationTo(issueToName, issueToName);
        }

        model.items.get(0).items.get(1).value = matterSimple.systemNo + "( " + matterSimple.primaryClient.name + " )"; // SystemNo
        model.items.get(0).items.get(1).code = matterSimple.systemNo;
        model.items.get(0).items.get(2).value = matterSimple.matter.description;
        model.items.get(0).items.get(2).code = matterSimple.matter.code;
        model.items.get(0).items.get(4).value = matterSimple.presetBill.strDescription;
        model.items.get(0).items.get(4).code = matterSimple.presetBill.code;
        model.items.get(0).items.get(5).value = matterSimple.spaPrice;
        model.items.get(0).items.get(6).value = matterSimple.spaLoan;
        model.items.get(0).items.get(7).value = matterSimple.rentalMonth;
        model.items.get(0).items.get(8).value = matterSimple.rentalPrice;

        notifySectionDataSetChanged(0);
    }

    public void updatePreset(CodeDescription codeDescription) {
        model.items.get(0).items.get(4).value = codeDescription.description;
        model.items.get(0).items.get(4).code = codeDescription.code;

        notifySectionItemChanged(0, 4);
    }

    public void updateMatterCode(MatterCodeModel matterCodeModel) {
        model.items.get(0).items.get(2).value = matterCodeModel.description;
        model.items.get(0).items.get(2).code = matterCodeModel.code;

        notifySectionItemChanged(0, 2);                                                                                                                                                                                                                                                                                            notifyAllSectionsDataSetChanged();
    }

    public void updateModelFromSave(BillModel billModel) {
        model.items.get(0).items.get(0).value = billModel.documentNo;
        notifySectionItemChanged(0, 0);
        updateBelowData(billModel.analysis);
    }

    public void updateBelowData(TaxInvoiceCalcModel calcModel) {
        model.items.get(1).items.get(0).value = calcModel.decFees;
        model.items.get(1).items.get(1).value = calcModel.decDisbGST;
        model.items.get(1).items.get(2).value = calcModel.decDisb;
        model.items.get(1).items.get(3).value = calcModel.decGST;
        model.items.get(1).items.get(4).value = calcModel.decTotal;

        notifySectionItemChanged(1, 0);
        notifySectionItemChanged(1, 1);
        notifySectionItemChanged(1, 2);
        notifySectionItemChanged(1, 3);
        notifySectionItemChanged(1, 4);
    }

    public JsonObject buildCalcParams() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("isRental", isRental);
        jsonObject.addProperty("spaPrice", DIHelper.toNumber(model.items.get(0).items.get(5).value).replace(",", ""));
        jsonObject.addProperty("spaLoan", DIHelper.toNumber(model.items.get(0).items.get(6).value).replace(",", ""));
        jsonObject.addProperty("rentalMonth", DIHelper.toNumber(model.items.get(0).items.get(7).value).replace(",", ""));
        jsonObject.addProperty( "rentalPrice ", DIHelper.toNumber(model.items.get(0).items.get(8).value).replace(",", ""));
        JsonObject presetCode = new JsonObject();
        presetCode.addProperty( "code", model.items.get(0).items.get(4).code);
        jsonObject.add( "presetCode", presetCode);

        return jsonObject;
    }

    public boolean isPresetSelected() {
        return !model.items.get(0).items.get(4).isEmptyCode();
    }

    public boolean isFileNoSelected() {
        return !model.items.get(0).items.get(1).value.isEmpty();
    }

    public JsonObject buildReceiptParams() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("documentNo", model.items.get(0).items.get(0).value);

        return NetworkManager.mergeJSONObjects(buildSaveParams(), jsonObject);
    }

    public JsonObject buildSaveParams() {
        clearFocus();

        JsonObject params = new JsonObject();

        params.addProperty("fileNo", model.items.get(0).items.get(1).code);
        params.addProperty("issueDate", DIHelper.todayWithTime());
        JsonObject issueTo =  new JsonObject();
        issueTo.addProperty("code", issueToFirstCode);
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

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        private int sectionIndex, itemIndex;

        public void updatePosition(int sectionIndex, int itemIndex) {
            this.sectionIndex = sectionIndex;
            this.itemIndex = itemIndex;
        }

        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if (hasFocus) {
                focusedEditText = (EditText)v;
                return;
            }

            updateDataFromInput(((EditText)v).getText().toString(), sectionIndex, itemIndex);
//            KeyboardUtils.hideKeyboard(v);

            android.os.Handler mHandler = ((AddQuotationActivity)context).getWindow().getDecorView().getHandler();
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

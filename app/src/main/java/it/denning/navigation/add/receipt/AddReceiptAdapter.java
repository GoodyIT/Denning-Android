package it.denning.navigation.add.receipt;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.AddReceiptModel;
import it.denning.model.CodeDescription;
import it.denning.model.LabelValueDetail;
import it.denning.model.MatterSimple;
import it.denning.model.ReceiptModel;
import it.denning.model.TaxInvoiceModel;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.myfloatingedittext.MyFloatingEditText;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2018-01-19.
 */

public class AddReceiptAdapter extends SectioningAdapter {
    private AddReceiptModel model = AddReceiptModel.build();
    private ReceiptModel receiptModel;
    Context context;
    final private OnSectionItemClickListener itemClickListener;

    // Sections
    public static final int RECEIPT = 0;
    public static final int MODE_OF_PAYMENT = 1;

    // RECEIPT
    public static final int FILE_NO = 0;
    public static final int BILL_NO = 1;
    public static final int ACCOUNT_TYPE = 2;
    public static final int RECEIVED_FROM = 3;
    public static final int AMOUNT = 4;
    public static final int TRANSACTION = 5;


    // MODE OF PAYMENT
    public static final int MODE = 0;
    public static final int ISSUER_BANK = 1;
    public static final int BANK_BRANCH = 2;
    public static final int CHEQUE_NO = 3;
    public static final int CHEQUE_AMOUNT = 4;
    public static final int REMARKS = 5;

    public AddReceiptAdapter(Context context, ReceiptModel receiptModel, OnSectionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.receiptModel = receiptModel;
        this.context = context;
        adjustUpdateData();
    }

    private void adjustUpdateData() {
        model.items.get(RECEIPT).items.get(FILE_NO).value = receiptModel.fileNo;
        model.items.get(RECEIPT).items.get(BILL_NO).value = receiptModel.invoiceNo;
        model.items.get(RECEIPT).items.get(ACCOUNT_TYPE).value = receiptModel.accountType.description;
        model.items.get(RECEIPT).items.get(RECEIVED_FROM).value = receiptModel.receivedFrom;
        model.items.get(RECEIPT).items.get(AMOUNT).value = receiptModel.amount;
        model.items.get(RECEIPT).items.get(AMOUNT).inputType = InputType.TYPE_CLASS_NUMBER;

        model.items.get(MODE_OF_PAYMENT).items.get(MODE).value = receiptModel.payment.mode;
        model.items.get(MODE_OF_PAYMENT).items.get(ISSUER_BANK).value = receiptModel.payment.issuerBank;
        model.items.get(MODE_OF_PAYMENT).items.get(BANK_BRANCH).value = receiptModel.payment.bankBranch;
        model.items.get(MODE_OF_PAYMENT).items.get(CHEQUE_NO).value = receiptModel.payment.referenceNo;
        model.items.get(MODE_OF_PAYMENT).items.get(CHEQUE_AMOUNT).value = receiptModel.payment.totalAmount;
        model.items.get(MODE_OF_PAYMENT).items.get(CHEQUE_AMOUNT).inputType = InputType.TYPE_CLASS_NUMBER;
        model.items.get(MODE_OF_PAYMENT).items.get(REMARKS).value = receiptModel.remarks;
    }

    public String getFileNo() {
        return model.items.get(0).items.get(0).value;
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
        public InputTypeViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            editText.setOnFocusChangeListener(myCustomEditTextListener);
        }
    }

    @Override
    public int getNumberOfSections() {

        return model.items.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = model.items.get(sectionIndex).items.size();
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
        if (!model.items.get(sectionIndex).items.get(itemIndex).hasDetail) {
            return DIConstants.INPUT_TYPE;
        }
        return DIConstants.GENERAL_TYPE;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        View itemView = null;
        if (itemUserType == DIConstants.INPUT_TYPE) {
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
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == DIConstants.INPUT_TYPE) {
            displayInput((InputTypeViewHolder) viewHolder, sectionIndex, itemIndex);
        } else {
            displayGeneral((GeneralTypeViewHolder)viewHolder, sectionIndex, itemIndex);
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        if (sectionIndex == 0) {
            headerViewHolder.firstTitle.setText("Receipt");
        } else {
            headerViewHolder.firstTitle.setText("Mode Of Payment");
        }
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    private void displayInput(final InputTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        viewHolder.myCustomEditTextListener.updatePosition(sectionIndex, itemIndex);
        final LabelValueDetail labelValueDetail = model.items.get(sectionIndex).items.get(itemIndex);
        Log.d("Log inputtype", labelValueDetail.label + " -- " + labelValueDetail.value);
        viewHolder.editText.setText(labelValueDetail.value);
        viewHolder.editText.setInputType(labelValueDetail.inputType);
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

        if ((sectionIndex == RECEIPT && itemIndex == AMOUNT )|| (sectionIndex == MODE_OF_PAYMENT && itemIndex == CHEQUE_AMOUNT)) {
            input = DIHelper.addThousandsSeparator(input);
        } else {
            input = input.substring(0, 1).toUpperCase() + input.substring(1);
        }
        model.items.get(sectionIndex).items.get(itemIndex).value = input;
    }

    public void updateMatterSimple(MatterSimple matterSimple) {
        model.items.get(0).items.get(0).value = matterSimple.systemNo;
        notifySectionItemChanged(0, 0);
    }

    public void updateAccountType(CodeDescription codeDescription) {
        model.items.get(0).items.get(2).value = codeDescription.getDesc();
        model.items.get(0).items.get(1).code = codeDescription.code;
        notifySectionItemChanged(0, 2);
        notifySectionItemChanged(0, 1);
    }

    public void updateReceivedFrom(String name, String code) {
        model.items.get(0).items.get(3).value = name;
        model.items.get(0).items.get(3).code = code;
        notifySectionItemChanged(0, 3);
    }

    public void updateBankIssuer(String issuer) {
        model.items.get(1).items.get(1).value = issuer;
        notifySectionItemChanged(1, 1);
    }

    public void updateBankBranch(String branch) {
        model.items.get(1).items.get(2).value = branch;
        notifySectionItemChanged(1, 2);
    }

    public void updateMode(CodeDescription codeDescription) {
        model.items.get(1).items.get(0).value = codeDescription.description;
        model.items.get(1).items.get(0).code = codeDescription.code;
        notifySectionItemChanged(1, 0);
    }

    public void updateBillNo(TaxInvoiceModel taxInvoiceModel) {
        model.items.get(0).items.get(1).value = taxInvoiceModel.invoiceNo;
        model.items.get(0).items.get(3).value = taxInvoiceModel.issueToName;
        notifySectionItemChanged(0, 1);
        notifySectionItemChanged(0, 3);
    }

    public void updateTransaction(String transaction) {
        model.items.get(0).items.get(5).value = transaction;
        notifySectionItemChanged(0, 5);
    }

    public boolean isValidProceed() {
        if (model.items.get(0).items.get(0).value.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_file_no_not_select);
            return false;
        }

        if (model.items.get(0).items.get(1).value.trim().length() == 0) {
            DIAlert.showSimpleAlert(context, R.string.alert_bill_no_not_select);
            return false;
        }

        return true;
    }

    public JsonObject buildSaveParams() {
        JsonObject params = new JsonObject();

        JsonObject accountTypeID = new JsonObject();
        accountTypeID.addProperty("ID", model.items.get(0).items.get(1).code);
        params.add("accountType", accountTypeID);
        params.addProperty("amount", model.items.get(0).items.get(4).value);
        params.addProperty("fileNo", model.items.get(0).items.get(0).value);
        params.addProperty("invoiceNo", model.items.get(0).items.get(1).value);
        params.addProperty("receivedFromName", model.items.get(0).items.get(3).value);
        params.addProperty("remarks", model.items.get(1).items.get(5).value);
        JsonObject payment = new JsonObject();
        payment.addProperty("bankBranch", model.items.get(1).items.get(2).value);
        payment.addProperty("totalAmount", model.items.get(1).items.get(4).value);
        payment.addProperty("referenceNo", model.items.get(1).items.get(3).value);
        payment.addProperty("issuerBank", model.items.get(1).items.get(1).value);
        payment.addProperty("mode", model.items.get(1).items.get(0).value);
        params.add("payment", payment);

        return params;
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

            android.os.Handler mHandler = ((AddReceiptActivity)context).getWindow().getDecorView().getHandler();
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


package it.denning.search.accounts.transaction;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.LedgerDetail;

/**
 * Created by denningit on 29/04/2017.
 */

public class TransactionDetailAdapter extends RecyclerView.Adapter {
    Context mContext;
    LedgerDetail model;

    TransactionDetailAdapter(LedgerDetail model, Context context) {
        this.mContext = context;
        this.model = model;
    }

    public class LedgerTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.label) TextView label;
        @BindView(R.id.value) TextView value;
        @BindView(R.id.ledger_cardview)
        CardView cardView;

        public LedgerTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_transaction, parent, false);

        return new LedgerTypeViewHolder(itemView);
    }

    private void displayAccounts(RecyclerView.ViewHolder holder, final int position) {
        LedgerTypeViewHolder ledgerTypeViewHolder = (LedgerTypeViewHolder) holder;
        String label = "", value = "";
        switch (position) {
            case 0:
                label = "Date";
                value = DIHelper.getOnlyDateFromDateTime(model.date);
                break;
            case 1:
                label = "Transaction No.";
                value = model.documentNo;
                break;
            case 2:
                label = "File No.";
                value = model.fileNo;
                break;
            case 3:
                label = "File Name";
                value = model.fileName;
                break;
            case 4:
                label = "Bill No.";
                value = model.taxInvoice;
                break;
            case 5:
                label = "Recd / Paid";
                value = model.recdPaid;
                break;
            case 6:
                label = "Description";
                value = model.description;
                break;
            case 7:
                label = "Dr Amount";
                value = model.amountDR;
                break;
            case 8:
                label = "Cr Amount";
                value = model.amountCR;
                break;
            case 9:
                label = "Mode";
                value = model.paymentMode;
                break;
            case 10:
                label = "Bank a/c";
                value = model.bankAcc;
                break;
            case 11:
                label = "Issued by";
                value = model.issuedBy;
                break;
            case 12:
                label = "Updated by";
                value = model.updatedBy;
                break;
        }
        int color = Color.LTGRAY;
        if (position % 2 == 0) {
            color = Color.WHITE;
        }

        ledgerTypeViewHolder.cardView.setBackgroundColor(color);
        ledgerTypeViewHolder.label.setText(label);
        ledgerTypeViewHolder.value.setText(value);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        displayAccounts(holder, position);
    }

    @Override
    public int getItemCount() {
        return 12;
    }
}

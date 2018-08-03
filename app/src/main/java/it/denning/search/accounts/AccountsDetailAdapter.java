package it.denning.search.accounts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.Accounts;
import it.denning.model.LedgerDetail;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 01/05/2017.
 */

public class AccountsDetailAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<LedgerDetail> LedgerDetailArrayList = new ArrayList<>();
    Map<Integer, Integer> typeSet;
    OnItemClickListener clickListener;

    AccountsDetailAdapter(List<LedgerDetail> LedgerDetailArrayList, Context context, OnItemClickListener clickListener) {
        this.mContext = context;
        this.LedgerDetailArrayList.addAll(LedgerDetailArrayList);
        typeSet = new HashMap<>();
        this.clickListener = clickListener;
    }

    public class LedgerDetailTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.account_date)
        TextView accountDate;
        @BindView(R.id.account_document_no) TextView accountDocumentNo;
        @BindView(R.id.account_description) TextView accountDescription;
        @BindView(R.id.account_amount) TextView accountAmount;
        @BindView(R.id.account_recdpaid) TextView accountRecdPaid;
        @BindView(R.id.card_view)
        CardView cardView;

        public LedgerDetailTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_accounts_detail, parent, false);

        return new LedgerDetailTypeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        LedgerDetailTypeViewHolder ledgerDetailTypeViewHolder = (LedgerDetailTypeViewHolder) holder;
        LedgerDetail LedgerDetail = LedgerDetailArrayList.get(position);
        ledgerDetailTypeViewHolder.accountDate.setText(DIHelper.getOnlyDateFromDateTime(LedgerDetail.date));
        ledgerDetailTypeViewHolder.accountDocumentNo.setText(LedgerDetail.documentNo);
        ledgerDetailTypeViewHolder.accountDescription.setText(LedgerDetail.description);
        ledgerDetailTypeViewHolder.accountAmount.setText(LedgerDetail.displayAmount);
        ledgerDetailTypeViewHolder.accountRecdPaid.setText(LedgerDetail.recdPaid);
        ledgerDetailTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, position);
            }
        });
    }

    public void updateAdapter(List<LedgerDetail> _accountsList) {
        this.LedgerDetailArrayList.clear();
        this.LedgerDetailArrayList.addAll(new ArrayList<LedgerDetail>(_accountsList));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return LedgerDetailArrayList.size();
    }

    public List<LedgerDetail> getModels() {
        return LedgerDetailArrayList;
    }
}

package it.denning.navigation.dashboard.section2;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.LedgerDetail;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class CollectionAdapter extends RecyclerView.Adapter {
    private final ArrayList<LedgerDetail> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public CollectionAdapter(ArrayList<LedgerDetail> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.date_textview)
        TextView openDate;
        @BindView(R.id.amount_textView)
        TextView amount;
        @BindView(R.id.recv_textview)
        TextView recv;
        @BindView(R.id.fileno_textview)
        TextView fileNo;
        @BindView(R.id.balancetype_textview)
        TextView balanceType;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getLayoutPosition());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_collection, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LedgerDetail model = modelArrayList.get(position);
        CustomViewHolder customViewHolder = (CustomViewHolder) holder;
        customViewHolder.fileNo.setText(model.documentNo);
        customViewHolder.amount.setText(model.displayAmount);
        customViewHolder.openDate.setText(DIHelper.getOnlyDateFromDateTime(model.date));
        customViewHolder.balanceType.setText(model.description);
        customViewHolder.recv.setText(model.recdPaid);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public List<LedgerDetail> getModels() {
        return modelArrayList;
    }

    public void swapItems(List<LedgerDetail> newSearchResultList) {
        this.modelArrayList.clear();
        this.modelArrayList.addAll(new ArrayList<>(newSearchResultList));
        notifyDataSetChanged();
    }
}

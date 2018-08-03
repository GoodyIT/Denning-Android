package it.denning.navigation.dashboard.section4.profitloss;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.ItemModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class ProfitLossAdapter extends RecyclerView.Adapter {
    private final ArrayList<ItemModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public ProfitLossAdapter(ArrayList<ItemModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.name_textview)
        TextView contactName;
        @BindView(R.id.id_textView)
        TextView contactID;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
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
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profit_loss, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemModel model = modelArrayList.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        itemViewHolder.contactID.setText(model.value);
        itemViewHolder.contactName.setText(model.itemId);
        if (Float.parseFloat(String.valueOf(model.value)) > 0) {
            itemViewHolder.contactID.setTextColor(Color.parseColor("#FF3B2F"));
        } else {
            itemViewHolder.contactID.setTextColor(Color.parseColor("#FF5CE499"));
        }
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void swapItems(ArrayList<ItemModel> newList) {
        modelArrayList.clear();
        modelArrayList.addAll(newList);

        notifyDataSetChanged();
    }
}

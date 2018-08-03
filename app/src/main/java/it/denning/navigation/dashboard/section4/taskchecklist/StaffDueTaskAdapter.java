package it.denning.navigation.dashboard.section4.taskchecklist;

import android.content.Context;
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
import it.denning.model.ItemModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 11/09/2017.
 */

public class StaffDueTaskAdapter extends RecyclerView.Adapter {
    private final ArrayList<ItemModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public StaffDueTaskAdapter(ArrayList<ItemModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.name_textview)
        TextView nameText;
        @BindView(R.id.badge_textview)
        TextView badgeView;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_staff_due_task, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemModel model = modelArrayList.get(position);
        CustomViewHolder customViewHolder = (CustomViewHolder)holder;
        customViewHolder.nameText.setText(model.label);
        customViewHolder.badgeView.setText(model.value);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void swapItems(List<ItemModel> newSearchResultList) {

        this.modelArrayList.clear();
        this.modelArrayList.addAll(newSearchResultList);

        notifyDataSetChanged();
    }

    public void clear() {
        this.modelArrayList.clear();
    }
}

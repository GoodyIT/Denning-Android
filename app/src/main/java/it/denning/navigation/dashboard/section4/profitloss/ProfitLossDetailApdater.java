package it.denning.navigation.dashboard.section4.profitloss;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.ProfitLossDetailModel;

/**
 * Created by hothongmee on 12/09/2017.
 */

public class ProfitLossDetailApdater extends RecyclerView.Adapter {
    private ProfitLossDetailModel mModel;

    public ProfitLossDetailApdater(ProfitLossDetailModel mModel) {
        this.mModel = mModel;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name_textview)
        TextView contactName;
        @BindView(R.id.id_textView)
        TextView contactID;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profit_loss, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
        String revenue = "0.0", expenses = "0.0", profitLoss = "0.0";
        if (mModel != null) {
            revenue = mModel.revenue;
            expenses = mModel.expenses;
            profitLoss = mModel.profitLoss;
        }

        switch (position){
            case 0:
                itemViewHolder.contactName.setText("Revenue");
                itemViewHolder.contactID.setText(revenue);
                break;
            case 1:
                itemViewHolder.contactName.setText("Expenses");
                itemViewHolder.contactID.setText(expenses);
                break;
            case 2:
                itemViewHolder.contactName.setText("Profit/Loss");
                itemViewHolder.contactID.setText(profitLoss);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setItem(ProfitLossDetailModel model) {
        this.mModel = model;
        notifyDataSetChanged();
    }
}

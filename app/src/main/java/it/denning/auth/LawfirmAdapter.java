package it.denning.auth;

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
import it.denning.model.LegalFirm;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 03/05/2017.
 */

public class LawfirmAdapter extends RecyclerView.Adapter {
    ArrayList<LegalFirm> legalFirmArrayList = new ArrayList<>();
    OnItemClickListener itemClickListener;

    LawfirmAdapter(ArrayList<LegalFirm> legalFirmArrayList) {
        this.legalFirmArrayList.addAll(legalFirmArrayList);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class LawfirmViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_last)
        TextView lawfirmName;
        @BindView(R.id.search_cardview)
        CardView cardview;

        public LawfirmViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_search_last, parent, false);

        return new LawfirmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        LawfirmViewHolder lawfirmViewHolder = (LawfirmViewHolder) holder;
        lawfirmViewHolder.lawfirmName.setText(legalFirmArrayList.get(position).name);
        lawfirmViewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return legalFirmArrayList.size();
    }

    public void addItems(List<LegalFirm> items) {
        legalFirmArrayList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        legalFirmArrayList.clear();
        notifyDataSetChanged();
    }
}

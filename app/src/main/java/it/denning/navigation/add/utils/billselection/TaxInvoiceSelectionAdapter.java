package it.denning.navigation.add.utils.billselection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.TaxInvoiceItem;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class TaxInvoiceSelectionAdapter extends SectioningAdapter {
    private final List<TaxInvoiceItem> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public TaxInvoiceSelectionAdapter(List<TaxInvoiceItem> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public List<TaxInvoiceItem> getModels() {
        return modelArrayList;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder  implements View.OnClickListener{
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.second_textView)
        TextView secondValue;
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

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;
        @BindView(R.id.second_textview)
        TextView secondTitle;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return 1;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return modelArrayList.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return false;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_two_column, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_three_column_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        TaxInvoiceItem model = modelArrayList.get(itemIndex);
        ItemViewHolder customViewHolder = (ItemViewHolder) viewHolder;
        customViewHolder.firstValue.setText(model.description);
        customViewHolder.secondValue.setText(model.amount);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {

    }

    public void swapItems(List<TaxInvoiceItem> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifyAllSectionsDataSetChanged();
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

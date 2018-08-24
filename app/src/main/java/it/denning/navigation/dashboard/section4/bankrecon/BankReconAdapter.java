package it.denning.navigation.dashboard.section4.bankrecon;

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
import it.denning.model.BankReconModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class BankReconAdapter extends SectioningAdapter {
    private final List<BankReconModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public BankReconAdapter(List<BankReconModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class BankReconItemViewHolder extends SectioningAdapter.ItemViewHolder  implements View.OnClickListener{
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.second_textView)
        TextView secondValue;
        @BindView(R.id.third_textView)
        TextView thirdValue;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public BankReconItemViewHolder(View itemView) {
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
        @BindView(R.id.third_textview)
        TextView thirdTitle;

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
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public BankReconItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_three_column, parent, false);
        return new BankReconItemViewHolder(v);
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
        BankReconModel model = modelArrayList.get(itemIndex);
        BankReconItemViewHolder customViewHolder = (BankReconItemViewHolder) viewHolder;
        customViewHolder.firstValue.setText(model.accountName);
        customViewHolder.secondValue.setText(model.accountNo);
        customViewHolder.thirdValue.setText(model.lastMovement);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.firstTitle.setText("A/C name");
        headerViewHolder.secondTitle.setText("A/C no");
        headerViewHolder.thirdTitle.setText("Date Last");
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void swapItems(List<BankReconModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifySectionItemInserted(0, newSearchResultList.size() +  1);
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

package it.denning.navigation.dashboard.section4.trialbalance;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.StaffOnlineModel;
import it.denning.model.TrialBalance;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceAdapter;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class TrialBalanceAdapter extends SectioningAdapter {
    private final List<TrialBalance> modelArrayList;
    private final OnItemClickListener clickListener;

    public TrialBalanceAdapter(List<TrialBalance> modelArrayList, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.clickListener = clickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.second_textView)
        TextView secondValue;
        @BindView(R.id.third_textView)
        TextView thirdValue;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;
        @BindView(R.id.forth_imageview)
        ImageView imageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;
        @BindView(R.id.second_textView)
        TextView secondTitle;
        @BindView(R.id.third_textView)
        TextView thirdTitle;
        @BindView(R.id.forth_textView)
        TextView forthTitle;

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
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_trial_balance_detail, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_trial_balance_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        TrialBalance model = modelArrayList.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.firstValue.setText(model.accountName);
        itemViewHolder.secondValue.setText(DIHelper.addThousandsSeparator(model.debit));
        itemViewHolder.thirdValue.setText(DIHelper.addThousandsSeparator(model.credit));
        if (model.isBalance.equals("No")) {
            itemViewHolder.imageView.setImageResource(R.drawable.ic_close_red);
        } else {
            itemViewHolder.imageView.setImageResource(R.drawable.ic_check);
        }

        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, itemIndex);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.firstTitle.setText("A/C");
        headerViewHolder.secondTitle.setText("Debit");
        headerViewHolder.thirdTitle.setText("Credit");
        headerViewHolder.forthTitle.setText("Balance");
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void swapItems(List<TrialBalance> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifySectionItemInserted(0, newSearchResultList.size() +  1);
    }

    public List<TrialBalance> getModels() {
        return modelArrayList;
    }
}

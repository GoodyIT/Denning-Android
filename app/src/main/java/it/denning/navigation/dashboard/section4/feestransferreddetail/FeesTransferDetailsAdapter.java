package it.denning.navigation.dashboard.section4.feestransferreddetail;

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
import it.denning.model.FeesUnTransferModel;
import it.denning.navigation.dashboard.section4.feestransfer.untransferred.UnTransferredAdapter;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class FeesTransferDetailsAdapter extends SectioningAdapter {
    private final List<FeesUnTransferModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public FeesTransferDetailsAdapter(List<FeesUnTransferModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public List<FeesUnTransferModel> getModels() {
        return modelArrayList;
    }

    public class TransferredViewHolder extends ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.first_textview)
        TextView firstTextView;
        @BindView(R.id.first_bottom_textview)
        TextView firstBottomTextView;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;

        public TransferredViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_fees_untransferred, parent, false);
        return new TransferredViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_three_column_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
       TransferredViewHolder holder = (TransferredViewHolder) viewHolder;
        FeesUnTransferModel model = modelArrayList.get(itemIndex);
        holder.firstTextView.setText(model.fileNo);
        holder.firstBottomTextView.setText(model.fileName);
        holder.secondTextView.setText(model.invoiceNo);
        holder.thirdTextView.setText(model.fee);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        headerViewHolder.firstTitle.setText("File No.");
        headerViewHolder.secondTitle.setText("Tax Invoice No.");
        headerViewHolder.thirdTitle.setText("Amount");
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void swapItems(List<FeesUnTransferModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifyAllSectionsDataSetChanged();
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

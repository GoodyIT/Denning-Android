package it.denning.navigation.dashboard.section4.dashboardcontactfolder;

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
import it.denning.general.DIHelper;
import it.denning.model.ContactFolderItem;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 10/09/2017.
 */

public class DashboardContactFolderAdapter extends SectioningAdapter {
    private final List<ContactFolderItem> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public DashboardContactFolderAdapter(List<ContactFolderItem> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public List<ContactFolderItem> getModels() {
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
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
       TransferredViewHolder holder = (TransferredViewHolder) viewHolder;
        ContactFolderItem model = modelArrayList.get(itemIndex);
        holder.firstTextView.setText(model.strContactName);
        holder.firstBottomTextView.setText(model.strContactID);
        holder.secondTextView.setText(DIHelper.getOnlyDateFromDateTime(model.dtLastModified));
        holder.thirdTextView.setText(model.strItemCount);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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
        headerViewHolder.firstTitle.setText("Contact");
        headerViewHolder.secondTitle.setText("Last Modified");
        headerViewHolder.thirdTitle.setText("Items");
    }

    public void swapItems(List<ContactFolderItem> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifyAllSectionsDataSetChanged();
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }

}

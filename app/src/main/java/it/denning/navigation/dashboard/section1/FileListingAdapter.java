package it.denning.navigation.dashboard.section1;

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
import it.denning.model.SearchResultModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class FileListingAdapter extends SectioningAdapter {
    private final List<SearchResultModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public FileListingAdapter(List<SearchResultModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener{
        @BindView(R.id.opendate_textView)
        TextView openDate;
        @BindView(R.id.filename_textview)
        TextView fileName;
        @BindView(R.id.fileno_textview)
        TextView fileNo;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getLayoutPosition());
        }
    }


    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.filenoTextView)
        TextView fileName;
        @BindView(R.id.filenameTextView)
        TextView fileNo;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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
        View v = inflater.inflate(R.layout.cardview_filelisting, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_file_listing_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        SearchResultModel model = modelArrayList.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.fileNo.setText(model.key);
        try
        {
            itemViewHolder.openDate.setText(DIHelper.getOnlyDateFromDateTime(model.sortDate));
        } catch (Exception e) {

        }

        String title= "";
        if (model.title.contains("File No.")) {
            title = model.title.substring(10);
        }
        itemViewHolder.fileName.setText(DIHelper.separateNameIntoTwo(title)[1]);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.fileNo.setText("File No");
        headerViewHolder.fileName.setText("File Name");
    }

    public void swapItems(List<SearchResultModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifySectionItemInserted(0, newSearchResultList.size() +  1);
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

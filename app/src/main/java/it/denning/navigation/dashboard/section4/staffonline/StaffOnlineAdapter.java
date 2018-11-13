package it.denning.navigation.dashboard.section4.staffonline;

import android.annotation.SuppressLint;
import android.content.Context;
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
import it.denning.model.SearchResultModel;
import it.denning.model.StaffOnlineModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 09/09/2017.
 */

public class StaffOnlineAdapter extends SectioningAdapter {
    private final List<StaffOnlineModel> modelArrayList;
    private final Context mContext;
    private final OnItemClickListener clickListener;

    public StaffOnlineAdapter(List<StaffOnlineModel> modelArrayList, Context mContext, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
        this.clickListener = clickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener{
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.exe_ImageView)
        ImageView exeStatus;
        @BindView(R.id.web_ImageView)
        ImageView webStatus;
        @BindView(R.id.app_ImageView)
        ImageView appStatus;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getLayoutPosition());
        }
    }


    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.exe_textView)
        TextView exetitle;
        @BindView(R.id.web_textView)
        TextView webTItle;
        @BindView(R.id.app_textView)
        TextView appTitle;

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
        View v = inflater.inflate(R.layout.cardview_staff_online, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_staff_online_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        StaffOnlineModel model = modelArrayList.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.firstValue.setText(model.name);
        if (model.onlineWeb) {
            itemViewHolder.webStatus.setImageResource(R.drawable.ic_status_green);
        } else {
            itemViewHolder.webStatus.setImageResource(R.drawable.ic_status_offline);
        }

        if (model.onlineExe) {
            itemViewHolder.exeStatus.setImageResource(R.drawable.ic_status_green);
        } else {
            itemViewHolder.exeStatus.setImageResource(R.drawable.ic_status_offline);
        }

        if (model.onlineApp) {
            itemViewHolder.appStatus.setImageResource(R.drawable.ic_status_green);
        } else {
            itemViewHolder.appStatus.setImageResource(R.drawable.ic_status_offline);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.firstValue.setText("Staff");
        headerViewHolder.exetitle.setText("360");
        headerViewHolder.webTItle.setText("Web");
        headerViewHolder.appTitle.setText("App");
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void swapItems(List<StaffOnlineModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifySectionItemInserted(0, newSearchResultList.size() +  1);
    }

    public void clear() {
        this.modelArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

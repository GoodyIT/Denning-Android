package it.denning.navigation.dashboard.section4.dashboardattendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import it.denning.model.StaffOnlineModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class DashboardAttendanceAdapter extends SectioningAdapter {

    private final List<StaffOnlineModel> modelArrayList;
    private final Context context;
    private final OnItemClickListener clickListener;


    public DashboardAttendanceAdapter(List<StaffOnlineModel> modelArrayList, Context context, OnItemClickListener clickListener) {
        this.modelArrayList = modelArrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener{
        @BindView(R.id.status_textView)
        TextView status;
        @BindView(R.id.staff_textview)
        TextView staff;
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
        View v = inflater.inflate(R.layout.cardview_dashboard_attendance, parent, false);
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
        StaffOnlineModel model = modelArrayList.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.staff.setText(model.name);
        itemViewHolder.status.setText(model.status);
        if (model.status.equals("ON-DUTY")) {
            itemViewHolder.status.setTextColor(context.getResources().getColor(R.color.babyGreen));
        } else {
            itemViewHolder.status.setTextColor(Color.RED);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.fileName.setText("Staff");
        headerViewHolder.fileNo.setText("Status");
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

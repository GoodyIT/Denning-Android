package it.denning.navigation.dashboard.section1.staffleave.leaveapp;

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
import it.denning.model.LeaveRecordModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2017-12-24.
 */

public class DashboardLeaveAppAdapter extends SectioningAdapter {
    private final List<LeaveRecordModel> models;
    OnItemClickListener clickListener;

    public DashboardLeaveAppAdapter(List<LeaveRecordModel> models, OnItemClickListener clickListener) {
        this.models = models;
        this.clickListener = clickListener;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LeaveRecordViewHolder extends ItemViewHolder {
        @BindView(R.id.dashboard_cardview)
        CardView cardView;
        @BindView(R.id.first_textview)
        TextView firstTextView;
        @BindView(R.id.second_textView)
        TextView secondTextView;
        @BindView(R.id.third_textView)
        TextView thirdTextView;

        public LeaveRecordViewHolder(View itemView) {
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
        int count = models.size();
        return count;
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
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        return sectionIndex;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_three_column_detail, parent, false);
        return new LeaveRecordViewHolder(view);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_three_column_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayItemSection(viewHolder, itemIndex);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        displayHeader(headerViewHolder, "Staff", "Period", "Type");
    }

    private void displayHeader(HeaderViewHolder viewHolder, String first, String second, String third) {
        viewHolder.firstTitle.setText(first);
        viewHolder.secondTextView.setText(second);
        viewHolder.thirdTextView.setText(third);
    }

    private void displayItemSection(ItemViewHolder viewHolder, final int itemIndex) {
        LeaveRecordViewHolder holder = (LeaveRecordViewHolder) viewHolder;
        LeaveRecordModel model = models.get(itemIndex);
        holder.firstTextView.setText(model.getStaffName());
        holder.secondTextView.setText(model.getDtStartDate() + "\n" + model.getDtEndDate());
        holder.thirdTextView.setText(model.getTypeOfLeaveCode());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, itemIndex);
            }
        });
    }

    public void addItems(List<LeaveRecordModel> items) {
        models.addAll(items);
        notifyAllSectionsDataSetChanged();
    }

    public void setItems(List<LeaveRecordModel> items) {
        models.clear();
        addItems(items);
    }

    public void clear() {
        models.clear();
    }

    public List<LeaveRecordModel> getModels() {
        return models;
    }
}
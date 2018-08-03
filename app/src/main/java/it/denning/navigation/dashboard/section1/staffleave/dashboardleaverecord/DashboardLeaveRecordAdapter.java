package it.denning.navigation.dashboard.section1.staffleave.dashboardleaverecord;

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

/**
 * Created by denningit on 2017-12-24.
 */

public class DashboardLeaveRecordAdapter extends SectioningAdapter {
    private final List<LeaveRecordModel> models;

    public DashboardLeaveRecordAdapter(List<LeaveRecordModel> models) {
        this.models = models;
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;
        @BindView(R.id.forth_textview)
        TextView forthTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class LeaveRecordViewHolder extends ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.first_textview)
        TextView firstTextView;
        @BindView(R.id.second_textview)
        TextView secondTextView;
        @BindView(R.id.third_textview)
        TextView thirdTextView;
        @BindView(R.id.forth_textview)
        TextView forthTextView;

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
        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_four_column, parent, false);
        return new LeaveRecordViewHolder(view);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_four_column_header_1, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayItemSection(viewHolder, itemIndex);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        displayHeader(headerViewHolder, "Staff", "PYL", "Taken", "AL");
    }

    private void displayHeader(HeaderViewHolder viewHolder, String first, String second, String third, String forth) {
        viewHolder.firstTitle.setText(first);
        viewHolder.secondTextView.setText(second);
        viewHolder.thirdTextView.setText(third);
        viewHolder.forthTextView.setText(forth);
    }

    private void displayItemSection(ItemViewHolder viewHolder, int itemIndex) {
        LeaveRecordViewHolder holder = (LeaveRecordViewHolder) viewHolder;
        LeaveRecordModel model = models.get(itemIndex);
        holder.firstTextView.setText(model.getStaffName());
        holder.secondTextView.setText(model.getIntPYL());
        holder.thirdTextView.setText(model.getIntAL());
        holder.forthTextView.setText(model.getIntTaken());
    }

    public void addItems(List<LeaveRecordModel> items) {
        models.addAll(items);
        notifyAllSectionsDataSetChanged();
    }

    public void setItems(List<LeaveRecordModel> items) {
        models.clear();
        addItems(items);
    }
}
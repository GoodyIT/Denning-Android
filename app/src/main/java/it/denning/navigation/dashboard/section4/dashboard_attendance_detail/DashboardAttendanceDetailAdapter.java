package it.denning.navigation.dashboard.section4.dashboard_attendance_detail;

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
import it.denning.model.AttendanceDetailModel;
import it.denning.model.StaffOnlineModel;
import it.denning.navigation.dashboard.section4.staffonline.StaffOnlineAdapter;

/**
 * Created by hothongmee on 09/11/2017.
 */

public class DashboardAttendanceDetailAdapter extends SectioningAdapter {
    final private List<AttendanceDetailModel> modelArrayList;

    public DashboardAttendanceDetailAdapter(List<AttendanceDetailModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.second_textView)
        TextView secondvalue;
        @BindView(R.id.third_textView)
        ImageView thirdValue;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstValue;
        @BindView(R.id.second_textview)
        TextView secondvalue;
        @BindView(R.id.third_textview)
        TextView thirdValue;

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
        View v = inflater.inflate(R.layout.cardview_three_column, parent, false);
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
        AttendanceDetailModel model = modelArrayList.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.firstValue.setText(model.timeIn);
        itemViewHolder.secondvalue.setText(model.timeOut);
        itemViewHolder.thirdValue.setTag(model.hours);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.firstValue.setText("Time-In");
        headerViewHolder.secondvalue.setText("Time-Out");
        headerViewHolder.thirdValue.setText("Hours");
    }

    public void swapItems(List<AttendanceDetailModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        notifySectionItemInserted(0, newSearchResultList.size() +  1);
    }
}

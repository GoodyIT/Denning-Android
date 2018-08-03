package it.denning.navigation.dashboard.util;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.DashboardModel;
import it.denning.model.S1;
import it.denning.model.SecondItemModel;

/**
 * Created by hothongmee on 06/09/2017.
 */

public class DashboardThirdAdapter extends SectioningAdapter {
    private final Context mContext;
    private final DashboardModel mDashboardModel;
    private final DashboardSecondItemClickListener clickListener;
    Point size;

    public DashboardThirdAdapter(Context context, DashboardModel dashboardModel, DashboardSecondItemClickListener clickListener) {
        this.mContext = context;
        this.mDashboardModel = dashboardModel;
        this.clickListener = clickListener;
        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public class ThirdHeaderViewHolder extends HeaderViewHolder {

        @BindView(R.id.third_section_title)
        TextView  title;

        public ThirdHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThirdContentViewHolder extends ItemViewHolder {
        @BindView(R.id.dashboard_cardview)
        CardView cardView;
        @BindView(R.id.status_imageview)
        TextView  label;
        @BindView(R.id.third_section_first)
        TextView firstValue;
        @BindView(R.id.third_section_second)
        TextView secondValue;
        @BindView(R.id.third_layout)
        RelativeLayout layout;

        public ThirdContentViewHolder(View itemView) {
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
        int count  = mDashboardModel.s2.items.size();
        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return  true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }


    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_content, parent, false);
        itemViewHolder = new ThirdContentViewHolder(view);

        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        HeaderViewHolder headerViewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_header, parent, false);
        headerViewHolder =  new ThirdHeaderViewHolder(view);

        return headerViewHolder;
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        displayThirdHeader((ThirdHeaderViewHolder)viewHolder, sectionIndex);
    }

    public void displayThirdHeader(ThirdHeaderViewHolder viewHolder, int sectionIndex) {
        ThirdHeaderViewHolder headerViewHolder = (ThirdHeaderViewHolder)viewHolder;
        headerViewHolder.title.setText(mDashboardModel.s2.title);
    }

    public void displayThirdItemContent(RecyclerView.ViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final SecondItemModel itemModel = mDashboardModel.s2.items.get(itemIndex);
        ThirdContentViewHolder contentViewHolder = (ThirdContentViewHolder) viewHolder;
        contentViewHolder.label.setText(itemModel.label);
        contentViewHolder.firstValue.setText(itemModel.RM);
        contentViewHolder.secondValue.setText(itemModel.deposited + " (" + itemModel.OR + ")");
        contentViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onSecondItemClick("collection", itemIndex, itemModel.label, itemModel.api);
            }
        });
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayThirdItemContent(viewHolder, sectionIndex, itemIndex);
    }
}

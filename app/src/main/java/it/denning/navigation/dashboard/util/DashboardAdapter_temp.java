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
import it.denning.model.S2;
import it.denning.model.S3;
import it.denning.model.SecondItemModel;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;

/**
 * Created by hothongmee on 06/09/2017.
 */

public class DashboardAdapter_temp extends SectioningAdapter {
    private final Context mContext;
    private final DashboardModel mDashboardModel;
    private final DashboardSecondItemClickListener clickListener;
    Point size;

    public DashboardAdapter_temp(Context context, DashboardModel dashboardModel, DashboardSecondItemClickListener clickListener) {
        this.mContext = context;
        this.mDashboardModel = dashboardModel;
        this.clickListener = clickListener;
        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public class TodayViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.textview_today)
        TextView today;
        public TodayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SecondViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.dashboard_second_gridview)
        GridView gridView;
        @BindView(R.id.gridview_parent)
        RelativeLayout relativeLayout;

        public SecondViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            gridView.setColumnWidth(size.x/3-9);
        }
    }

    public class ThirdHeaderViewHolder extends SectioningAdapter.HeaderViewHolder {

        @BindView(R.id.third_section_title)
        TextView  title;

        public ThirdHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThirdContentViewHolder extends SectioningAdapter.ItemViewHolder {
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

    public class ForthHeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.forth_section_title)
        TextView title;

        public ForthHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return 5;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 0;
        switch (sectionIndex) {
            case 0: // Today
                count = 1;
                break;
            case 1:
                count = 1;
                break;
            case 2:
                count = mDashboardModel.s2.items.size();
                break;
            case 3:
                count = 1;
                break;
            case 4:
                count = 1;
                break;
        }
        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        boolean hasHeader = false;
        switch (sectionIndex) {
            case 0: // Today
                hasHeader = false;
                break;
            case 1:
                hasHeader = false;
                break;
            case 2:
                hasHeader = true;
                break;
            case 3:
                hasHeader = true;
                break;
            case 4:
                hasHeader = false;
                break;
        }

        return hasHeader;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        int itemUserType = -1;
        switch (sectionIndex) {

            case 0:
                itemUserType = DIConstants.DASHBOARD_FIRST_SECTION;
                break;
            case 1:
                itemUserType = DIConstants.DASHBOARD_SECOND_SECTION;
                break;
            case 2:
                itemUserType = DIConstants.DASHBOARD_THIRD_SECTION;
                break;
            case 3:
                itemUserType = DIConstants.DASHBOARD_FORTH_SECTION;
                break;
            case 4: // Today
                itemUserType = DIConstants.DASHBOARD_FIFTH_SECTION;
                break;
        }

        return itemUserType;
    }

    @Override
    public int getSectionHeaderUserType(int sectionIndex) {
        int headerUserType = -1;
        switch (sectionIndex) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                headerUserType = DIConstants.DASHBOARD_THIRD_SECTION_HEADER;
                break;
            case 3:
                headerUserType = DIConstants.DASHBOARD_FORTH_SECTION_HEADER;
                break;
            case 4: // Today
                break;
        }

        return headerUserType;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        ItemViewHolder itemViewHolder = null;
        View view = null;
        switch (itemUserType) {
            case DIConstants.DASHBOARD_FIRST_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_first, parent, false);
                itemViewHolder =  new TodayViewHolder(view);
                break;
            case DIConstants.DASHBOARD_SECOND_SECTION:
            case DIConstants.DASHBOARD_FORTH_SECTION:
            case DIConstants.DASHBOARD_FIFTH_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_second, parent, false);
                itemViewHolder = new SecondViewHolder(view);
                break;
            case DIConstants.DASHBOARD_THIRD_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_content, parent, false);
                itemViewHolder = new ThirdContentViewHolder(view);
            default:
                break;
        }

        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        HeaderViewHolder headerViewHolder = null;
        View view = null;
        switch (headerType) {
            case DIConstants.DASHBOARD_THIRD_SECTION_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_header, parent, false);
                headerViewHolder =  new ThirdHeaderViewHolder(view);
                break;
            case DIConstants.DASHBOARD_FORTH_SECTION_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_forth_header, parent, false);
                headerViewHolder =  new ForthHeaderViewHolder(view);
                break;
        }

        return headerViewHolder;
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        switch (headerType) {
            case DIConstants.DASHBOARD_THIRD_SECTION_HEADER:
                displayThirdHeader((ThirdHeaderViewHolder)viewHolder, sectionIndex);
                break;
            case DIConstants.DASHBOARD_FORTH_SECTION_HEADER:
                displayForthHeader((ForthHeaderViewHolder)viewHolder, sectionIndex);
                break;
        }
    }

    public void displayThirdHeader(ThirdHeaderViewHolder viewHolder, int sectionIndex) {
        ThirdHeaderViewHolder headerViewHolder = (ThirdHeaderViewHolder)viewHolder;
        headerViewHolder.title.setText(mDashboardModel.s2.title);
    }

    public void displayForthHeader(ForthHeaderViewHolder viewHolder, int sectionIndex) {
        ForthHeaderViewHolder headerViewHolder = (ForthHeaderViewHolder)viewHolder;
        headerViewHolder.title.setText(mDashboardModel.s3.title);
    }

    public void displaySecondItem(RecyclerView.ViewHolder viewHolder, final S1 s1) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        ViewGroup.LayoutParams layoutParams = secondViewHolder.gridView.getLayoutParams();
        layoutParams.height = size.x/3 * s1.items.size()/3; //this is in pixels
        secondViewHolder.gridView.setLayoutParams(layoutParams);
        final DashboardSecondItemFrameAdapter itemAdapter = new DashboardSecondItemFrameAdapter(mContext, s1.items);
        secondViewHolder.gridView.setAdapter(itemAdapter);
        secondViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListener.onSecondItemClick(s1.items.get(position).formName,  position, "",  s1.items.get(position).mainAPI);
            }
        });
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

    public void displayForthItem(RecyclerView.ViewHolder viewHolder, int sectionIndex, int itemIndex) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        final DashboardForthItemAdapter itemAdapter = new DashboardForthItemAdapter(mContext, mDashboardModel.s3.items, -1);
        ViewGroup.LayoutParams layoutParams = secondViewHolder.gridView.getLayoutParams();
        layoutParams.height = size.x/2; //this is in pixels
        secondViewHolder.gridView.setLayoutParams(layoutParams);
        secondViewHolder.gridView.setAdapter(itemAdapter);
        secondViewHolder.gridView.setColumnWidth(size.x/4-9);
        secondViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void displayFifthItem(RecyclerView.ViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        final DashboardFifthItemAdapter itemAdapter = new DashboardFifthItemAdapter(mContext, mDashboardModel.s4.items);
        ViewGroup.LayoutParams layoutParams = secondViewHolder.gridView.getLayoutParams();
        layoutParams.height = size.x/3 * mDashboardModel.s4.items.size()/3; //this is in pixels
        secondViewHolder.gridView.setLayoutParams(layoutParams);
        secondViewHolder.gridView.setAdapter(itemAdapter);
        final String formName = mDashboardModel.s4.items.get(itemIndex).formName;
        final String api = mDashboardModel.s4.items.get(itemIndex).mainAPI;
        secondViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListener.onSecondItemClick(formName, itemIndex, "",  api);
            }
        });
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        switch (itemType) {
            case DIConstants.DASHBOARD_FIRST_SECTION: // today
                ((TodayViewHolder)viewHolder).today.setText(DIHelper.getOnlyDateFromDateTime(mDashboardModel.todayDate));
                break;
            case DIConstants.DASHBOARD_SECOND_SECTION: // S1 - Matter
                displaySecondItem(viewHolder, mDashboardModel.s1);
                break;
            case DIConstants.DASHBOARD_THIRD_SECTION: // S2 - Collection
                displayThirdItemContent(viewHolder, sectionIndex, itemIndex);
                break;
            case DIConstants.DASHBOARD_FORTH_SECTION:
                displayForthItem(viewHolder, sectionIndex, itemIndex);
                break;
            default: // S4 - More
                displayFifthItem(viewHolder, sectionIndex, itemIndex);
                break;
        }
    }
}

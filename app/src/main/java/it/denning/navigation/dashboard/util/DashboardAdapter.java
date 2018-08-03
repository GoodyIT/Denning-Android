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

/**
 * Created by hothongmee on 06/09/2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final DashboardModel mDashboardModel;
    private final DashboardSecondItemClickListener clickListener;
    Point size;

    public DashboardAdapter(Context context, DashboardModel dashboardModel, DashboardSecondItemClickListener clickListener) {
        this.mContext = context;
        this.mDashboardModel = dashboardModel;
        this.clickListener = clickListener;
        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public class TodayViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.textview_today)
        TextView today;
        public TodayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SecondViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dashboard_second_gridview)
        GridView gridView;
        @BindView(R.id.gridview_parent)
        RelativeLayout relativeLayout;

        public SecondViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThirdHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.third_section_title)
        TextView  title;

        public ThirdHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThirdContentViewHolder extends RecyclerView.ViewHolder {
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

    public class ForthHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.forth_section_title)
        TextView title;

        public ForthHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case DIConstants.DASHBOARD_FIRST_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_first, parent, false);
                return new TodayViewHolder(view);
            case DIConstants.DASHBOARD_SECOND_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_second, parent, false);
                return new SecondViewHolder(view);
            case DIConstants.DASHBOARD_THIRD_SECTION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_header, parent, false);
                return new ThirdHeaderViewHolder(view);
            default:
                break;
        }

        viewType = viewType - 3 ;
        if (viewType < mDashboardModel.s2.items.size()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_third_content, parent, false);
            return new ThirdContentViewHolder(view);
        }

        viewType = viewType - mDashboardModel.s2.items.size();
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_forth_header, parent, false);
            return new ForthHeaderViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_second, parent, false);
            return new SecondViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_dashboard_section_second, parent, false);
        return new SecondViewHolder(view);
    }

    public void displaySecondItem(RecyclerView.ViewHolder viewHolder, final S1 s1) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        final DashboardSecondItemAdapter itemAdapter = new DashboardSecondItemAdapter(mContext, s1.items);
        secondViewHolder.gridView.setAdapter(itemAdapter);
        ViewGroup.LayoutParams layoutParams = secondViewHolder.gridView.getLayoutParams();
        layoutParams.height = size.x*4/3; //this is in pixels
        secondViewHolder.gridView.setLayoutParams(layoutParams);
        secondViewHolder.gridView.setColumnWidth(size.x/3-10);
        secondViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListener.onSecondItemClick(s1.items.get(position).formName,  position, "",  s1.items.get(position).mainAPI);
            }
        });
    }

    public void displayThirdItemHeader(RecyclerView.ViewHolder viewHolder, S2 s2) {
        ThirdHeaderViewHolder headerViewHolder = (ThirdHeaderViewHolder)viewHolder;
        headerViewHolder.title.setText(s2.title);
    }

    public void displayThirdItemContent(RecyclerView.ViewHolder viewHolder, final SecondItemModel itemModel, final int position) {
        ThirdContentViewHolder contentViewHolder = (ThirdContentViewHolder) viewHolder;
        if (position == 2) {
       //     contentViewHolder.layout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
        }
        contentViewHolder.label.setText(itemModel.label);
        contentViewHolder.firstValue.setText(itemModel.RM);
        contentViewHolder.secondValue.setText(itemModel.deposited + " (" + itemModel.OR + ")");
        contentViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onSecondItemClick("collection", position, itemModel.label, itemModel.api);
            }
        });
    }

    public void displayForthItemHeader(RecyclerView.ViewHolder viewHolder, S3 s3) {
        ForthHeaderViewHolder headerViewHolder = (ForthHeaderViewHolder)viewHolder;
        headerViewHolder.title.setText(s3.title);
    }

    public void displayForthItem(RecyclerView.ViewHolder viewHolder, S3 s3) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        final DashboardForthItemAdapter itemAdapter = new DashboardForthItemAdapter(mContext, s3.items, -1);
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

    public void displayFifthItem(RecyclerView.ViewHolder viewHolder, final S1 s4) {
        SecondViewHolder secondViewHolder = (SecondViewHolder) viewHolder;
        final DashboardFifthItemAdapter itemAdapter = new DashboardFifthItemAdapter(mContext, s4.items);
        ViewGroup.LayoutParams layoutParams = secondViewHolder.gridView.getLayoutParams();
        layoutParams.height = size.x*4/3; //this is in pixels
        secondViewHolder.gridView.setLayoutParams(layoutParams);
        secondViewHolder.gridView.setAdapter(itemAdapter);
        secondViewHolder.gridView.setColumnWidth(size.x/3-9);
        secondViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListener.onSecondItemClick(s4.items.get(position).formName, position, "",  s4.items.get(position).mainAPI);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 0: // today
                ((TodayViewHolder)holder).today.setText(DIHelper.getOnlyDateFromDateTime(mDashboardModel.todayDate));
                return;
            case 1: // S1 - Matter
                displaySecondItem(holder, mDashboardModel.s1);
                return;
            case 2: // S2 - Collection
                displayThirdItemHeader(holder, mDashboardModel.s2);
                return;
            default: // S4 - More
                break;
        }

        position = position - 3 ;
        if (position < mDashboardModel.s2.items.size()) {
            displayThirdItemContent(holder, mDashboardModel.s2.items.get(position), position);
            return;
        }

        position = position - mDashboardModel.s2.items.size();
        if (position == 0) {
            displayForthItemHeader(holder, mDashboardModel.s3);
            return;
        } else if (position == 1) {
            displayForthItem(holder, mDashboardModel.s3);
            return;
        }

        displayFifthItem(holder, mDashboardModel.s4);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 3+mDashboardModel.s2.items.size()+2+1;
    }
}

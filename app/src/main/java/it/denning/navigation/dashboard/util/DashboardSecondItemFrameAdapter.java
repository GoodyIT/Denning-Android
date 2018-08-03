package it.denning.navigation.dashboard.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matrixxun.starry.badgetextview.MaterialBadgeTextView;

import java.util.ArrayList;

import it.denning.R;
import it.denning.model.FirstItemModel;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class DashboardSecondItemFrameAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<FirstItemModel> models;
    Point size;

    public DashboardSecondItemFrameAdapter(Context mContext, ArrayList<FirstItemModel> models) {
        this.mContext = mContext;
        this.models = models;
        Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FirstItemModel firstItemModel = models.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.dashboard_second_item_frame, null);
        }
        final TextView titleTextView = (TextView)convertView.findViewById(R.id.title_textview);
        final TextView valueTextView = (TextView)convertView.findViewById(R.id.value_textview);
        final TextView tomorrowTextView = (TextView)convertView.findViewById(R.id.tomorrow_textview);
        final MaterialBadgeTextView footerValueTextView = (MaterialBadgeTextView)convertView.findViewById(R.id.footervalue_textview);
        final FrameLayout frameLayout = (FrameLayout)convertView.findViewById(R.id.second_item_layout);
        final ViewHolder viewHolder = new ViewHolder(titleTextView, valueTextView, tomorrowTextView, footerValueTextView, frameLayout);

     //   viewHolder.relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size.x/3));
        viewHolder.title.setText(firstItemModel.title);
        viewHolder.title.setTextColor(Color.parseColor(firstItemModel.titleColor));
        viewHolder.value.setText(firstItemModel.mainValue);
        viewHolder.value.setTextColor(Color.parseColor(firstItemModel.mainValueColor));
        viewHolder.frameLayout.setBackgroundColor(Color.parseColor(firstItemModel.background));
        ViewGroup.LayoutParams layoutParams = viewHolder.frameLayout.getLayoutParams();
        layoutParams.height = size.x/3;
        layoutParams.width = size.x/3-9;
        viewHolder.frameLayout.setLayoutParams(layoutParams);

        if (firstItemModel.footerValue.trim().length() == 0) {
            viewHolder.tomorrow.setVisibility(View.INVISIBLE);
            viewHolder.footerDescription.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tomorrow.setVisibility(View.VISIBLE);
            viewHolder.footerDescription.setVisibility(View.VISIBLE);
            viewHolder.footerDescription.setText(firstItemModel.footerDescription);
            viewHolder.footerDescription.setTextColor(Color.parseColor(firstItemModel.footerDescriptionColor));
        }

        return convertView;
    }

    private class ViewHolder {
        private final TextView title;
        private final TextView value;
        private final TextView tomorrow;
        private final MaterialBadgeTextView footerDescription;
        private final FrameLayout frameLayout;

        private ViewHolder(TextView title, TextView value, TextView tomorrow, MaterialBadgeTextView footerDescription, FrameLayout frameLayout) {
            this.title = title;
            this.value = value;
            this.tomorrow = tomorrow;
            this.footerDescription = footerDescription;
            this.frameLayout = frameLayout;
        }
    }
}

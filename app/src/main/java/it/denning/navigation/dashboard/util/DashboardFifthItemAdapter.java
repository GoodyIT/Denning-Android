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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.denning.R;
import it.denning.model.FirstItemModel;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class DashboardFifthItemAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<FirstItemModel> models;
    Point size;

    public DashboardFifthItemAdapter(Context mContext, ArrayList<FirstItemModel> models) {
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
            convertView = layoutInflater.inflate(R.layout.dashboard_fifth_item, null);
            final TextView titleTextView = (TextView) convertView.findViewById(R.id.title_textview);
            final ImageView itemImage = (ImageView) convertView.findViewById(R.id.item_imageview);
            final RelativeLayout relativeLayout = (RelativeLayout)convertView.findViewById(R.id.fifth_item_layout);
            final ViewHolder viewHolder = new ViewHolder(titleTextView, itemImage, relativeLayout);
            convertView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.title.setText(firstItemModel.title);
        int resId = mContext.getResources().getIdentifier(firstItemModel.icon, "drawable", mContext.getPackageName());
        viewHolder.itemImage.setImageResource(resId);
        viewHolder.relativeLayout.setBackgroundColor(Color.parseColor(firstItemModel.background));
        ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
        layoutParams.height = size.x/3;
        viewHolder.relativeLayout.setLayoutParams(layoutParams);
        viewHolder.relativeLayout.setBackgroundColor(Color.parseColor(firstItemModel.background));
        return convertView;
    }

    private class ViewHolder {
        private final TextView title;
        private final ImageView itemImage;
        private final RelativeLayout relativeLayout;

        private ViewHolder(TextView title, ImageView itemImage, RelativeLayout relativeLayout) {
            this.title = title;
            this.itemImage = itemImage;
            this.relativeLayout = relativeLayout;
        }
    }
}

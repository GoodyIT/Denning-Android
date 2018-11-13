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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matrixxun.starry.badgetextview.MaterialBadgeTextView;

import java.util.ArrayList;
import java.util.List;

import it.denning.App;
import it.denning.R;
import it.denning.model.ThirdItemModel;

/**
 * Created by hothongmee on 07/09/2017.
 */

public class DashboardForthItemAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<ThirdItemModel> models;
    public int curPosition;
    Point size;

    public DashboardForthItemAdapter(Context mContext, List<ThirdItemModel> models, int curPosition) {
        this.mContext = mContext;
        this.models = models;
        this.curPosition = curPosition;
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
        final ThirdItemModel thirdItemModel = models.get(position);
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.dashboard_forth_item, null);
            final TextView titleTextView = (TextView)convertView.findViewById(R.id.title_textview);
            final MaterialBadgeTextView valueTextView = (MaterialBadgeTextView)convertView.findViewById(R.id.value_textview);
            final RelativeLayout relativeLayout = (RelativeLayout)convertView.findViewById(R.id.forth_item_layout);
            final ViewHolder viewHolder = new ViewHolder(titleTextView, valueTextView, relativeLayout);
            convertView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.title.setText(thirdItemModel.label);
        viewHolder.value.setText(thirdItemModel.value);

        if (position == curPosition) {
            viewHolder.title.setTextColor(App.getInstance().getResources().getColor(R.color.colorAccent));
        } else {
            viewHolder.title.setTextColor(Color.GRAY);
        }

        ViewGroup.LayoutParams layoutParams = viewHolder.relativeLayout.getLayoutParams();
        layoutParams.height = size.x/4 > 200 ? size.x/4 : 200;
        viewHolder.relativeLayout.setLayoutParams(layoutParams);

        return convertView;
    }

    private class ViewHolder {
        private final TextView title;
        private final MaterialBadgeTextView value;
        private final RelativeLayout relativeLayout;

        private ViewHolder(TextView title, MaterialBadgeTextView value, RelativeLayout relativeLayout) {
            this.title = title;
            this.value = value;
            this.relativeLayout = relativeLayout;
        }
    }
}

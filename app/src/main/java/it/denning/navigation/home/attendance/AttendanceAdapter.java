package it.denning.navigation.home.attendance;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.model.AttendanceItem;

/**
 * Created by denningit on 2017-12-22.
 */

public class AttendanceAdapter extends  RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private final List<AttendanceItem> modelList;

    public AttendanceAdapter(List<AttendanceItem> modelList) {
        this.modelList = modelList;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_attendance, parent, false);

        return new AttendanceViewHolder(itemView);
    }

    private void changeColor(AttendanceViewHolder holder, int color, int image) {
        holder.statusImageView.setImageDrawable(App.getInstance().getResources().getDrawable(image));
        holder.statusTextView.setTextColor(App.getInstance().getResources().getColor(color));
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        AttendanceItem item = modelList.get(position);
        switch (item.theType) {
            case "Clock-in":
                changeColor(holder, R.color.baby_blue, R.drawable.blue_circle);
                break;

            case "Clock-out":
                changeColor(holder, R.color.colorAccent, R.drawable.red_circle);
                break;

            default:
                changeColor(holder, R.color.md_yellow_500, R.drawable.yellow_circle);
                break;
        }

        holder.statusTextView.setText(item.theType);
        holder.locationTextView.setText(item.theLocation);
        holder.timeTextView.setText(item.theTime);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.status_imageview)
        ImageView statusImageView;

        @BindView(R.id.status_textview)
        TextView statusTextView;

        @BindView(R.id.location_textview)
        TextView locationTextView;

        @BindView(R.id.time_textview)
        TextView timeTextView;

        public AttendanceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addItems(List<AttendanceItem> items) {
        modelList.clear();
        modelList.addAll(items);
        notifyDataSetChanged();
    }
}

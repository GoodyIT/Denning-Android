package it.denning.navigation.home.calendar;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.Event;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2017-12-18.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.EventViewHolder> {
    private final List<Event> modelList;
    private final OnItemClickListener itemClickListener;

    public CalendarAdapter(List<Event> modelList, OnItemClickListener itemClickListener) {
        this.modelList = modelList;
        this.itemClickListener = itemClickListener;
    }

    public List<Event> getModels() {
        return modelList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.case_name)
        TextView caseName;
        protected @BindView(R.id.case_no) TextView caseNo;
        protected @BindView(R.id.start_date) TextView startDate;
        protected @BindView(R.id.counsel) TextView counsel;
        protected @BindView(R.id.location) TextView location;
        @BindView(R.id.start_day) TextView startDay;
        @BindView(R.id.start_time) TextView startTime;
        @BindView(R.id.left_bar)
        LinearLayout leftbar;
        @BindView(R.id.calendar_cardview)
        CardView cardView;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_event, parent, false);

        return new EventViewHolder(itemView);
    }

    private void changeColor(EventViewHolder holder, int color) {
        int _color = App.getInstance().getResources().getColor(color);
        holder.leftbar.setBackgroundColor(_color);
        holder.startDate.setTextColor(_color);
        holder.startDay.setTextColor(_color);
        holder.startTime.setTextColor(_color);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, final int position) {
        Event event = modelList.get(position);
        holder.caseName.setText(event.caseName);
        holder.caseNo.setText(event.caseNo);
        holder.location.setText(event.location);
        holder.counsel.setText(event.counsel);
        holder.startDate.setText(DIHelper.getOnlyDateFromDateTime(event.eventStart));
        holder.startDay.setText(DIHelper.getDay(event.eventStart));
        holder.startTime.setText(DIHelper.getTime(event.eventStart));

        switch (event.eventType) {
            case "1court":
                changeColor(holder, R.color.colorAccent);
                break;

            case "2office":
                changeColor(holder, R.color.baby_blue);
                break;

            case "3personal":
                changeColor(holder, R.color.md_deep_orange_300);
                break;
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void updateAdapter(List<Event> events) {
        this.modelList.addAll(events);
        notifyDataSetChanged();
    }

    public void clear() {
        this.modelList.clear();
        notifyDataSetChanged();
    }
}

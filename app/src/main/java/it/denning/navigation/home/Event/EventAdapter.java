package it.denning.navigation.home.Event;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.Event;
import it.denning.navigation.home.news.NewsAdapter;

/**
 * Created by denningit on 18/04/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    List<Event> eventList;

    EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_event, parent, false);

        return new EventAdapter.EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected @BindView(R.id.case_name)
        TextView caseName;
        protected @BindView(R.id.case_no) TextView caseNo;
        protected @BindView(R.id.start_date) TextView startDate;
        protected @BindView(R.id.counsel) TextView counsel;
        protected @BindView(R.id.location) TextView location;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

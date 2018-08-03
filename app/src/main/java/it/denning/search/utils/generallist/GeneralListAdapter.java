package it.denning.search.utils.generallist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.search.utils.OnGeneralClickListener;

/**
 * Created by denningit on 2017-12-27.
 */

public class GeneralListAdapter extends RecyclerView.Adapter<GeneralListAdapter.GeneralListViewHolder>{
    private List<JsonObject> modelList;
    private OnGeneralClickListener onGeneralClickListener;
    private String value;

    public GeneralListAdapter(List<JsonObject> modelList, OnGeneralClickListener onGeneralClickListener, String value) {
        this.onGeneralClickListener = onGeneralClickListener;
        this.modelList = modelList;
        this.value = value;
    }

    @Override
    public GeneralListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_search_one_label, parent, false);
        return new GeneralListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GeneralListViewHolder holder, int position) {
        final JsonObject model = modelList.get(position);
        holder.description.setText(model.get(value).getAsString());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGeneralClickListener.onClick(view, model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class GeneralListViewHolder extends  RecyclerView.ViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_general_description)
        TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;

        public GeneralListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addItems(List<JsonObject> items) {
        modelList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }
}

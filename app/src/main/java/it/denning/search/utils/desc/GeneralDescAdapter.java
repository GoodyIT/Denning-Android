package it.denning.search.utils.desc;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.CodeDescription;
import it.denning.search.utils.OnClickListenerForCodeDesc;
import it.denning.search.utils.OnClickListenerWithCode;
import it.denning.search.utils.codedesc.CodeDescAdapter;

/**
 * Created by denningit on 2017-12-27.
 */

public class GeneralDescAdapter extends RecyclerView.Adapter<GeneralDescAdapter.GeneralDescViewHolder>{

    private List<String> modelList;
    private OnClickListenerWithCode onItemClickListener;

    public GeneralDescAdapter(List<String> modelList, OnClickListenerWithCode onItemClickListener) {
        this.modelList = modelList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public GeneralDescViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_search_one_label, parent, false);

        return new GeneralDescViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GeneralDescViewHolder holder, int position) {
        final String str = modelList.get(position);
        holder.description.setText(str);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(view, str);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class GeneralDescViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_general_description)
        TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;

        public GeneralDescViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addItems(List<String> items) {
        modelList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }
}

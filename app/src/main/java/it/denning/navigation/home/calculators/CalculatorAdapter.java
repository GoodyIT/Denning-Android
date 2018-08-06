package it.denning.navigation.home.calculators;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.navigation.home.news.NewsAdapter;
import it.denning.search.utils.OnItemClickListener;

public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.DetailViewHolder> {
    private final List<String> calcTypes;
    private final OnItemClickListener itemClickListener;

    public CalculatorAdapter(List<String> calcTypes, OnItemClickListener itemClickListener) {
        this.calcTypes = calcTypes;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_simple_detail, parent, false);

        return new DetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, final int position) {
        holder.name.setText(calcTypes.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return calcTypes.size();
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.detail_name)
        TextView name;
        @BindView(R.id.cardview)
        CardView cardView;

        public DetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

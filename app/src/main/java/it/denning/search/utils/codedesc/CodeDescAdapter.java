package it.denning.search.utils.codedesc;

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
import it.denning.search.matter.MatterAdapter;
import it.denning.search.utils.OnClickListenerForCodeDesc;
import it.denning.search.utils.OnClickListenerWithCode;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2017-12-27.
 */

public class CodeDescAdapter  extends RecyclerView.Adapter<CodeDescAdapter.CodeDescViewHolder>{
    private List<CodeDescription> modelList;
    private OnClickListenerForCodeDesc onItemClickListener;

    public CodeDescAdapter(List<CodeDescription> modelList, OnClickListenerForCodeDesc onItemClickListener) {
        this.modelList = modelList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public CodeDescViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_search_one_label, parent, false);

        return new CodeDescViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CodeDescViewHolder holder, int position) {
        final CodeDescription model = modelList.get(position);
        holder.description.setText(model.description);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(view, model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class CodeDescViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_general_description)
        TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;

        public CodeDescViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addItems(List<CodeDescription> items) {
        modelList.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }
}

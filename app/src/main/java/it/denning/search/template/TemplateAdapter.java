package it.denning.search.template;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.expanx.Util.parent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.Template;
import it.denning.search.MatterCode.MatterCodeAdapter;
import it.denning.search.paymentrecord.PaymentRecordAdapter;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2017-12-24.
 */

public class TemplateAdapter  extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>{
    private List<Template> modelList;
    private OnItemClickListener clickListener;

    public TemplateAdapter(List<Template> modelList, OnItemClickListener clickListener) {
        this.modelList = modelList;
        this.clickListener = clickListener;
    }

    public List<Template> getModel() {
        return modelList;
    }

    @Override
    public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_template, parent, false);

        return new TemplateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TemplateViewHolder holder, final int position) {
        Template template = modelList.get(position);
        holder.title.setText(template.strDescription);
        holder.version.setText(template.intVersionID);
        holder.date.setText("(" + DIHelper.getOnlyDateFromDateTime(template.dtCreatedDate) + ")");
        holder.type.setText(template.strSource);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v, position);
            }
        });
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }

    public void swapItems(List<Template> list) {
        modelList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.title_textview)
        TextView title;
        @BindView(R.id.version_textview) TextView version;
        @BindView(R.id.date_textview) TextView date;
        @BindView(R.id.type_textiew) TextView type;
        @BindView(R.id.cardview)
        CardView cardView;
        public TemplateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

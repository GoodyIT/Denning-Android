package it.denning.search.template;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import it.denning.model.Template;

/**
 * Created by denningit on 2017-12-24.
 */

public class TemplateAdapter  extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>{
    private List<Template> modelList;

    public TemplateAdapter(List<Template> modelList) {
        this.modelList = modelList;
    }

    @Override
    public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TemplateViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder
    {

        public TemplateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

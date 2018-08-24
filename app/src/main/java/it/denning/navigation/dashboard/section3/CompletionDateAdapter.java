package it.denning.navigation.dashboard.section3;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.model.CompletionTrackingModel;
import it.denning.model.CompletionTrackingModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-01-16.
 */

public class CompletionDateAdapter extends SectioningAdapter {
    private OnItemClickListener itemClickListener;
    final private List<CompletionTrackingModel> models;

    public CompletionDateAdapter(List<CompletionTrackingModel> models) {

        this.models = models;
    }
    
    public List<CompletionTrackingModel> getModels() {
        return models;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.file_no_textview)
        TextView fileNo;
        @BindView(R.id.file_name_textview)
        TextView fileName;
        @BindView(R.id.completion_date_textview)
        TextView dateTextView;
        @BindView(R.id.extended_textview)
        TextView extendedTextView;
        @BindView(R.id.daystocomplete_textview)
        TextView daysToComplete;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return 1;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return models.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return false;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_completion_date, parent, false);
        return new ItemViewHolder(v);
    }


    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        CompletionTrackingModel model = models.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        itemViewHolder.fileNo.setText(model.fileNo);
        itemViewHolder.fileName.setText(model.fileName);
        itemViewHolder.dateTextView.setText(DIHelper.getOnlyDateFromDateTime(model.completionDate));
        itemViewHolder.extendedTextView.setText(DIHelper.getOnlyDateFromDateTime(model.extendedDate));
        itemViewHolder.daysToComplete.setText(model.dayToComplete);
        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, itemIndex);
            }
        });
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    public void clear() {
        this.models.clear();
        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<CompletionTrackingModel> newmodels) {
        this.models.addAll(newmodels);
        notifyAllSectionsDataSetChanged();
    }
}

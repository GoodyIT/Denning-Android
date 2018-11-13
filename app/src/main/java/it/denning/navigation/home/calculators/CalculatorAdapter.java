package it.denning.navigation.home.calculators;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.CalcModel;
import it.denning.model.LabelIconOpenFormModel;
import it.denning.model.NameCode;
import it.denning.search.utils.OnItemClickListener;
import it.denning.search.utils.OnSectionItemClickListener;
import org.zakariya.stickyheaders.SectioningAdapter;

public class CalculatorAdapter extends SectioningAdapter {
    private final CalcModel calcModel = new CalcModel();
    private final OnSectionItemClickListener itemClickListener;

    public CalculatorAdapter(OnSectionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.first_textview)
        TextView firstTitle;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GeneralTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.detail_name)
        TextView name;
        @BindView(R.id.cardview)
        CardView cardView;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return calcModel.items.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return calcModel.items.get(sectionIndex).items.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_simple_detail, parent, false);

        return new GeneralTypeViewHolder(itemView);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayGeneral((GeneralTypeViewHolder)viewHolder, sectionIndex, itemIndex);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.firstTitle.setText(calcModel.items.get(sectionIndex).title);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    private void displayGeneral(GeneralTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final NameCode model = calcModel.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.name.setText(model.name);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, model.code);
            }
        });
    }
}

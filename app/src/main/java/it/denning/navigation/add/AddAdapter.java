package it.denning.navigation.add;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.AddModel;
import it.denning.model.AddSectionModel;
import it.denning.model.LabelIconModel;
import it.denning.search.utils.OnItemClickListener;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 2018-01-15.
 */

public class AddAdapter extends SectioningAdapter {
    final private OnSectionItemClickListener itemClickListener;
    private AddModel addModel;

    public AddAdapter(OnSectionItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        addModel = AddModel.build();
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
        @BindView(R.id.add_name)
        TextView label;
        @BindView(R.id.add_icon)
        ImageView imageButton;
        @BindView(R.id.add_cardview)
        CardView cardView;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {

        return addModel.items.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return addModel.items.get(sectionIndex).items.size();
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
                inflate(R.layout.cardview_add_main, parent, false);

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
        headerViewHolder.firstTitle.setText("");
    }

    private void displayGeneral(GeneralTypeViewHolder viewHolder, final int sectionIndex, final int itemIndex) {
        final LabelIconModel model = addModel.items.get(sectionIndex).items.get(itemIndex);
        viewHolder.label.setText(model.label);
        viewHolder.imageButton.setImageResource(model.image);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, sectionIndex, itemIndex, model.label);
            }
        });
    }
}

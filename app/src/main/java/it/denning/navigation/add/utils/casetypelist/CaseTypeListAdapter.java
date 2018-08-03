package it.denning.navigation.add.utils.casetypelist;

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
import it.denning.model.CaseType;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-01-16.
 */

public class CaseTypeListAdapter extends SectioningAdapter {
    private OnItemClickListener itemClickListener;
    final private List<CaseType> models;

    public CaseTypeListAdapter(List<CaseType> models) {

        this.models = models;
    }
    
    public List<CaseType> getModels() {
        return models;
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.name_textview)
        TextView contactName;
        @BindView(R.id.id_textView)
        TextView contactID;
        @BindView(R.id.dashboard_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.name_textview)
        TextView nameTitle;
        @BindView(R.id.id_textView)
        TextView idTitle;

        public HeaderViewHolder(View itemView) {
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
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_contactlisting, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_contact_listing_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        CaseType model = models.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        itemViewHolder.contactName.setText(model.strBahasa);
        itemViewHolder.contactID.setText(model.strEnglish);
        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, itemIndex);
            }
        });
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder)viewHolder;
        headerViewHolder.nameTitle.setText("Bahasa");
        headerViewHolder.idTitle.setText("English");
    }

    public void clear() {
        this.models.clear();
        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<CaseType> newmodels) {
        this.models.addAll(newmodels);
        notifyAllSectionsDataSetChanged();
    }
}

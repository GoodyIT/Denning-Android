package it.denning.navigation.add.utils.accounttype;

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
import it.denning.model.AccountType;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-01-16.
 */

public class AccountTypeAdapter extends SectioningAdapter {
    private OnItemClickListener itemClickListener;
    final private List<AccountType> models;

    public AccountTypeAdapter(List<AccountType> models) {

        this.models = models;
    }
    
    public List<AccountType> getModels() {
        return models;
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.search_general_description)
        TextView description;
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
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_one_label, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        AccountType _model = models.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        itemViewHolder.description.setText(_model.code);
        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, itemIndex);
            }
        });
    }

    public void clear() {
        this.models.clear();
        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<AccountType> newmatterCodes) {
        this.models.addAll(newmatterCodes);
        notifyAllSectionsDataSetChanged();
    }
}

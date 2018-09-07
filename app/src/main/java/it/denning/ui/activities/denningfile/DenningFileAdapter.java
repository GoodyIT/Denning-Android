package it.denning.ui.activities.denningfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.SearchResultModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by com on 12/4/2017.
 */

public class DenningFileAdapter extends SectioningAdapter {
    private List<SearchResultModel> modelArrayList, filteredArrayList = new ArrayList<>();
    private final Context mContext;
    private OnItemClickListener clickListener;
    public int tab = 0;

    public DenningFileAdapter(List<SearchResultModel> modelArrayList, Context mContext) {
        this.modelArrayList = modelArrayList;
        this.mContext = mContext;
    }

    public List<SearchResultModel> getModelArrayList() {
        return filteredArrayList;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class GeneralTypeViewHolder extends ItemViewHolder{

        @BindView(R.id.search_cardview)
        CardView cardView;
        @BindView(R.id.search_general_name)
        TextView title;
        @BindView(R.id.search_general_description) TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightBtn;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rightBtn.setVisibility(View.INVISIBLE);
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

    @Override
    public int getNumberOfSections() {
        return filteredArrayList.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return 1;
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
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        return DIHelper.determinSearchType(filteredArrayList.get(sectionIndex).form);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_general, parent, false);
        return new GeneralTypeViewHolder(view);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        SearchResultModel searchResultModel = this.filteredArrayList.get(sectionIndex);
        displayGeneralSearchResult(viewHolder, searchResultModel, sectionIndex);
    }

    private void displayGeneralSearchResult(ItemViewHolder viewHolder, final SearchResultModel searchResultModel, final int sectionIndex) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) viewHolder;
        generalTypeViewHolder.title.setText(searchResultModel.title);
        generalTypeViewHolder.description.setText(searchResultModel.description);

        // implement button click
        generalTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, sectionIndex);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        SearchResultModel searchResultModel = this.filteredArrayList.get(sectionIndex);
        Integer type = DIHelper.determinSearchType(searchResultModel.form);
        String headerName = "";
        switch (type) {
            case DIConstants.CONTACT_TYPE:
                headerName = "Contact";
                break;
            case DIConstants.MATTER_TYPE:
                headerName = "Matter";
                break;

            default:
                headerName = "";
                break;
        }
        headerViewHolder.firstTitle.setText(headerName);
    }

    public void filterList() {
        if (tab == 0) { // All
            filteredArrayList.addAll(modelArrayList);
        } else {
            List<SearchResultModel> newList = new ArrayList<>();
            for (SearchResultModel model : modelArrayList) {
                Integer type = DIHelper.determinSearchType(model.form);
                if (tab == 1 && type == DIConstants.MATTER_TYPE) { // File
                    newList.add(model);
                } else if (tab == 2 && type == DIConstants.CONTACT_TYPE){ // Contact
                    newList.add(model);
                }
            }
            filteredArrayList = newList;
        }

        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<SearchResultModel> newSearchResultList) {
        this.modelArrayList.addAll(newSearchResultList);
        filterList();
    }

    public void clear() {
        this.modelArrayList.clear();
        this.filteredArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }
}

package it.denning.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.FirmModel;
import it.denning.model.ParentModel;
import it.denning.model.ParentModel;
import it.denning.model.SearchResultModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by com on 12/4/2017.
 */

public class DenningShareAdapter extends SectioningAdapter {
    private List<ParentModel> searchModelList, filteredArrayList = new ArrayList<>();
    private final Context mContext;
    private OnItemClickListener clickListener;
    public int tab = 0;
    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;

    public DenningShareAdapter(List<ParentModel> searchModelList, Context mContext) {
        this.searchModelList = searchModelList;
        this.mContext = mContext;
    }

    public List<ParentModel> getModelArrayList() {
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
        @BindView(R.id.branch_check)
        CheckBox checkBox;

        public GeneralTypeViewHolder(View itemView) {
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
        if (DISharedPreferences.getInstance().isStaff()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

//    @Override
//    public int getSectionItemUserType(int sectionIndex, int itemIndex)
//    {
//        if (DISharedPreferences.getInstance().isStaff()) {
//            return DIHelper.determinSearchType(((SearchResultModel)filteredArrayList.get(sectionIndex)).form);
//        } else {
//            return DIConstants.SHARE_BRANCH_TYPE;
//        }
//    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_search_general_checkbox, parent, false);
        return new GeneralTypeViewHolder(view);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        ParentModel model = this.filteredArrayList.get(sectionIndex);
        if (DISharedPreferences.getInstance().isStaff()) {
            displayGeneralSearchResult(viewHolder, (SearchResultModel) model, sectionIndex);
        } else {
            displayFirmBranch(viewHolder, (FirmModel) model, sectionIndex);
        }
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    private void displayGeneralSearchResult(ItemViewHolder viewHolder, final SearchResultModel searchResultModel, final int sectionIndex) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) viewHolder;
        generalTypeViewHolder.title.setText(searchResultModel.title);
        generalTypeViewHolder.description.setText(searchResultModel.description);
        generalTypeViewHolder.checkBox.setTag(sectionIndex);

        // implement button click
//        generalTypeViewHolder.cardView.setOnClickListener(new MyCheckClickListener());
        generalTypeViewHolder.checkBox.setOnClickListener(new MyCheckClickListener());
    }

    private void displayFirmBranch(ItemViewHolder viewHolder, final FirmModel searchResultModel, final int sectionIndex) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) viewHolder;
        generalTypeViewHolder.title.setText(searchResultModel.LawFirm.name);
        generalTypeViewHolder.description.setText(searchResultModel.LawFirm.address.city);
        generalTypeViewHolder.checkBox.setTag(sectionIndex);

        // implement button click
//        generalTypeViewHolder.cardView.setOnClickListener(new MyCheckClickListener());
        generalTypeViewHolder.checkBox.setOnClickListener(new MyCheckClickListener());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        SearchResultModel searchResultModel = (SearchResultModel)this.filteredArrayList.get(sectionIndex);
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

    public void filterSearchList() {
        if (tab == 3) {
            filteredArrayList = new ArrayList<>();
        } else if (tab == 0) { // All
            filteredArrayList.addAll(searchModelList);
        } else  {
            List<ParentModel> newList = new ArrayList<>();
            for (int i = 0; i < searchModelList.size(); i++) {
                SearchResultModel model = (SearchResultModel)searchModelList.get(i);
                Integer type = DIHelper.determinSearchType(model.form);
                if (tab == 1 && type == DIConstants.MATTER_TYPE) { // File
                    newList.add(model);
                } else if (type == DIConstants.CONTACT_TYPE){ // Contact
                    newList.add(model);
                }
            }
            filteredArrayList = newList;
        }

        notifyAllSectionsDataSetChanged();
    }

    public void filterBranchList(String query) {
        if (query.trim().length() == 0) {
            filteredArrayList.addAll(searchModelList);
        } else {
            query = query.toLowerCase();
            List<ParentModel> newList = new ArrayList<>();
            for (int i = 0; i < searchModelList.size(); i++) {
                FirmModel firmModel = (FirmModel)searchModelList.get(i);
                if (firmModel.LawFirm.name.toLowerCase().contains(query) || firmModel.LawFirm.address.city.contains(query)) {
                    newList.add(firmModel);
                }
            }

            filteredArrayList = new ArrayList<>(newList);
        }

        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<ParentModel> newSearchResultList) {
        this.searchModelList.addAll(newSearchResultList);

        if (DISharedPreferences.getInstance().isStaff()) {
            filterSearchList();
        } else {
            filterBranchList("");
        }
    }

    public void clear() {
        this.searchModelList.clear();
        this.filteredArrayList.clear();
        notifyAllSectionsDataSetChanged();
    }

    protected class MyCheckClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox)v;
            int clickedPos = ((Integer)cb.getTag()).intValue();

            if(cb.isChecked())
            {
                if(lastChecked != null)
                {
                    lastChecked.setChecked(false);
                }

                lastChecked = cb;
                lastCheckedPos = clickedPos;
                clickListener.onClick(v, clickedPos);
            } else {
                lastChecked = null;
                clickListener.onClick(v, -1);
            }
        }
    }
}

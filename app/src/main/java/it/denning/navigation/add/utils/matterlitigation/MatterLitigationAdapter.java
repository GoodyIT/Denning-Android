package it.denning.navigation.add.utils.matterlitigation;

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
import it.denning.model.MatterLitigation;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 2018-01-16.
 */

public class MatterLitigationAdapter extends SectioningAdapter {
    private OnItemClickListener itemClickListener;
    final private List<MatterLitigation> matterCodes;

    public MatterLitigationAdapter(List<MatterLitigation> matterCodes) {

        this.matterCodes = matterCodes;
    }
    
    public List<MatterLitigation> getMatterCodes() {
        return matterCodes;
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.file_no_textview)
        TextView fileNo;
        @BindView(R.id.case_name_textview)
        TextView caseName;
        @BindView(R.id.case_no_textView)
        TextView caseNo;
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
        return matterCodes.size();
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
        View v = inflater.inflate(R.layout.cardview_matter_litigation, parent, false);
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
        MatterLitigation model = matterCodes.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;

        itemViewHolder.fileNo.setText(model.systemNo);
        itemViewHolder.caseNo.setText(model.getCaseNo());
        itemViewHolder.caseName.setText(model.getCaseName());
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
        headerViewHolder.nameTitle.setText("Code");
        headerViewHolder.idTitle.setText("Description");
    }

    public void clear() {
        this.matterCodes.clear();
        notifyAllSectionsDataSetChanged();
    }

    public void swapItems(List<MatterLitigation> newmatterCodes) {
        this.matterCodes.addAll(newmatterCodes);
        notifyAllSectionsDataSetChanged();
    }
}

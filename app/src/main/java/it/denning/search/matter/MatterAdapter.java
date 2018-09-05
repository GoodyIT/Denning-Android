package it.denning.search.matter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.BankGroup;
import it.denning.model.FormulaModel;
import it.denning.model.LabelValue;
import it.denning.model.MatterProperty;
import it.denning.model.PartyGroup;
import it.denning.model.MatterModel;
import it.denning.model.SolicitorGroup;
import it.denning.search.utils.OnAccountsClickListener;
import it.denning.search.utils.OnDetailItemClickListener;
import it.denning.search.utils.OnFileFolderClickListener;
import it.denning.search.utils.OnFileNoteClickListener;
import it.denning.search.utils.OnMatterCodeClickListener;
import it.denning.search.utils.OnPaymentRecordClickListener;

/**
 * Created by denningit on 27/04/2017.
 */

public class MatterAdapter extends SectioningAdapter {
    MatterModel mRelatedMatter;
    Context mContext;
    OnDetailItemClickListener itemClickListener;
    OnFileFolderClickListener fileFolderClickListener;
    OnAccountsClickListener accountsClickListener;
    OnFileNoteClickListener fileNoteClickListener;
    OnPaymentRecordClickListener paymentRecordClickListener;
    OnMatterCodeClickListener matterCodeClickListener;
    Map<Integer, Integer> typeSet;
    int numberOfFirstGeneralTypeHolders;

    MatterAdapter(MatterModel relatedMatter, Context context) {
        this.mRelatedMatter = relatedMatter;
        this.mContext = context;
        typeSet = new HashMap<>();
        this.numberOfFirstGeneralTypeHolders = 5;
    }

    public void setClickListeners(OnMatterCodeClickListener matterCodeClickListener, OnDetailItemClickListener itemClickListener, OnFileFolderClickListener fileFolderClickListener, OnAccountsClickListener accountsClickListener, OnFileNoteClickListener fileNoteClickListener, OnPaymentRecordClickListener paymentRecordClickListener) {
        this.itemClickListener = itemClickListener;
        this.accountsClickListener = accountsClickListener;
        this.fileFolderClickListener = fileFolderClickListener;
        this.fileNoteClickListener = fileNoteClickListener;
        this.paymentRecordClickListener = paymentRecordClickListener;
        this.matterCodeClickListener = matterCodeClickListener;
    }

    public class GeneralTypeViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.search_cardview)
        CardView searchView;
        @BindView(R.id.search_general_name)
        TextView label;
        @BindView(R.id.search_general_description) TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MatterLastTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_cardview) CardView cardView;
        @BindView(R.id.search_matter_accountsBtn)
        Button openAccoutsBtn;
        @BindView(R.id.search_matter_openFolderBtn) Button openFolderBtn;
        @BindView(R.id.search_matter_filenoteBtn) Button fileNoteBtn;
        @BindView(R.id.search_matter_paymentRecordBtn) Button paymentRecordBtn;

        public MatterLastTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class OneLabelTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_cardview)
        CardView searchView;
        @BindView(R.id.search_general_description) TextView description;
        @BindView(R.id.search_last_rightBtn)
        ImageButton rightButton;

        public OneLabelTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ThreeTypeViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.search_cardview)
        CardView searchView;
        @BindView(R.id.search_first) TextView titleView;
        @BindView(R.id.search_second) TextView firstValue;
        @BindView(R.id.search_third) TextView secondValue;
        @BindView(R.id.search_last_rightBtn)
        ImageView rightButton;

        public ThreeTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderTypeViewHolder  extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_header_name)
        TextView headerName;
        @BindView(R.id.search_header_IDNo) TextView headerIDNo;
        @BindView(R.id.search_header_oldID) TextView oldID;
        @BindView(R.id.search_header_chatBtn)
        ImageButton chatBtn;
        @BindView(R.id.search_header_editBtn)
        ImageButton editBtn;

        public HeaderTypeViewHolder(View itemView) {
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
        return 11;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 0;
        switch (sectionIndex) {
            case DIConstants.MATTER_HEADER: // Header
                count = 1;
                break;
            case DIConstants.MATTER_INFORMATION: // Main Information
                count = 7;
                break;
            case DIConstants.MATTER_COURT_INFORMATION: // Court
                count = 5;
                break;
            case DIConstants.MATTER_PARTIES_GROUP: // Parties Group
                for (PartyGroup partyGroup : mRelatedMatter.partyGroup) {
                    if (partyGroup.party != null) {
                        count += partyGroup.party.size();
                    }
                }
                break;
            case DIConstants.MATTER_SOLICITORS: // Solicitors
                count = mRelatedMatter.solicitorsGroup.size();
                break;
            case DIConstants.MATTER_PROPERTIES: // Properties
                count = mRelatedMatter.propertyGroup.size();
                break;
            case DIConstants.MATTER_BANKS: // Banks
                count = mRelatedMatter.bankGroup.size();
                break;
            case DIConstants.MATTER_RM_GROUP: // Important RM
                count = mRelatedMatter.RMGroup.size();
                break;
            case DIConstants.MATTER_DATE_GROUP: // Important Dates
                count = mRelatedMatter.dateGroup.size();
                break;
            case DIConstants.MATTER_TEXT_GROUP: // Other Information
                count = mRelatedMatter.textGroup.size();
                break;
            case DIConstants.MATTER_FILES_LEDGERS: // Files & Ledgers
                count = 1;
                break;

            default:
                    break;
        }

        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        if (sectionIndex == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        int type = 0;
        switch (sectionIndex) {
            case DIConstants.MATTER_HEADER:
                type = DIConstants.HEADER_TYPE;
                break;
            case DIConstants.MATTER_INFORMATION:
            case DIConstants.MATTER_COURT_INFORMATION:
            case DIConstants.MATTER_BANKS:
            case DIConstants.MATTER_RM_GROUP:
            case DIConstants.MATTER_DATE_GROUP:
            case DIConstants.MATTER_TEXT_GROUP:
                type = DIConstants.GENERAL_TYPE;
                break;
            case DIConstants.MATTER_PARTIES_GROUP:
                if (getPartyIndex(itemIndex) == 0) {
                    type = DIConstants.GENERAL_TYPE;
                } else {
                    type = DIConstants.ONE_TYPE;
                }
                break;
            case DIConstants.MATTER_SOLICITORS:
            case DIConstants.MATTER_PROPERTIES:
                type = DIConstants.THREE_TYPE;
                break;

            case DIConstants.MATTER_FILES_LEDGERS:
                type = DIConstants.LAST_TYPE;
            default:
                break;
        }

        return type;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        switch (itemType) {
            case DIConstants.HEADER_TYPE:
                View itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_header, parent, false);

                itemViewHolder =  new HeaderTypeViewHolder(itemView);
                break;
            case DIConstants.GENERAL_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_general, parent, false);

                itemViewHolder =   new GeneralTypeViewHolder(itemView);
                break;

            case DIConstants.ONE_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_one_label, parent, false);

                itemViewHolder =  new OneLabelTypeViewHolder(itemView);
                break;
            case DIConstants.THREE_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_threefield, parent, false);

                itemViewHolder =  new ThreeTypeViewHolder(itemView);
                break;
            case DIConstants.LAST_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_matter_last, parent, false);

                itemViewHolder =   new MatterLastTypeViewHolder(itemView);
                break;
            default:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_general, parent, false);

                itemViewHolder =  new GeneralTypeViewHolder(itemView);
                break;
        }

        return itemViewHolder;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_search_section_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        String headerName = "";
        switch (sectionIndex) {
            case DIConstants.MATTER_INFORMATION:
                headerName = "Main Information";
                break;

            case DIConstants.MATTER_COURT_INFORMATION:
                headerName = "Court Case Information";
                break;

            case DIConstants.MATTER_PARTIES_GROUP:
                headerName = "Parties Group";
                break;

            case DIConstants.MATTER_SOLICITORS:
                headerName = "Solicitors";
                break;

            case DIConstants.MATTER_PROPERTIES:
                headerName = "Properties";
                break;

            case DIConstants.MATTER_BANKS:
                headerName = "Banks";
                break;

            case DIConstants.MATTER_RM_GROUP:
                headerName = "Important RM";
                break;

            case DIConstants.MATTER_DATE_GROUP:
                headerName = "Important Dates";
                break;

            case DIConstants.MATTER_TEXT_GROUP:
                headerName = "Other Information";
                break;

            case DIConstants.MATTER_FILES_LEDGERS:
                headerName = "Files & Ledgers";
                break;

            default:
                break;
        }

        headerViewHolder.firstTitle.setText(headerName);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        displayItems(viewHolder, sectionIndex, itemIndex, itemType);
    }

    void displayItems(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        switch (sectionIndex) {
            case DIConstants.MATTER_HEADER:
                displayHeader(viewHolder, itemIndex);
                break;
            case DIConstants.MATTER_INFORMATION:
                displayMatterInformation(viewHolder, itemIndex);
                break;

            case DIConstants.MATTER_COURT_INFORMATION:
                displayCourt(viewHolder, itemIndex);
                break;

            case DIConstants.MATTER_PARTIES_GROUP:
                displayPartiesGroup(viewHolder, itemIndex);
                break;

            case DIConstants.MATTER_BANKS:
                BankGroup bankGroup = mRelatedMatter.bankGroup.get(itemIndex);
                String bankName = bankGroup != null ? bankGroup.getBankName(): "";
                String bankCode = bankGroup != null ? bankGroup.getBankCode(): "";
                setupGeneralTypeHoler((GeneralTypeViewHolder) viewHolder, bankGroup != null ? bankGroup.groupName : "", bankName, View.VISIBLE);
                setupOnclickForCardview((GeneralTypeViewHolder) viewHolder, "bank", bankCode);
                break;

            case DIConstants.MATTER_RM_GROUP:
                FormulaModel RMGroup = mRelatedMatter.RMGroup.get(itemIndex);
                setupGeneralTypeHoler((GeneralTypeViewHolder) viewHolder, RMGroup != null ?  RMGroup.label:"", RMGroup != null ? RMGroup.value:"", View.INVISIBLE);
                break;

            case DIConstants.MATTER_DATE_GROUP:
                FormulaModel dateGroup = mRelatedMatter.dateGroup.get(itemIndex);
                setupGeneralTypeHoler((GeneralTypeViewHolder) viewHolder, dateGroup != null ? dateGroup.label:"", dateGroup != null ? DIHelper.convertToSimpleDateFormat(dateGroup.value):"", View.INVISIBLE);
                break;

            case DIConstants.MATTER_TEXT_GROUP:
                LabelValue textGroup = mRelatedMatter.textGroup.get(itemIndex);
                setupGeneralTypeHoler((GeneralTypeViewHolder) viewHolder, textGroup != null ? textGroup.label:"", textGroup != null ? textGroup.value:"", View.INVISIBLE);

            case DIConstants.MATTER_SOLICITORS:
                SolicitorGroup solicitorGroup = mRelatedMatter.solicitorsGroup.get(itemIndex);
                String solicitorCode =  solicitorGroup != null ? solicitorGroup.getCode(): "";
                setupThreeTypeHolder(viewHolder, solicitorGroup != null ? solicitorGroup.groupName:"", solicitorGroup != null ? solicitorGroup.getName():"", solicitorGroup != null ? solicitorGroup.reference:"", View.VISIBLE);
                setupOnclickForCardview( viewHolder, "solicitor", solicitorCode);
                break;

            case DIConstants.MATTER_PROPERTIES:
                MatterProperty matterProperty = mRelatedMatter.propertyGroup.get(itemIndex);
                String name = "Property " + (itemIndex+1);
                String propertyCode = matterProperty != null ? matterProperty.code:"";
                setupThreeTypeHolder(viewHolder, name,  matterProperty != null ? matterProperty.fullTitle:"", matterProperty != null ? matterProperty.address:"", View.VISIBLE);
                setupOnclickForCardview(viewHolder, "property", propertyCode);
                break;

            default:
                displayMatterLast(viewHolder, itemIndex);
                break;
        }
    }

    private void displayHeader(SectioningAdapter.ItemViewHolder holder, int position) {
        HeaderTypeViewHolder headerTypeViewHolder = (HeaderTypeViewHolder) holder;
        headerTypeViewHolder.headerName.setText(mRelatedMatter.systemNo);
        headerTypeViewHolder.headerIDNo.setText(mRelatedMatter.clientName().toUpperCase());

        headerTypeViewHolder.editBtn.setVisibility(View.VISIBLE);
        headerTypeViewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MatterActivity)mContext).onEditMatter();
            }
        });
    }

    void displayMatterInformation(SectioningAdapter.ItemViewHolder viewHolder, int itemIndex) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) viewHolder;
        String value = "";
        switch (itemIndex) {
            case 0:
                setupGeneralTypeHoler(generalTypeViewHolder, "Client", mRelatedMatter.getPrimaryClientName().toUpperCase(), View.VISIBLE);
                setupOnclickForCardview(generalTypeViewHolder, "contact", mRelatedMatter.getContactCode());
                break;
            case 1:
                value = DIHelper.convertToSimpleDateFormat(mRelatedMatter.openDate) + " / " + mRelatedMatter.getFileStatusDesc().toUpperCase()+ " / " + DIHelper.convertToSimpleDateFormat(mRelatedMatter.dateClose);
                setupGeneralTypeHoler(generalTypeViewHolder, "Open Date / Status / Closed Date", value, View.INVISIBLE);
                break;
            case 2:
                value = mRelatedMatter.locationBox + " / " + mRelatedMatter.locationPocket + " / " + mRelatedMatter.locationPhysical;
                setupGeneralTypeHoler(generalTypeViewHolder, "File Location / Pocket / Storage", value, View.INVISIBLE);
                break;
            case 3:
                setupGeneralTypeHoler(generalTypeViewHolder, "Matter", mRelatedMatter.getMatterDesc().toUpperCase(), View.VISIBLE);
                generalTypeViewHolder.searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        matterCodeClickListener.onMatterClick(view, mRelatedMatter.matter);
                    }
                });
                break;
            case 4:
                value = mRelatedMatter.getPartnerName().toUpperCase() + " / "
                        + mRelatedMatter.getLegalAssistantName().toUpperCase()  + " / "
                        + mRelatedMatter.getClerkName().toUpperCase();
                setupGeneralTypeHoler(generalTypeViewHolder, "Partner / LA / Clerk", value, View.INVISIBLE);
                break;
            case 5:
                setupGeneralTypeHoler(generalTypeViewHolder, "Branch", mRelatedMatter.getBranchName(), View.INVISIBLE);
                break;
            case 6:
                setupGeneralTypeHoler(generalTypeViewHolder, "Remarks", mRelatedMatter.remarks, View.INVISIBLE);
                break;
        }
    }

    void displayCourt(SectioningAdapter.ItemViewHolder viewHolder, int itemIndex) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) viewHolder;
        switch (itemIndex) {
            case 0:
                setupGeneralTypeHoler(generalTypeViewHolder, "Court", mRelatedMatter.getCourt(), View.INVISIBLE);
                break;
            case 1:
                setupGeneralTypeHoler(generalTypeViewHolder, "Place", mRelatedMatter.getCourtPlace(), View.INVISIBLE);
                break;
            case 2:
                setupGeneralTypeHoler(generalTypeViewHolder, "Case No", mRelatedMatter.getCourtCaseNo(), View.INVISIBLE);
                break;
            case 3:
                setupGeneralTypeHoler(generalTypeViewHolder, "Judge",mRelatedMatter.getCourtJudge(), View.INVISIBLE);
                break;
            case 4:
                setupGeneralTypeHoler(generalTypeViewHolder, "SAR", mRelatedMatter.getCourtSAR(), View.INVISIBLE);
                break;
        }
    }

    int getPartyIndex(int itemIndex) {
        int index = itemIndex;
        int cnt = 0;
        for (PartyGroup partyGroup : mRelatedMatter.partyGroup) {
            if (partyGroup.party != null) {
                cnt += partyGroup.party.size();
                if (itemIndex < cnt) {
                    break;
                }
                index = itemIndex - cnt;
            }
        }

        return index;
    }

    void displayPartiesGroup(SectioningAdapter.ItemViewHolder viewHolder, int itemIndex) {
        String clientName = "";
        String partyName = "";
        String code = "";
        int cnt = 0;
        int index = itemIndex;
        for (PartyGroup partyGroup : mRelatedMatter.partyGroup) {
            if (partyGroup.party != null) {
                cnt += partyGroup.party.size();
                if (itemIndex < cnt) {
                    clientName = partyGroup.party.get(index).name;
                    partyName = partyGroup.partyName;
                    code = partyGroup.party.get(index).code;
                    break;
                }
                index = itemIndex - cnt;
            }
        }

        if (index == 0) {
            setupGeneralTypeHoler((GeneralTypeViewHolder)viewHolder, partyName, clientName, View.VISIBLE);
            setupOnclickForCardview((GeneralTypeViewHolder)viewHolder, "contact", code);
        } else {
            OneLabelTypeViewHolder oneLabelTypeViewHolder = (OneLabelTypeViewHolder) viewHolder;
            oneLabelTypeViewHolder.description.setText(clientName);
            oneLabelTypeViewHolder.rightButton.setVisibility(View.VISIBLE);
            final String _code = code;
            oneLabelTypeViewHolder.searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, "contact", _code);
                }
            });
        }
    }

    void setupOnclickForCardview(SectioningAdapter.ItemViewHolder generalTypeViewHolder, final String type, final String code) {

        if (generalTypeViewHolder instanceof GeneralTypeViewHolder) {
            if (code == null || code.trim().length() == 0) {
                ((GeneralTypeViewHolder)generalTypeViewHolder).rightButton.setVisibility(View.INVISIBLE);
                return;
            } else {
                ((GeneralTypeViewHolder)generalTypeViewHolder).searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(v, type, code);
                    }
                });
            }
        } else {
            if (code == null || code.trim().length() == 0) {
                ((ThreeTypeViewHolder)generalTypeViewHolder).rightButton.setVisibility(View.INVISIBLE);
                return;
            } else {
                ((ThreeTypeViewHolder) generalTypeViewHolder).searchView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(v, type, code);
                    }
                });
            }

        }
    }

    void setupThreeTypeHolder(SectioningAdapter.ItemViewHolder holder, String title, String value1, String value2, int visibility) {
        ThreeTypeViewHolder threeTypeViewHolder = (ThreeTypeViewHolder) holder;
        threeTypeViewHolder.titleView.setText(title);
        threeTypeViewHolder.firstValue.setText(value1);
        threeTypeViewHolder.secondValue.setText(value2);
        threeTypeViewHolder.rightButton.setVisibility(visibility);
    }

    void setupGeneralTypeHoler(GeneralTypeViewHolder generalTypeViewHolder, String title, String value, int visibility) {
        generalTypeViewHolder.label.setText(title);
        generalTypeViewHolder.description.setText(value);
        generalTypeViewHolder.rightButton.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            generalTypeViewHolder.rightButton.setImageResource(R.mipmap.ic_detail_gray);
        }
    }

    void displayMatterLast(SectioningAdapter.ItemViewHolder holder, final int position) {
        MatterLastTypeViewHolder matterLasterTypeViewHolder = (MatterLastTypeViewHolder) holder;
        final String code = mRelatedMatter.systemNo;
        matterLasterTypeViewHolder.openFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileFolderClickListener.onFileFolderClick(v, code, "matter");
            }
        });

        matterLasterTypeViewHolder.openAccoutsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountsClickListener.onAccountsClick(v, code);
            }
        });

        matterLasterTypeViewHolder.fileNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileNoteClickListener.onFileNoteClick(v, code, mRelatedMatter.getPrimaryClientName());
            }
        });

        matterLasterTypeViewHolder.paymentRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentRecordClickListener.onPaymentRecordClick(v, code);
            }
        });
    }
}

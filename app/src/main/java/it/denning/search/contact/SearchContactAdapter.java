package it.denning.search.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.Contact;
import it.denning.model.SearchResultModel;
import it.denning.search.utils.OnClickListenerWithCode;

/**
 * Created by denningit on 25/04/2017.
 */

public class SearchContactAdapter extends SectioningAdapter {
    Contact mContact;
    Context mContext;
    Boolean isRelatedMatter;
    OnClickListenerWithCode clickListener;

    SearchContactAdapter(Contact contact, Context context, OnClickListenerWithCode clickListener, Boolean isRelatedMatter) {
        this.mContact = contact;
        this.mContext = context;
        this.isRelatedMatter = isRelatedMatter;
        this.clickListener = clickListener;
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

    public class GeneralTypeViewHolder extends SectioningAdapter.ItemViewHolder {
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

    public class LastTypeViewHolder extends SectioningAdapter.ItemViewHolder {
        @BindView(R.id.search_last) TextView fileNo;
        @BindView(R.id.search_name) TextView name;
        @BindView(R.id.search_date) TextView date;
        @BindView(R.id.search_cardview)
        CardView cardView;

        public LastTypeViewHolder(View itemView) {
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
        int count = 3;
        if (isRelatedMatter) {
            count = 2;
        }

        return count;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 0;
        if (sectionIndex == 0) {
            count = 1;
        } else if (sectionIndex == 1 && !isRelatedMatter) {
            count =  11;
        }

        if (sectionIndex == getNumberOfSections()-1){
            if (mContact.relatedMatter != null) {
                count =  mContact.relatedMatter.size();
            } else {
                count = 0;
            }
        }

        return count;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return sectionIndex != 0;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        int type = 0;
        if (sectionIndex == 0) {
            type = DIConstants.HEADER_TYPE;
        } else if (sectionIndex == 1 && !isRelatedMatter) {
            type =  DIConstants.GENERAL_TYPE;
        }

        if (sectionIndex == getNumberOfSections()-1) {
            type = DIConstants.LAST_TYPE;
        }

        return type;
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        SectioningAdapter.ItemViewHolder itemViewHolder = null;
        switch (itemUserType) {
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

                itemViewHolder = new GeneralTypeViewHolder(itemView);
                break;
            case DIConstants.LAST_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_last, parent, false);

                itemViewHolder =  new LastTypeViewHolder(itemView);
                break;
            default:
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
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        if (itemType == DIConstants.HEADER_TYPE) {
            displayHeader(viewHolder, itemIndex);
        } else if (itemType == DIConstants.GENERAL_TYPE){
            displayGeneral(viewHolder, itemIndex);
        } else {
            displayLast(viewHolder, itemIndex);
        }
    }

    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        String headerName = "";
        if (sectionIndex == 1) {
            headerName = "Main Information";
        } else if (sectionIndex == 2) {
            headerName = "Related Matter";
        }

        if (isRelatedMatter) {
            headerName = "Related Matter";
        }

        headerViewHolder.firstTitle.setText(headerName);
    }

    @Override
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
    }

    private void displayHeader(SectioningAdapter.ItemViewHolder holder, int position) {
        HeaderTypeViewHolder headerTypeViewHolder = (HeaderTypeViewHolder) holder;
        headerTypeViewHolder.headerName.setText(mContact.name.toUpperCase());
        headerTypeViewHolder.headerIDNo.setText(mContact.IDNo);
        headerTypeViewHolder.oldID.setText(mContact.KPLama);

        headerTypeViewHolder.oldID.setVisibility(View.VISIBLE);
        headerTypeViewHolder.editBtn.setVisibility(View.VISIBLE);
        headerTypeViewHolder.chatBtn.setVisibility(View.VISIBLE);

        headerTypeViewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchContactActivity)mContext).editContact();
            }
        });
    }

    private void displayGeneral(SectioningAdapter.ItemViewHolder holder, int position) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) holder;
        switch (position) {
            case 0:
                setupGeneralTypeHoler(generalTypeViewHolder, "Date of Birth", DIHelper.convertToSimpleDateFormat(mContact.dateOfBirth), View.INVISIBLE, -1);
                break;
            case 1:
                setupGeneralTypeHoler(generalTypeViewHolder, "Citizenship", mContact.citizenship.toUpperCase(), View.INVISIBLE, -1);
                break;
            case 2:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (mobile)", mContact.mobilePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 3:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (office)", mContact.officePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 4:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (home)", mContact.homePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 5:
                setupGeneralTypeHoler(generalTypeViewHolder, "Fax", mContact.fax, View.INVISIBLE, -1);
                break;
            case 6:
                setupGeneralTypeHoler(generalTypeViewHolder, "Email", mContact.email, View.VISIBLE, R.drawable.ic_email_red);
                break;
            case 7:
                setupGeneralTypeHoler(generalTypeViewHolder, "Tax File No", mContact.tax, View.INVISIBLE, -1);
                break;
            case 8:
                setupGeneralTypeHoler(generalTypeViewHolder, "IRD Branch", mContact.IRDBranch != null? mContact.IRDBranch.description: "", View.INVISIBLE, -1);
                break;
            case 9:
                setupGeneralTypeHoler(generalTypeViewHolder, "Occupation", mContact.occupation != null ? mContact.occupation.description.toUpperCase(): "", View.INVISIBLE, -1);
                break;
            case 10:
                setupGeneralTypeHoler(generalTypeViewHolder, "Address", mContact.address != null ? mContact.address.fullAddress: "", View.VISIBLE, R.drawable.ic_location);
                break;

            default:
                break;
        }
    }

    private void setupGeneralTypeHoler(GeneralTypeViewHolder generalTypeViewHolder, String title, final String value, int visibility, int imageResource) {
        generalTypeViewHolder.label.setText(title);
        generalTypeViewHolder.description.setText(value);
        generalTypeViewHolder.rightButton.setVisibility(visibility);
        if (value.trim().length() == 0) {
            generalTypeViewHolder.rightButton.setVisibility(View.INVISIBLE);
        } else if (visibility == View.VISIBLE) {
            generalTypeViewHolder.rightButton.setImageResource(imageResource);
            if (imageResource == R.drawable.ic_phone_red ) {
                generalTypeViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        phoneCall(value);
                    }
                });

            } else if (imageResource == R.drawable.ic_email_red  && value.trim().length() != 0) {
                generalTypeViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmail(value);
                    }
                });

            } else {

            }
        }
    }

    private void phoneCall(String phone) {
        Uri call = Uri.parse("tel:" + phone);
        Intent surf = new Intent(Intent.ACTION_DIAL, call);
        mContext.startActivity(surf);
    }

    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(intent, "Denning it.denning.App"));
    }

    private void displayLast(SectioningAdapter.ItemViewHolder holder, final int position) {
        LastTypeViewHolder lastTypeViewHolder = (LastTypeViewHolder) holder;

        final SearchResultModel matter = mContact.relatedMatter.get(position);
        String[] fileNoName = DIHelper.removeFileNoFromMatterTitle(matter.title);
        lastTypeViewHolder.fileNo.setText(fileNoName[0]);
        lastTypeViewHolder.name.setText(fileNoName[1]);
        lastTypeViewHolder.date.setText(DIHelper.convertToSimpleDateFormat(matter.sortDate));

        lastTypeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view, matter.key);
            }
        });
    }
}

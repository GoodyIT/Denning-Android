package it.denning.search.bank;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
import it.denning.model.Bank;

/**
 * Created by denningit on 27/04/2017.
 */

public class BankAdapter extends SectioningAdapter {
    Context mContext;
    Bank mBank;
    String TO, SUBJECT, MESSAGE ;

    BankAdapter(Bank bank, Context context) {
        this.mContext = context;
        this.mBank = bank;
    }

    public class HeaderTypeViewHolder  extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.search_header_name)
        TextView headerName;
        @BindView(R.id.search_header_IDNo) TextView headerIDNo;

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
        return 3;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int count = 0;
        if (sectionIndex == 0) {
            count = 1;
        } else if (sectionIndex == 1) {
            count =  5;
        } else {
            if (mBank.relatedMatter != null) {
                count =  mBank.relatedMatter.size();
            } else {
                count = 0;
            }
        }

        return count;
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex)
    {
        int type = 0;
        switch (sectionIndex) {
            case 0:
                type = DIConstants.HEADER_TYPE;
                break;
            case 1:
                type =  DIConstants.GENERAL_TYPE;
                break;
            default:
                type = DIConstants.LAST_TYPE;
                break;
        }
        return type;
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


    private void displayHeader(RecyclerView.ViewHolder holder, int position) {
        HeaderTypeViewHolder headerTypeViewHolder = (HeaderTypeViewHolder) holder;
        headerTypeViewHolder.headerName.setText(mBank.name);
        headerTypeViewHolder.headerIDNo.setText(mBank.IDNo);
    }

    void setupGeneralTypeHoler(GeneralTypeViewHolder generalTypeViewHolder, String title, final String value, int visibility, int imageResource) {
        generalTypeViewHolder.label.setText(title);
        generalTypeViewHolder.description.setText(value);
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

    private void displayGeneral(RecyclerView.ViewHolder holder, int position) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) holder;
        switch (position) {
            case 0:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone 1", mBank.homePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 1:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (mobile)", mBank.mobilePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 2:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (office)", mBank.officePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 3:
                setupGeneralTypeHoler(generalTypeViewHolder, "Email", mBank.email, View.VISIBLE, R.drawable.ic_email_red);
                break;
            case 4:
                setupGeneralTypeHoler(generalTypeViewHolder, "Address", mBank.address.fullAddress, View.VISIBLE, R.drawable.ic_location_red);
                break;

            default:
                break;
        }
    }

    private void displayLast(RecyclerView.ViewHolder holder, int position) {
        LastTypeViewHolder lastTypeViewHolder = (LastTypeViewHolder) holder;

        String[] fileNoName = DIHelper.removeFileNoFromMatterTitle(mBank.relatedMatter.get(position).title);
        lastTypeViewHolder.fileNo.setText(fileNoName[0]);
        lastTypeViewHolder.name.setText(fileNoName[1]);
        lastTypeViewHolder.date.setText(DIHelper.convertToSimpleDateFormat(mBank.relatedMatter.get(position).sortDate));
    }

    @Override
    public SectioningAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        switch (itemUserType) {
            case DIConstants.HEADER_TYPE:
                View itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_header, parent, false);

                return new HeaderTypeViewHolder(itemView);
            case DIConstants.GENERAL_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_general, parent, false);

                return new GeneralTypeViewHolder(itemView);
            case DIConstants.LAST_TYPE:
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.cardview_search_last, parent, false);

                return new LastTypeViewHolder(itemView);
            default:
                break;
        }

        return null;
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
    public GhostHeaderViewHolder onCreateGhostHeaderViewHolder(ViewGroup parent) {
        final View ghostView = new View(parent.getContext());
        ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new GhostHeaderViewHolder(ghostView);
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

        headerViewHolder.firstTitle.setText(headerName);
    }
}

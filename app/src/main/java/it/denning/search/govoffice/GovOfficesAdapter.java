package it.denning.search.govoffice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.GovOffice;
import it.denning.search.bank.BankAdapter;

/**
 * Created by denningit on 28/04/2017.
 */

public class GovOfficesAdapter extends RecyclerView.Adapter {
    Context mContext;
    GovOffice mGovOffice;
    String TO, SUBJECT, MESSAGE ;
    Map<Integer, Integer> typeSet;

    GovOfficesAdapter(GovOffice govOffice, Context context) {
        this.mContext = context;
        this.mGovOffice = govOffice;
        typeSet = new HashMap<>();
    }

    public class HeaderTypeViewHolder  extends RecyclerView.ViewHolder {
        @BindView(R.id.search_header_name)
        TextView headerName;
        @BindView(R.id.search_header_IDNo) TextView headerIDNo;

        public HeaderTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class GeneralTypeViewHolder extends RecyclerView.ViewHolder {
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

    public class LastTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_last) TextView matterName;

        public LastTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            typeSet.put(position, DIConstants.HEADER_TYPE);
            return DIConstants.HEADER_TYPE; //
        } else if (position > 0 && position < 8) {
            typeSet.put(position, DIConstants.GENERAL_TYPE);
            return DIConstants.GENERAL_TYPE; //
        } else {
            typeSet.put(position, DIConstants.LAST_TYPE);
            return DIConstants.LAST_TYPE; //
        }
    }

    private void displayHeader(RecyclerView.ViewHolder holder, int position) {
        HeaderTypeViewHolder headerTypeViewHolder = (HeaderTypeViewHolder) holder;
        headerTypeViewHolder.headerName.setText(mGovOffice.name);
        headerTypeViewHolder.headerIDNo.setText(mGovOffice.IDNo);
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
            case 1:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone 1", mGovOffice.homePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 2:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone 2", mGovOffice.homePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 3:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (mobile)", mGovOffice.mobilePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 4:
                setupGeneralTypeHoler(generalTypeViewHolder, "Phone (office)", mGovOffice.officePhone, View.VISIBLE, R.drawable.ic_phone_red);
                break;
            case 5:
                setupGeneralTypeHoler(generalTypeViewHolder, "Fax", mGovOffice.fax, View.INVISIBLE, R.drawable.ic_phone_red);
                break;
            case 6:
                setupGeneralTypeHoler(generalTypeViewHolder, "Email", mGovOffice.email, View.VISIBLE, R.drawable.ic_email_red);
                break;
            case 7:
                setupGeneralTypeHoler(generalTypeViewHolder, "Address", mGovOffice.address.fullAddress, View.VISIBLE, R.drawable.ic_location_red);
                break;

            default:
                break;
        }
    }

    private void displayLast(RecyclerView.ViewHolder holder, int position) {
        LastTypeViewHolder lastTypeViewHolder = (LastTypeViewHolder) holder;

        int index = position - 8;
//        lastTypeViewHolder.matterName.setText(DIHelper.removeFileNoFromMatterTitle(mGovOffice.relatedMatters.get(index).title));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Integer type = typeSet.get(position);
        if (type == DIConstants.HEADER_TYPE) {
            displayHeader(holder, position);
        } else if (type == DIConstants.GENERAL_TYPE) {
            displayGeneral(holder, position);
        } else {
            displayLast(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return 8 + mGovOffice.relatedMatters.size();
    }
}

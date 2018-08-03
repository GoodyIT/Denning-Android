package it.denning.search.property;

import android.content.Context;
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
import it.denning.model.MatterProperty;

/**
 * Created by denningit on 27/04/2017.
 */

public class PropertyAdapter extends RecyclerView.Adapter {
    Context mContext;
    MatterProperty mMatterProperty;
    Map<Integer, Integer> typeSet;

    PropertyAdapter(MatterProperty matterProperty, Context context) {
        this.mContext = context;
        this.mMatterProperty = matterProperty;
        typeSet = new HashMap<>();
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
       if (position < 3) {
            typeSet.put(position, DIConstants.GENERAL_TYPE);
            return DIConstants.GENERAL_TYPE; //
        } else {
            typeSet.put(position, DIConstants.LAST_TYPE);
            return DIConstants.LAST_TYPE; //
        }
    }

    void setupGeneralTypeHoler(GeneralTypeViewHolder generalTypeViewHolder, String title, String value, int visibility, int imageResource) {
        generalTypeViewHolder.label.setText(title);
        generalTypeViewHolder.description.setText(value);
        generalTypeViewHolder.rightButton.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            generalTypeViewHolder.rightButton.setImageResource(imageResource);
        }
    }

    private void displayGeneral(RecyclerView.ViewHolder holder, int position) {
        GeneralTypeViewHolder generalTypeViewHolder = (GeneralTypeViewHolder) holder;
        switch (position) {
            case 0:
//                setupGeneralTypeHoler(generalTypeViewHolder, mMatterProperty.lotPtType, mMatterProperty.lotPtValue, View.INVISIBLE, R.drawable.ic_phone_red);
                break;
            case 1:
                setupGeneralTypeHoler(generalTypeViewHolder, "Description", mMatterProperty.fullTitle, View.INVISIBLE, R.drawable.ic_phone_red);
                break;
            case 2:
                setupGeneralTypeHoler(generalTypeViewHolder, "Address", mMatterProperty.address, View.VISIBLE, R.drawable.ic_location_red);
                break;
        }
    }

    private void displayLast(RecyclerView.ViewHolder holder, int position) {
        LastTypeViewHolder lastTypeViewHolder = (LastTypeViewHolder) holder;

        int index = position - 3;
//        lastTypeViewHolder.matterName.setText(DIHelper.removeFileNoFromMatterTitle(mMatterProperty.relatedMatters.get(index).title));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DIConstants.GENERAL_TYPE:
                View itemView = LayoutInflater.
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
        if (type == DIConstants.GENERAL_TYPE) {
            displayGeneral(holder, position);
        } else {
            displayLast(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

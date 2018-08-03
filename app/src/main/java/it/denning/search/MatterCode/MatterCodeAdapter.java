package it.denning.search.MatterCode;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.model.MatterCodeModel;
import it.denning.search.govoffice.GovOfficesAdapter;

/**
 * Created by denningit on 2017-12-12.
 */

public class MatterCodeAdapter extends RecyclerView.Adapter {
    private final MatterCodeModel matterCodeModel;

    public MatterCodeAdapter(MatterCodeModel matterCodeModel) {
        this.matterCodeModel = matterCodeModel;
    }

    public class GeneralTypeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_textview)
        TextView label;
        @BindView(R.id.value_textView) TextView description;
        @BindView(R.id.general_cardview)
        CardView cardView;

        public GeneralTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_mattercode, parent, false);

        return new GeneralTypeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GeneralTypeViewHolder viewHolder = (GeneralTypeViewHolder) holder;
        String name = "", description = "";
        switch (position) {
            case 0:
                name = "Category";
                description = matterCodeModel.category;
                break;
            case 1:
                name = "Description";
                description = matterCodeModel.description;
                break;
            case 2:
                name = "Form Name";
                description = matterCodeModel.formName;
                break;
            case 3:
                name = "Group Name 1";
                description = matterCodeModel.groupName1;
                break;
            case 4:
                name = "Group Name 2";
                description = matterCodeModel.groupName2;
                break;
            case 5:
                name = "Group Name 3";
                description = matterCodeModel.groupName3;
                break;
            case 6:
                name = "Group Name 4";
                description = matterCodeModel.groupName4;
                break;
            case 7:
                name = "Group Name 5";
                description = matterCodeModel.groupName5;
                break;
            case 8:
                name = "Turn Around";
                description = matterCodeModel.turnAround;
                break;
        }

        viewHolder.label.setText(name);
        viewHolder.description.setText(description);
        if (position % 2 == 0) {
           int backgroundColor =  Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(App.getInstance(), R.color.light_gray)));
            viewHolder.cardView.setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }
}

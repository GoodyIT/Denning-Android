package it.denning.auth.branch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by denningit on 20/04/2017.
 */

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    List<FirmModel> firmModelList;

    BranchAdapter(List<FirmModel> firmModelList) {
        this.firmModelList = firmModelList;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_branch, parent, false);

        return new BranchAdapter.BranchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        FirmModel firmModel = firmModelList.get(position);
        holder.branchName.setText(firmModel.LawFirm.name + "\n" + firmModel.LawFirm.address.city);
        holder.branchName.setTag(position);
        holder.branchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirmBranchActivity.onClickListener.onClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return firmModelList.size();
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.branch_name)
        Button branchName;

        public BranchViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

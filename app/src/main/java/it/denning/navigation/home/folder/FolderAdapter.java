package it.denning.navigation.home.folder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.DocumentModel;

/**
 * Created by denningit on 20/04/2017.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.BranchViewHolder> {
    List<DocumentModel> list;

    FolderAdapter(List<DocumentModel> list) {
        this.list = list;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_branch, parent, false);

        return new FolderAdapter.BranchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        DocumentModel model = list.get(position);
        holder.branchName.setText(model.name);
        holder.branchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FolderActivity.onClickListener.onClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.branch_name)
        TextView branchName;

        public BranchViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

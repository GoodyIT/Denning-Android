package it.denning.navigation.home.branch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.auth.SplashActivity;
import it.denning.auth.branch.FirmBranchActivity;
import it.denning.general.DISharedPreferences;
import it.denning.model.FirmModel;
import it.denning.model.FirmURLModel;
import it.denning.navigation.home.Home;

/**
 * Created by denningit on 20/04/2017.
 */

public class HomeBranchAdapter extends RecyclerView.Adapter<HomeBranchAdapter.BranchViewHolder> {

    private FirmURLModel firmURLModel;

    public HomeBranchAdapter(FirmURLModel firmURLModel) {
        this.firmURLModel = firmURLModel;
    }

    @Override
    public BranchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cardview_branch_home, parent, false);

        return new HomeBranchAdapter.BranchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BranchViewHolder holder, int position) {
        final FirmModel firmModel = firmURLModel.catDenning.get(position);
        holder.branchName.setText(firmModel.LawFirm.name);
        holder.branchCity.setText(firmModel.LawFirm.address.city);
        holder.branchName.setTag(position);
        if (firmModel.APIServer.equals(DISharedPreferences.getInstance().getServerAPI())) {
            holder.branchCheck.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_check));
        } else {
            holder.branchCheck.setVisibility(View.INVISIBLE);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DISharedPreferences.getInstance().saveServerAPI(firmModel);
                Home.onClickListener.onClick(view);
            }
        });
    }


    @Override
    public int getItemCount() {
        return firmURLModel.catDenning.size();
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.branch_name)
        TextView branchName;
        @BindView(R.id.branch_city)
        TextView branchCity;
        @BindView(R.id.branch_check)
        ImageView branchCheck;
        @BindView(R.id.branch_cardview)
        CardView cardView;

        public BranchViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}

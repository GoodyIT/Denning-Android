package it.denning.navigation.home.calculators.legalcost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

public class SPAFragment extends Fragment {
    @BindView(R.id.spa_relationship_textview)
    AppCompatEditText relationship;

    private int relationship_sel;

    @OnClick(R.id.spa_relationship_textview)
    void chooseRelationship() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_a_relation)
                .items(R.array.spa_relationship)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        relationship.setText(text);
                        relationship_sel = which;
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .show();
    }

    public static SPAFragment newInstance() {
        SPAFragment fragment = new SPAFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_spa, container, false);
        return view;
    }
}

package it.denning.navigation.home.calculators.legalcost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

public class LoanFragment extends Fragment {
    @BindView(R.id.loan_type_textview)
    AppCompatEditText type;
    private int type_sel;

    @OnClick(R.id.loan_type_textview)
    void chooseType() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_area_type_title)
                .items(R.array.loan_type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        type.setText(text);
                        type_sel = which;
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .show();
    }


    public static LoanFragment newInstance() {
        LoanFragment fragment = new LoanFragment();
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
        View view = inflater.inflate(R.layout.fragment_calculate_loan, container, false);
        return view;
    }
}

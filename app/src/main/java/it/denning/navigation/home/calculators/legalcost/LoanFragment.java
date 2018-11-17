package it.denning.navigation.home.calculators.legalcost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

public class LoanFragment extends Fragment {
    @BindView(R.id.loan_type_textview)
    AppCompatEditText type;
    @BindView(R.id.loan_textview)
    AppCompatEditText loanTextview;
    @BindView(R.id.stamp_textview)
    TextView stampTextview;
    @BindView(R.id.legal_textview)
    TextView legalFeeTextview;
    @BindView(R.id.total_textview)
    TextView totalTextview;

    private float[] loan_type_array = {0.005f, (0.005f*0.8f)};
    private float loan_type_sel = 0.005f;
    private int loan_type_sel_index = 0;

    @OnClick(R.id.loan_type_textview)
    void chooseType() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_loan_type)
                .items(R.array.loan_type)
                .itemsCallbackSingleChoice(loan_type_sel_index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        type.setText(text);
                        loan_type_sel = loan_type_array[which];
                        loan_type_sel_index = which;
                        calc();
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .negativeText(R.string.dlg_cancel)
                .show();
    }

    public static LoanFragment newInstance() {
        LoanFragment fragment = new LoanFragment();
        return fragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        type.setText(getResources().getStringArray(R.array.loan_type)[loan_type_sel_index]);
        loan_type_sel = loan_type_array[loan_type_sel_index];

        loanTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_loan, container, false);
        return view;
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        loanTextview.setText("");
        type.setText("");
        stampTextview.setText("");
        legalFeeTextview.setText("");
        totalTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float priceValue = DIHelper.toFloat(loanTextview.getText().toString());
        float backPrice = priceValue;
        if (priceValue == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_loan_required);
            ErrorUtils.showError(getContext(), R.string.alert_loan_required);
            return;
        }

        float stampDuty = (float) (Math.ceil(priceValue / 1000) * 10);
        stampDuty = stampDuty * loan_type_sel;
        priceValue = DIHelper.calcLoanAndLegal(backPrice)[0];
        float legalFee = DIHelper.calcLoanAndLegal(backPrice)[1];
        if (priceValue > 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_legal_negotiate);
            ErrorUtils.showError(getContext(), R.string.alert_legal_negotiate);
        }
        float totalLoan = (stampDuty+legalFee);
        stampTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", stampDuty)));
        legalFeeTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", legalFee)));
        totalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", totalLoan)));
    }

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if (hasFocus || !isVisible() || isDetached()) {
                return;
            }

            float value = DIHelper.toFloat(((AppCompatEditText)v).getText().toString());
            String valueWithComma = DIHelper.addThousandsSeparator(String.valueOf(value));

            switch (Integer.valueOf(v.getTag().toString())) {
                case 1:
                    loanTextview.setText(valueWithComma);
                    break;
            }

            calc();
        }
    }
}

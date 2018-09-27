package it.denning.navigation.home.calculators.legalcost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;
import it.denning.navigation.home.util.MinMaxFilter;

public class SPAFragment extends Fragment {
    @BindView(R.id.spa_relationship_textview)
    AppCompatEditText relationship;
    @BindView(R.id.price_textview)
    AppCompatEditText priceTextview;
    @BindView(R.id.loan_margin_textview)
    AppCompatEditText loanMarginTextview;
    @BindView(R.id.loan_amount_textview)
    AppCompatEditText loanAmountTextview;
    @BindView(R.id.loan_type_textview)
    AppCompatEditText loanTypeTexview;
    @BindView(R.id.stamp_spa_textview)
    TextView stampSPATextview;
    @BindView(R.id.stamp_loan_textview)
    TextView stampLoanTextview;
    @BindView(R.id.legal_spa_textview)
    TextView legalSPATextview;
    @BindView(R.id.legal_loan_textview)
    TextView legalLoanTextview;
    @BindView(R.id.total_spa_textview)
    TextView totalSPATextview;
    @BindView(R.id.total_loan_textview)
    TextView totalLoanTextview;
    @BindView(R.id.grand_total_textview)
    TextView grandTotalTextview;

    private float relationship_sel = -2.0f;
    private float[] relationship_sel_array = {1, 0, 0.5f, 0.5f, -1, -1, -1, -1, -1};
    private int relationship_sel_index = 0;
//    private ArrayList<String> loan_margin_array = new ArrayList<>();
//    private ArrayList<Integer> loan_margin_int_array = new ArrayList<>();
    private int margin_sel;
    private float[] loan_type_array = {0.005f, (0.005f*0.8f)};
    private float loan_type_sel;
    private int loan_type_sel_index = 0;

    @OnClick(R.id.spa_relationship_textview)
    void chooseRelationship() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_a_relation)
                .items(R.array.spa_relationship)
                .itemsCallbackSingleChoice(relationship_sel_index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        relationship.setText(text);
                        relationship_sel = relationship_sel_array[which];
                        relationship_sel_index = which;
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .negativeText(R.string.dlg_cancel)
                .show();
    }

    @OnClick(R.id.loan_type_textview)
    void chooseLoanType() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_loan_type)
                .items(R.array.loan_type)
                .negativeText(R.string.dlg_cancel)
                .itemsCallbackSingleChoice(loan_type_sel_index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        loanTypeTexview.setText(text);
                        loan_type_sel = loan_type_array[which];
                        loan_type_sel_index = which;
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

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loanMarginTextview.setFilters(new InputFilter[]{ new MinMaxFilter("1", "100")});
        loanMarginTextview.setText(String.valueOf(1));

        // generate loan margin array
//        for (int i = 1; i <= 100; i++) {
//            loan_margin_array.add(String.valueOf(i + "%"));
//            loan_margin_int_array.add(i);
//        }
        relationship.setText(getResources().getStringArray(R.array.spa_relationship)[relationship_sel_index]);
        relationship_sel = relationship_sel_array[relationship_sel_index];
        loanTypeTexview.setText(getResources().getStringArray(R.array.loan_type)[loan_type_sel_index]);
        loan_type_sel = loan_type_array[loan_type_sel_index];
        loanMarginTextview.setText("1");
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        priceTextview.setText("");
        relationship.setText("");
        loanMarginTextview.setText("");
        loanAmountTextview.setText("");
        loanTypeTexview.setText("");
        stampSPATextview.setText("");
        stampLoanTextview.setText("");
        legalLoanTextview.setText("");
        legalSPATextview.setText("");
        totalSPATextview.setText("");
        totalLoanTextview.setText("");
        grandTotalTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float priceValue = DIHelper.toFloat(priceTextview.getText().toString());
        float backPrice = priceValue;
        float loanMargin = DIHelper.toFloat(loanMarginTextview.getText().toString());
        if (priceValue == 0) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_price_required);
            return;
        }

        if (relationship_sel == -2.0f) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_price_required);
            return;
        }

        if (loanMargin == 0) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_loan_margin_required);
            return;
        }

// Calculate the stam Duty
        float stamDuty = 0;
        if (priceValue  >= 100000) {
            stamDuty  += 100000* 0.01;
        } else {
            stamDuty  += priceValue * 0.01;
        }

        priceValue  -= 100000;

        if (priceValue  > 0 && priceValue  < 400000){
            stamDuty  += priceValue *0.02;
        } else if (priceValue  >= 400000) {
            stamDuty  += 400000*0.02;
        }

        priceValue  -= 400000;

        if (priceValue  > 0) {
            stamDuty  += priceValue *0.03;
        }

        priceValue = DIHelper.calcLoanAndLegal(backPrice)[0];
        float legalFee = DIHelper.calcLoanAndLegal(backPrice)[1];
        if (priceValue > 0) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_legal_negotiate);
        }

        if (relationship_sel == -1) {
            stamDuty += 10;
        } else {
            stamDuty *= relationship_sel;
        }

        float totalSPA = stamDuty + legalFee;
        stampSPATextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", stamDuty)));
        legalSPATextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", legalFee)));
        totalSPATextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f",totalSPA )));

        // Calculate Loan
        float amount = backPrice * loanMargin / 100.0f;
        float stampLoan = loan_type_sel * backPrice;
        float totalLoan = (stampLoan+legalFee);
        loanAmountTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", amount)));
        stampLoanTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", stampLoan)));
        legalLoanTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", legalFee)));
        totalLoanTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", totalLoan)));
        grandTotalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", (totalLoan+totalSPA))));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_spa, container, false);
        return view;
    }

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if (hasFocus) {
                return;
            }
            float value = DIHelper.toFloat(((AppCompatEditText)v).getText().toString());
            String valueWithComma = DIHelper.addThousandsSeparator(String.valueOf(value));

            switch (Integer.valueOf(v.getTag().toString())) {
                case 1:
                    priceTextview.setText(valueWithComma);
                    break;
                case 2:
                    loanAmountTextview.setText(valueWithComma);
                    break;
            }
        }
    }
}

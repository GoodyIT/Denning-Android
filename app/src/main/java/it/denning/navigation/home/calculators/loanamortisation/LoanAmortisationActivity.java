package it.denning.navigation.home.calculators.loanamortisation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.home.calculators.realproperty.RealPropertyActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class LoanAmortisationActivity extends BaseActivity {
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.loan_amount_textview)
    AppCompatEditText loanAmountTextview;
    @BindView(R.id.annual_interest_textview)
    AppCompatEditText annualInterestTextview;
    @BindView(R.id.loan_period_textview)
    AppCompatEditText loanPeriodTextview;
    @BindView(R.id.monthly_installment_textview)
    TextView monthlyInstallmentTextview;
    @BindView(R.id.number_of_months_textview)
    TextView numberofMonthsTextview;
    @BindView(R.id.total_textview)
    TextView totalTextview;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoanAmortisationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_loan_armotisation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.loan_amortisation));
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        loanAmountTextview.setText("");
        annualInterestTextview.setText("");
        loanPeriodTextview.setText("");
        monthlyInstallmentTextview.setText("");
        numberofMonthsTextview.setText("");
        totalTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float P = Float.valueOf(loanAmountTextview.getText().toString().replace(",", ""));
        float r = Float.valueOf(annualInterestTextview.getText().toString().replace(",", "")) / 12 / 100;
        float n = Float.valueOf(loanPeriodTextview.getText().toString().replace(",", "")) * 12;

        if (P == 0) {
            DIAlert.showSimpleAlert(this, R.string.warning_title, R.string.alert_loan_amount_required);
            return;
        }
        float monthlyInstallment =  (float)(P * (r + r / (Math.pow(1+r, n) - 1)));
        float total = n * monthlyInstallment - P;

        monthlyInstallmentTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", monthlyInstallment)));
        numberofMonthsTextview.setText(String.valueOf(n));
        totalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", total)));
    }
}

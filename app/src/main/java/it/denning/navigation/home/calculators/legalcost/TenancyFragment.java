package it.denning.navigation.home.calculators.legalcost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.quickblox.q_municate_db.utils.ErrorUtils;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.internal.operators.flowable.FlowableSamplePublisher;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.dashboard.section1.staffleave.leaveapp.DashboardLeaveAppFragment;

public class TenancyFragment extends Fragment {
    @BindView(R.id.monthly_rent_textview)
    AppCompatEditText monthlyRentTextview;
    @BindView(R.id.annual_rent_textview)
    TextView annualRentRextview;
    @BindView(R.id.terms_tenancy_textview)
    AppCompatEditText termsTenancyTextview;
    @BindView(R.id.type_textview)
    AppCompatEditText type;
    @BindView(R.id.stamp_duty_textview)
    TextView stampDutyTextview;
    @BindView(R.id.legal_fee_textview)
    TextView legalFeeTextview;
    @BindView(R.id.total_textview)
    TextView totalTextview;

    private float type_sel = 0.25f;
    private int type_sel_index = 0;
    private float[] type_int_array = {0.25f, 0.5f};

    public static TenancyFragment newInstance() {
        TenancyFragment fragment = new TenancyFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        type.setText(getResources().getStringArray(R.array.tenancy_type)[type_sel_index]);
        type_sel = type_int_array[type_sel_index];

        monthlyRentTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
        termsTenancyTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_tenancy_lease, container, false);
        return view;
    }

    @OnClick(R.id.type_textview)
    void chooseType() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.select_loan_type)
                .items(R.array.tenancy_type)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        type.setText(text);
                        type_sel = type_int_array[which];
                        type_sel_index = which;
                        calc();
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .negativeText(R.string.dlg_cancel)
                .show();
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        monthlyRentTextview.setText("");
        annualRentRextview.setText("");
        termsTenancyTextview.setText("");
        type.setText("");
        stampDutyTextview.setText("");
        legalFeeTextview.setText("");
        totalTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float monthlyRent = DIHelper.toFloat(monthlyRentTextview.getText().toString());
        if (monthlyRent == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_monthly_rent_required);
            ErrorUtils.showError(getContext(), R.string.alert_monthly_rent_required);
            return;
        }
        float annualRent = monthlyRent / 12f;
        annualRentRextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", annualRent)));

        float termsTenancy = DIHelper.toFloat(termsTenancyTextview.getText().toString());
        if (termsTenancy == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_terms_tenancy_required);
            ErrorUtils.showError(getContext(), R.string.alert_terms_tenancy_required);
            return;
        }

        float stampDuty;
        if (annualRent <= 2400) {
            stampDuty = 0;
        }

        if (termsTenancy == 1) {
            stampDuty = annualRent / 250.0f;
        } else if (termsTenancy < 4) {
            stampDuty = annualRent / 250.0f * 2;
        } else {
            stampDuty = annualRent / 250.0f * 4;
        }

        float legalFee = monthlyRent * type_sel;
        float total = stampDuty + legalFee;

        stampDutyTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", stampDuty)));
        legalFeeTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", legalFee)));
        totalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", total)));
    }

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if (hasFocus || !isVisible() || isDetached()) {
                return;
            }

            if (v.getTag().toString().equals("2")) { // Terms of tenancy
                calc();
                return;
            }

            float value = DIHelper.toFloat(((AppCompatEditText)v).getText().toString());
            String valueWithComma = DIHelper.addThousandsSeparator(String.valueOf(value));

            switch (Integer.valueOf(v.getTag().toString())) {
                case 1:
                    monthlyRentTextview.setText(valueWithComma);
                    break;
            }

            calc();
        }
    }
}

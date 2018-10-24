package it.denning.navigation.home.calculators.legalcost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.home.util.MinMaxFilter;

public class SharesFragment extends Fragment {
    @BindView(R.id.sale_consideration_textview)
    AppCompatEditText saleConsiderationTextview;
    @BindView(R.id.nta_textview)
    AppCompatEditText ntaTextview;
    @BindView(R.id.per_textview)
    AppCompatEditText perTextview;
    @BindView(R.id.stamp_textview)
    TextView stampTextview;
    @BindView(R.id.legal_fee_textview)
    TextView legalFeeTextview;
    @BindView(R.id.legal_fee_margin_textview)
    AppCompatEditText legalFeeMarginTextview;
    @BindView(R.id.total_textview)
    TextView totalTextview;

    public static SharesFragment newInstance() {
        SharesFragment fragment = new SharesFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        legalFeeMarginTextview.setFilters(new InputFilter[]{ new MinMaxFilter("1", "100")});
        legalFeeMarginTextview.setText(String.valueOf(1));

        saleConsiderationTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
        ntaTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
        perTextview.setOnFocusChangeListener(new MyCustomEditTextListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculate_shares, container, false);
        return view;
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        saleConsiderationTextview.setText("");
        ntaTextview.setText("");
        perTextview.setText("");
        stampTextview.setText("");
        legalFeeTextview.setText("");
        totalTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float saleConsideration = DIHelper.toFloat(saleConsiderationTextview.getText().toString());
        float nta = DIHelper.toFloat(ntaTextview.getText().toString());
        float per = DIHelper.toFloat(perTextview.getText().toString());
        if (saleConsideration == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_sale_consideration_required);
            ErrorUtils.showError(getContext(), R.string.alert_sale_consideration_required);
            return;
        }
        if (nta == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_nta_required);
            ErrorUtils.showError(getContext(), R.string.alert_nta_required);
            return;
        }
        if (per == 0) {
//            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_per_required);
            ErrorUtils.showError(getContext(), R.string.alert_per_required);
            return;
        }
        float hightestValue = Math.max(saleConsideration, nta);
        hightestValue = Math.max(per, hightestValue);
        float stampDuty = (float) (Math.ceil(hightestValue / 1000.0f) * 3);
        float factor = DIHelper.toFloat(legalFeeMarginTextview.getText().toString());
        float legalFee = hightestValue * factor / 100.0f;
        float total = legalFee + stampDuty;

        stampTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", stampDuty)));
        legalFeeTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", legalFee)));
        totalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", total)));
    }

    protected class MyCustomEditTextListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, final boolean hasFocus) {
            if (hasFocus || !isVisible() || isDetached()) {
                return;
            }

            if (v.getTag().toString().equals("4")) { // Loan Margin
                calc();
                return;
            }

            float value = DIHelper.toFloat(((AppCompatEditText)v).getText().toString());
            String valueWithComma = DIHelper.addThousandsSeparator(String.valueOf(value));

            switch (Integer.valueOf(v.getTag().toString())) {
                case 1:
                    saleConsiderationTextview.setText(valueWithComma);
                    break;
                case 2:
                    ntaTextview.setText(valueWithComma);
                    break;
                case 3:
                    perTextview.setText(valueWithComma);
                    break;
            }

            calc();
        }
    }
}
package it.denning.navigation.home.calculators.realproperty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIHelper;
import it.denning.navigation.add.utils.basesectionadapter.BaseSectionAdapter;
import it.denning.navigation.home.calculators.incometax.IncomeTaxActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

public class RealPropertyActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener{
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.sale_price_textview)
    AppCompatEditText salePriceTextview;
    @BindView(R.id.sale_commission_textview)
    AppCompatEditText saleCommissionTextview;
    @BindView(R.id.legal_costs_textview)
    AppCompatEditText legalCostsTextview;
    @BindView(R.id.renovation_textview)
    AppCompatEditText renovationTextview;
    @BindView(R.id.net_disposal_textview)
    TextView netDisposalTextview;

    @BindView(R.id.purchase_price_textview)
    AppCompatEditText purchasePriceTextview;
    @BindView(R.id.purchase_commision_textview)
    AppCompatEditText purchaseCommisionTextview;
    @BindView(R.id.legal_stamp_textview)
    AppCompatEditText legalStampTextview;
    @BindView(R.id.other_costs_textview)
    AppCompatEditText otherCostsTextview;
    @BindView(R.id.net_acquisition_textview)
    TextView netAcquisitionTextview;

    @BindView(R.id.gain_loss_textview)
    TextView gainLossTextview;
    @BindView(R.id.date_disposal_textview)
    AppCompatEditText dateDisposalTextview;
    @BindView(R.id.date_acquisition_textview)
    AppCompatEditText dateAcquisitionTextview;
    @BindView(R.id.number_of_year_textview)
    AppCompatEditText numberOfYearTextview;
    @BindView(R.id.state_taxpayer_textview)
    AppCompatEditText stateTaxpayerTextview;

    @BindView(R.id.tax_payable_textview)
    TextView taxPayableTextview;
    @BindView(R.id.tax_rate_textview)
    TextView taxRateTextview;

    private String taxPayer_sel = "";
    private int taxPayer_sel_index = 0;
    private float taxRate = -1.0f;
    private DatePickerDialog dpd;
    private String selectedDateRow = "Disposal";
    private String acquisitionDate = "", disposalDate = "";
    private float numberOfYears;

    public MyCustomEditTextListener netDisposalListener, netAcquisitionListener;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RealPropertyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_real_property_gain;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    @SuppressLint("ResourceType")
    private void initFields() {
        toolbarTitle.setText(getString(R.string.real_property_gains_tax));

        taxPayer_sel = getResources().getStringArray(R.array.state_of_taxprayer)[taxPayer_sel_index];
        stateTaxpayerTextview.setText(taxPayer_sel);

        netDisposalListener = new MyCustomEditTextListener();
        salePriceTextview.setOnFocusChangeListener(netDisposalListener);
        saleCommissionTextview.setOnFocusChangeListener(netDisposalListener);
        legalCostsTextview.setOnFocusChangeListener(netDisposalListener);
        renovationTextview.setOnFocusChangeListener(netDisposalListener);
        purchaseCommisionTextview.setOnFocusChangeListener(netDisposalListener);
        purchasePriceTextview.setOnFocusChangeListener(netDisposalListener);
        legalStampTextview.setOnFocusChangeListener(netDisposalListener);
        otherCostsTextview.setOnFocusChangeListener(netDisposalListener);
    }

    @OnClick(R.id.state_taxpayer_textview)
    void choseStateOfTaxpayer() {
        new MaterialDialog.Builder(this)
                .title(R.string.select_state_of_taxpayer)
                .items(R.array.state_of_taxprayer)
                .itemsCallbackSingleChoice(taxPayer_sel_index, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        stateTaxpayerTextview.setText(text);
                        taxPayer_sel = String.valueOf(text);
                        taxPayer_sel_index = which;
                        return true;
                    }
                })
                .positiveText(R.string.dlg_ok)
                .negativeText(R.string.dlg_cancel)
                .show();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = DIHelper.getBirthday(datePickerDialog.getSelectedDay());
        if (selectedDateRow.equals("Disposal")) {
            disposalDate = date;
            dateDisposalTextview.setText(disposalDate);
        } else {
            acquisitionDate = date;
            dateAcquisitionTextview.setText(acquisitionDate);
        }

        if (!disposalDate.isEmpty() && !acquisitionDate.isEmpty()) {
            numberOfYears = DIHelper.calcDateDiff(disposalDate, acquisitionDate);
            numberOfYearTextview.setText(String.valueOf(numberOfYears));
        }
    }

    @OnClick(R.id.date_disposal_textview)
    void showDisposalDate() {
        String date = dateDisposalTextview.getText().toString();
        selectedDateRow = "Disposal";
        showCalendar(date, "01 Jan 2014");
    }

    @OnClick(R.id.date_acquisition_textview)
    void showAcquisitionDate() {
        String date = dateAcquisitionTextview.getText().toString();
        selectedDateRow = "Acquisition";
        showCalendar(date, "");
    }

    void showCalendar(String date, String _minDate) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date testDate = null;
        Date minDate = null;
        if (date.isEmpty()) {
            calendar = Calendar.getInstance();
        } else {
            try {
                testDate = sdf.parse(date);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            calendar = Calendar.getInstance();
            calendar.setTime(testDate);
        }
        try {
            minDate = sdf.parse(_minDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dpd = DatePickerDialog.newInstance(
                this,
                calendar
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        if (!_minDate.isEmpty()) {
            Calendar minCal = Calendar.getInstance();
            minCal.setTime(minDate);
            dpd.setMinDate(minCal);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.reset_btn)
    void reset() {
        saleCommissionTextview.setText("");
        salePriceTextview.setText("");
        legalCostsTextview.setText("");
        renovationTextview.setText("");
        netDisposalTextview.setText("");
        purchaseCommisionTextview.setText("");
        purchasePriceTextview.setText("");
        legalStampTextview.setText("");
        otherCostsTextview.setText("");
        netAcquisitionTextview.setText("");
        gainLossTextview.setText("");
        dateDisposalTextview.setText("");
        dateAcquisitionTextview.setText("");
        numberOfYearTextview.setText("");
        stateTaxpayerTextview.setText("");
        taxPayableTextview.setText("");
        taxRateTextview.setText("");
    }

    @OnClick(R.id.calculate_btn)
    void calc() {
        float saleCommision = DIHelper.toFloat(saleCommissionTextview.getText().toString());
        float salePrice = DIHelper.toFloat(salePriceTextview.getText().toString());
        float legalCosts = DIHelper.toFloat(legalCostsTextview.getText().toString());
        float renovation = DIHelper.toFloat(renovationTextview.getText().toString());
        float netDisposal = DIHelper.toFloat(netDisposalTextview.getText().toString());
        float purchaseCommission = DIHelper.toFloat(purchaseCommisionTextview.getText().toString());
        float purchasePrice = DIHelper.toFloat(purchasePriceTextview.getText().toString());
        float legalStamp = DIHelper.toFloat(legalStampTextview.getText().toString());
        float otherCosts = DIHelper.toFloat(otherCostsTextview.getText().toString());
        float netAcquisition = DIHelper.toFloat(netAcquisitionTextview.getText().toString());
        float gainLoss = DIHelper.toFloat(gainLossTextview.getText().toString());
        String dateDisposal = dateDisposalTextview.getText().toString();
        String dateAcquisition = dateAcquisitionTextview.getText().toString();
        float numberOfYearsHeld = DIHelper.toFloat(numberOfYearTextview.getText().toString());

        if (dateDisposal.isEmpty()|| dateAcquisition.isEmpty()) {
            DIAlert.showSimpleAlert(this, R.string.warning_title, R.string.alert_required_all);
            return;
        }

        if (taxPayer_sel.equals("Malaysian Company")) {
            calculateTaxRateForLocalCompany();
        } else if (taxPayer_sel.equals("Malaysian Individual / PR")) {
            calculateTaxRateForLocalPerson();
        } else if (taxPayer_sel.equals("Foreigner") || taxPayer_sel.equals("Foreign Company")) {
            calculateTaxRateForForeignerAndCompany();
        }

        float realpropertyTax = gainLoss * taxRate / 100;

        taxRateTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", taxRate)));
        taxPayableTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", realpropertyTax)));
    }


    void calculateTaxRateForLocalCompany() {
        taxRate = 0;

        if (numberOfYears < 2) {
            taxRate = 30;
        } else if (numberOfYears < 3) {
            taxRate = 30;
        } else if (numberOfYears < 4) {
            taxRate = 20;
        } else if (numberOfYears < 5) {
            taxRate = 15;
        } else {
            taxRate = 5;
        }
    }

    void calculateTaxRateForLocalPerson() {
        if (numberOfYears < 2) {
            taxRate = 30;
        } else if (numberOfYears < 3) {
            taxRate = 30;
        } else if (numberOfYears < 4) {
            taxRate = 20;
        } else if (numberOfYears < 5) {
            taxRate = 15;
        } else {
            taxRate = 0;
        }
    }

    void calculateTaxRateForForeignerAndCompany() {
        if (numberOfYears < 5) {
            taxRate = 30;
        } else {
            taxRate = 5;
        }
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
                    salePriceTextview.setText(valueWithComma);
                    break;
                case 2:
                    saleCommissionTextview.setText(valueWithComma);
                    break;
                case 3:
                    legalCostsTextview.setText(valueWithComma);
                    break;
                case 4:
                    renovationTextview.setText(valueWithComma);
                    break;
                case 11:
                    purchasePriceTextview.setText(valueWithComma);
                    break;
                case 12:
                    purchaseCommisionTextview.setText(valueWithComma);
                    break;
                case 13:
                    legalStampTextview.setText(valueWithComma);
                    break;
                case 14:
                    otherCostsTextview.setText(valueWithComma);
                    break;
            }
            float salePrice = DIHelper.toFloat(salePriceTextview.getText().toString());
            float saleCommision = DIHelper.toFloat(saleCommissionTextview.getText().toString());
            float legalCosts = DIHelper.toFloat(legalCostsTextview.getText().toString());
            float renovation = DIHelper.toFloat(renovationTextview.getText().toString());

            float purchasePrice = DIHelper.toFloat(purchasePriceTextview.getText().toString());
            float purchaseCommission = DIHelper.toFloat(purchaseCommisionTextview.getText().toString());
            float legalStamp = DIHelper.toFloat(legalStampTextview.getText().toString());
            float otherCosts = DIHelper.toFloat(otherCostsTextview.getText().toString());

            final float netDisposal = salePrice - saleCommision - legalCosts - renovation;
            final float netAcquisition = purchasePrice - purchaseCommission - legalStamp - otherCosts;
            final float gainLoss = netDisposal - netAcquisition;
            gainLossTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", gainLoss)));
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                public void run(){
                    //change adapter contents
                    netDisposalTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", netDisposal)));
                    netAcquisitionTextview.setText(DIHelper.addThousandsSeparator(String.format("%.2f", netAcquisition)));
                }
            }, 300);
        }
    }
}

package it.denning.navigation.add.bill;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.File;
import java.io.IOException;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.BillModel;
import it.denning.model.CodeDescription;
import it.denning.model.MatterCodeModel;
import it.denning.model.ReceiptModel;
import it.denning.model.TaxInvoiceCalcModel;
import it.denning.navigation.add.quotation.AddQuotationActivity;
import it.denning.navigation.add.quotation.AddQuotationAdapter;
import it.denning.navigation.add.receipt.AddReceiptActivity;
import it.denning.navigation.add.utils.billselection.TaxInvoiceSelectionActivity;
import it.denning.navigation.add.utils.listmatter.ListMatterActivity;
import it.denning.navigation.add.utils.simplematter.SimpleMatterActivity;
import it.denning.navigation.dashboard.section4.contacts.DashboardContactActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.network.services.DownloadService;
import it.denning.network.utils.Download;
import it.denning.network.utils.DownloadCompleteInterface;
import it.denning.network.utils.ProgressInterface;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddBillActivity extends MyBaseActivity implements OnSectionItemClickListener, ProgressInterface, DownloadCompleteInterface {
    AddBillAdapter adapter;
    BillModel billModel = new BillModel();
    private boolean hasCalculate = true;
    private boolean isCalcDone = false, isSaved = false;
    private String url;
    private File file = null;
    private int selectedSectionIndex, selectedItemIndex;

    public static void start(Context context, boolean hasCalculate, BillModel billModel) {
        Intent i = new Intent(context, AddBillActivity.class);
        i.putExtra("hasCalculate", hasCalculate);
        i.putExtra("billModel", billModel);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
        setupRecyclerView();
    }

    private void initFields() {
        toolbarTitle.setText(R.string.add_bill_title);
        hasCalculate = getIntent().getBooleanExtra("hasCalculate", true);
        billModel = (BillModel) getIntent().getSerializableExtra("billModel");
    }

    private void setupRecyclerView() {
        adapter = new AddBillAdapter(this, this, billModel,  hasCalculate);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedItemIndex = itemIndex;
        selectedSectionIndex = sectionIndex;
        switch (name) {
            case "Convert Quotation":
                break;
            case "File No.":
                gotoSimpleMatter();
                break;
            case "Matter":
                gotoMatterCode();
                break;
            case "Bill to":
               gotoBillTo();
                break;
            case "Preset Code":
                gotoPresetBill();
                break;
            case "Professional Fees":
            case "Disb. with DST":
            case "Disbursements":
            case "GST":
                gotoTaxSelection();
                break;

            // Button event
            case "Calculator":
                calculateTax();
                break;
            case "Issue Receipt":
                gotoReceipt();
                break;
            case "Save":
                saveBill();
                break;
            case "View":
                viewBill();
                break;
        }
//        findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    private void manageCalcResponse(JsonElement jsonElement) {
        hideProgress();
        isCalcDone = true;
        TaxInvoiceCalcModel calcModel = new Gson().fromJson(jsonElement, TaxInvoiceCalcModel.class);
        adapter.updateBelowData(calcModel);
        if (billModel == null) {
            billModel = new BillModel();
        }
        billModel.analysis = calcModel;
    }

    private boolean isPresetSelected() {
        if (!adapter.isPresetSelected()) {
            DIAlert.showSimpleAlert(this, R.string.alert_preset_not_select);
            return false;
        }

        return true;
    }

    private void calculateTax() {
        if (!isPresetSelected()) {
            return;
        }

        JsonObject params = adapter.buildCalcParams();
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.TAXINVOICE_CALCULATION_URL;
        NetworkManager.getInstance().sendPrivatePostRequest(url, params, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageCalcResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void gotoTaxSelection() {
        if (!isCalcDone) {
            return;
        }
        String title = "Quotation-" + adapter.getFileNo();
        TaxInvoiceSelectionActivity.start(this, title, selectedItemIndex, billModel);
    }

    private void gotoReceipt() {
        if (!isSaved) {
            DIAlert.showSimpleAlert(this, R.string.alert_should_save_bill);
            return;
        }

        showProgress();

        NetworkManager.getInstance().sendPrivatePostRequest(DIConstants.RECEIPT_FROM_TAXINVOICE, adapter.buildReceiptParams(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageReceiptResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageReceiptResponse(JsonElement jsonElement) {
        hideProgress();
        ReceiptModel receiptModel = new Gson().fromJson(jsonElement, ReceiptModel.class);
        AddReceiptActivity.start(this, receiptModel);
    }

    private void saveBill() {
//        findViewById(R.id.search_bank_layout).requestFocus();

        if (!isPresetSelected()) {
            return;
        }

        DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_bill, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                _save();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private void manageSaveResponse(JsonElement jsonElement) {
        hideProgress();
        isSaved = true;
        isCalcDone = true;
        // Disable save button
        BillModel billModel = new Gson().fromJson(jsonElement, BillModel.class);
        adapter.updateModelFromSave(billModel);
    }

    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.BILL_SAVE_URL;
        NetworkManager.getInstance().sendPrivatePostRequest(url, adapter.buildSaveParams(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSaveResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void viewBill() {
        if (!isSaved) {
            DIAlert.showSimpleAlert(this, R.string.alert_should_save_bill);
            return;
        }

        if (file != null) {
            openDocument();
            return;
        }

        url = DIConstants.REPORT_VIEWER_PDF_TAXINVOICE_URL + adapter.getBillNo();
        downloadFile();
    }

    private void gotoMatterCode() {
        if (adapter.isConvertQuotationEmpty()) {
            return;
        }
        Intent intent = new Intent(this, ListMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.MATTER_REQUEST_CODE);
    }

    private void gotoConvertQuotation() {

    }

    private void gotoSimpleMatter() {
        if (!adapter.isConvertQuotationEmpty()) {
            return;
        }
        Intent intent = new Intent(this, SimpleMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.SIMPLE_MATTER_REQUEST_CODE);
    }

    private void gotoBillTo() {
        if (adapter.isFileNoEmpty()) {
            return;
        }
        Intent intent = new Intent(this, DashboardContactActivity.class);
        intent.putExtra("hasCallback", true);
        intent.putExtra("api", DIConstants.GENERAL_CONTACT_URL);
        startActivityForResult(intent, DIConstants.CONTACT_REQUEST_CODE);
    }

    private void gotoPresetBill() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.preset_title);
        i.putExtra("url", DIConstants.PRESET_BILL_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.PRESET_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DIConstants.CONTACT_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                String name = data.getStringExtra("name");
                String code = data.getStringExtra("code");
                adapter.updateBillTo(name, code);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.MATTER_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updateMatterCode((MatterCodeModel)data.getSerializableExtra("model"));
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.SIMPLE_MATTER_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updateMatterSimple(DISharedPreferences.getInstance().selectedMatterSimple);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.PRESET_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updatePreset((CodeDescription) data.getSerializableExtra("model"));
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    public void downloadFile(){

        if(checkPermission()){
            startDownload();
        } else {
            requestPermission();
        }
    }

    private void startDownload(){
        DownloadService downloadService =  new DownloadService(this, url, this, this);
        downloadService.initDownload();
        showActionBarProgress();
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DIConstants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DIConstants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startDownload();
                } else {
                    showSnackbar(R.string.permission_denied, Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onProgress(final Download download) {
        hideActionBarProgress();
    }

    public void openDocument() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hideActionBarProgress();
                    new DIFileManager(AddBillActivity.this).openFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onComplete(final File file) {
        this.file = file;
        openDocument();
    }

    @Override
    public void onFailure(String error) {
        ErrorUtils.showError(this, error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideActionBarProgress();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveBill();
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }
}

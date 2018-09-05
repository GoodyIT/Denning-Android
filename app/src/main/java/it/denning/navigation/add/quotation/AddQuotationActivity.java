package it.denning.navigation.add.quotation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import it.denning.navigation.add.bill.AddBillActivity;
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

public class AddQuotationActivity extends MyBaseActivity implements OnSectionItemClickListener, ProgressInterface, DownloadCompleteInterface {
    AddQuotationAdapter adapter;
    BillModel billModel = new BillModel();
    private boolean isCalcDone = false;
    private boolean isSaved = false;
    private String url;
    private File file = null;
    private int selectedSectionIndex, selectedItemIndex;

    public static void start(Context context) {
        Intent i = new Intent(context, AddQuotationActivity.class);
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
        toolbarTitle.setText(R.string.add_quotation_title);
    }

    private void setupRecyclerView() {
        adapter = new AddQuotationAdapter(this, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedSectionIndex = sectionIndex;
        selectedItemIndex = itemIndex;
        switch (name) {
            case "File No.":
                gotoSimpleMatter();
                break;
            case "Matter":
                gotoMatterCode();
                break;
            case "Quotation to":
               gotoQuotationTo();
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
            case "Calculate":
                calculateTax();
                break;
            case "Issue Receipt":
                gotoReceipt();
                break;
            case "Save":
                saveQuotation();
                break;
            case "View":
                viewQuotation();
                break;
            case "Convert To Tax Invoice":
                convertToTaxInvoice();
                break;
        }

        findViewById(R.id.search_bank_layout).requestFocus();
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
        billModel.analysis = calcModel;
    }

    private boolean isPresetSelected() {
        if (!adapter.isPresetSelected()) {
            DIAlert.showSimpleAlert(this, R.string.alert_preset_not_select);
            return false;
        }

        return true;
    }

    private boolean isFileNoSelected() {
        if (!adapter.isFileNoSelected()) {
            DIAlert.showSimpleAlert(this, R.string.alert_file_no_not_select);
            return false;
        }

        return true;
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
            DIAlert.showSimpleAlert(this, R.string.alert_should_save_quotation);
            return;
        }

        showProgress();

        NetworkManager.getInstance().sendPrivatePostRequest(DIConstants.RECEIPT_FROM_QUOTATION, adapter.buildReceiptParams(), new CompositeCompletion() {
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

    private void calculateTax() {
        if (!isPresetSelected()) {
            return;
        }

        if (!isFileNoSelected()) {
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

    private void saveQuotation() {
        findViewById(R.id.search_bank_layout).requestFocus();

        if (isSaved) {
            return;
        }

        if (!isPresetSelected()) {
            return;
        }

        DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_quotation, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                _save();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private void manageReceiptResponse(JsonElement jsonElement) {
        hideProgress();
        ReceiptModel receiptModel = new Gson().fromJson(jsonElement, ReceiptModel.class);
        AddReceiptActivity.start(this, receiptModel);
    }

    private void manageSaveResponse(JsonElement jsonElement) {
        hideProgress();
        isSaved = true;
        isCalcDone = true;
        // Disable save button
        billModel = new Gson().fromJson(jsonElement, BillModel.class);
        adapter.updateModelFromSave(billModel);
    }

    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.QUOTATION_SAVE_URL;
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

    private void viewQuotation() {
        if (!isSaved) {
            DIAlert.showSimpleAlert(this, R.string.alert_should_save_quotation);
            return;
        }

        if (file != null) {
            openDocument();
            return;
        }

        url = DIConstants.REPORT_VIEWER_PDF_QUATION_URL + adapter.getQuotationNo();
        downloadFile();
    }


    private void manageBillResponse(JsonElement jsonElement) {
        hideProgress();
        // Disable save button
        billModel = new Gson().fromJson(jsonElement, BillModel.class);
        AddBillActivity.start(this, false, billModel);
    }

    private void convertToTaxInvoice() {
        if (!isSaved) {
            DIAlert.showSimpleAlert(this, R.string.alert_should_save_quotation);
            return;
        }

        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.INVOICE_FROM_QUOTATION;
        NetworkManager.getInstance().sendPrivatePostRequest(url, adapter.buildBillParams(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageBillResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void gotoMatterCode() {
        Intent intent = new Intent(this, ListMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.MATTER_REQUEST_CODE);
    }

    private void gotoSimpleMatter() {
        Intent intent = new Intent(this, SimpleMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.SIMPLE_MATTER_REQUEST_CODE);
    }

    private void gotoQuotationTo() {
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
                adapter.updateQuotationTo(name, code);
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
                    new DIFileManager(AddQuotationActivity.this).openFile(file);
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
            saveQuotation();
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }
}

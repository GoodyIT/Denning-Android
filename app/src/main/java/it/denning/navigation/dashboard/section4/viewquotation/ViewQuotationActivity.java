package it.denning.navigation.dashboard.section4.viewquotation;

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIFileManager;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.BillModel;
import it.denning.model.CodeDescription;
import it.denning.model.MasterTitle;
import it.denning.model.Mukim;
import it.denning.model.ProjectHousing;
import it.denning.model.Property;
import it.denning.model.ReceiptModel;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.add.bill.AddBillActivity;
import it.denning.navigation.add.property.AddPropertyAdapter;
import it.denning.navigation.add.quotation.AddQuotationActivity;
import it.denning.navigation.add.receipt.AddReceiptActivity;
import it.denning.navigation.add.utils.billselection.TaxInvoiceSelectionActivity;
import it.denning.navigation.add.utils.mattertitle.MasterTitleListActivity;
import it.denning.navigation.add.utils.projecthousing.ProjectHousingListActivity;
import it.denning.navigation.add.utils.simplespinerdialog.SimpleSpinnerDialog;
import it.denning.navigation.add.utils.simplespinerdialog.SpinnerDialog;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.network.services.DownloadService;
import it.denning.network.utils.Download;
import it.denning.network.utils.DownloadCompleteInterface;
import it.denning.network.utils.ProgressInterface;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.OnSpinerItemClick;
import it.denning.search.utils.desc.GeneralDescActivity;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

import static it.denning.general.DIConstants.PROPERTY_TYPE_GET_LIST_URL;

/**
 * Created by denningit on 2018-01-16.
 */

public class ViewQuotationActivity extends MyBaseActivity implements
        OnSectionItemClickListener,
        ProgressInterface,
        DownloadCompleteInterface {

    ViewQuotationAdapter adapter;
    public BillModel billModel;
    private int selectedItemIndex;
    private String url;
    private File file = null;

    public static void start(Context context, BillModel model) {
        Intent i = new Intent(context, ViewQuotationActivity.class);
        i.putExtra("model", model);
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
        toolbarTitle.setText(R.string.update_property_title);

        billModel = (BillModel) getIntent().getSerializableExtra("model");
    }

    private void setupRecyclerView() {
        adapter = new ViewQuotationAdapter(this, billModel, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedItemIndex = itemIndex;
        switch (name) {
            case "Professional Fees":
            case "Disb. with GST":
            case "Disbursements":
            case "GST":
                gotoTaxSelection();
                break;

            case "Issue Receipt":
                gotoReceipt();
                break;
            case "View Document":
                viewQuotation();
                break;
            case "Convert to Tax Invoice":
                convertToTaxInvoice();
                break;
        }

        findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void manageReceiptResponse(JsonElement jsonElement) {
        hideProgress();
        ReceiptModel receiptModel = new Gson().fromJson(jsonElement, ReceiptModel.class);
        AddReceiptActivity.start(this, receiptModel);
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    private void gotoReceipt() {

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

    private void viewQuotation() {

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


    private void gotoTaxSelection() {
        String title = "Quotation-" + adapter.getFileNo();
        TaxInvoiceSelectionActivity.start(this, title, selectedItemIndex, billModel);
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
                    new DIFileManager(ViewQuotationActivity.this).openFile(file);
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
}

package it.denning.navigation.add.receipt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.BillModel;
import it.denning.model.CodeDescription;
import it.denning.model.ReceiptModel;
import it.denning.model.TaxInvoiceModel;
import it.denning.navigation.add.utils.simplespinerdialog.SpinnerDialog;
import it.denning.navigation.add.utils.simplematter.SimpleMatterActivity;
import it.denning.navigation.dashboard.section4.contacts.DashboardContactActivity;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.OnSpinerItemClick;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddReceiptActivity extends MyBaseActivity implements OnSectionItemClickListener {
    AddReceiptAdapter adapter;
    BillModel billModel;
    ReceiptModel receiptModel = new ReceiptModel();
    public boolean isUpdate = false;
    private boolean isSaved = false;


    public static void start(Context context, ReceiptModel model) {
        Intent i = new Intent(context, AddReceiptActivity.class);
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
        toolbarTitle.setText(R.string.add_receipt_title);
        receiptModel = (ReceiptModel) getIntent().getSerializableExtra("model");
        if (receiptModel == null) {
            isUpdate = false;
            receiptModel = new ReceiptModel();
        } else {
            isUpdate = true;
        }
    }

    private void setupRecyclerView() {
        adapter = new AddReceiptAdapter(this, receiptModel, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        switch (name) {
            case "File No. (System)":
                gotoSimpleMatter();
                break;

            case "Bill No.":
                gotoBillNo();
                break;

            case "Account Type":
                gotoAccountType();
                break;

            case "Received From":
                gotoReceivedFrom();
                break;

            case "Transaction":
                gotoTransaction();
                break;

            case "Mode":
                gotoMode();
                break;

            case "Issuer Bank":
                gotoIssuerBank();
                break;

            case "Bank Branch":
                gotoBankBranch();
                break;
        }

        findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    private void saveReceipt() {
        findViewById(R.id.search_bank_layout).requestFocus();

        if (isSaved) {
            return;
        }

        if (!adapter.isValidProceed()) {
            return;
        }
        if (isUpdate) {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_update_receipt, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    _update();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        } else {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_receipt, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    _save();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        }
    }

    private void manageSaveResponse(JsonElement jsonElement) {
        hideProgress();
        isSaved = true;
        // Disable save button
        ErrorUtils.showError(this, "Successfully Saved");
    }

    private void _update() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.RECEIPT_UPDATE_URL;
        NetworkManager.getInstance().sendPrivatePutRequest(url, adapter.buildSaveParams(), new CompositeCompletion() {
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


    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.RECEIPT_SAVE_URL;
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

    private void gotoBillNo() {
        Intent intent = new Intent(this, TaxInvoiceActivity.class);
        intent.putExtra("hasCallback", true);
        intent.putExtra("api", DIConstants.TAXINVOICE_ALL_GET_URL);
        intent.putExtra("fileNo", adapter.getFileNo());
        startActivityForResult(intent, DIConstants.TAX_REQUEST_CODE);
    }

    private void gotoAccountType() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.account_type_title);
        i.putExtra("url", DIConstants.ACCOUNT_TYPE_GET_LIST_URL);
        i.putExtra("code", "ID");
        i.putExtra("value", "shortDesc");
        startActivityForResult(i, DIConstants.ACCOUNT_TYPE_REQUEST_CODE);
    }

    private void gotoReceivedFrom() {
        Intent intent = new Intent(this, DashboardContactActivity.class);
        intent.putExtra("hasCallback", true);
        intent.putExtra("api", DIConstants.GENERAL_CONTACT_URL);
        startActivityForResult(intent, DIConstants.CONTACT_REQUEST_CODE);
    }

    private void gotoTransaction() {
        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.TRANSACTION_DESCRIPTION_RECEIPT_GET, "strTransactionDescription", R.string.transaction_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateTransaction(item);
            }

            @Override
            public void onClick(JsonObject object) {

            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoMode() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.mode_title);
        i.putExtra("url", DIConstants.PAYMENT_MODE_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "strDescription");
        startActivityForResult(i, DIConstants.PAYMENT_MODE_REQUEST_CODE);
    }

    private void gotoBankBranch() {
        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.BANK_BRANCH_GET_LIST_URL, "name", R.string.branch_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateBankBranch(item);
            }

            @Override
            public void onClick(JsonObject object) {

            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoIssuerBank() {
        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.ACCOUNT_CHEQUE_ISSUEER_GET_URL, "description", R.string.issuer_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateBankIssuer(item);
            }

            @Override
            public void onClick(JsonObject object) {

            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoSimpleMatter() {
        Intent intent = new Intent(this, SimpleMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.SIMPLE_MATTER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIConstants.CONTACT_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                String name = data.getStringExtra("name");
                String code = data.getStringExtra("code");
                adapter.updateReceivedFrom(name, code);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.ACCOUNT_TYPE_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateAccountType(accountType);
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
        } else if (requestCode == DIConstants.PAYMENT_MODE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updateMode((CodeDescription) data.getSerializableExtra("model"));
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.TAX_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updateBillNo((TaxInvoiceModel) data.getSerializableExtra("model"));
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isUpdate) {
            getMenuInflater().inflate(R.menu.update, menu);
        } else {
            getMenuInflater().inflate(R.menu.save, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save || id == R.id.action_update) {
            saveReceipt();
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }
}

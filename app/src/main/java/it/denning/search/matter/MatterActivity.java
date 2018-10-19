package it.denning.search.matter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.model.*;
import it.denning.navigation.add.matter.AddMatterActivity;
import it.denning.navigation.add.property.AddPropertyActivity;
import it.denning.navigation.home.upload.UploadActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.MatterCode.MatterCodeActivity;
import it.denning.search.accounts.AccountsActivity;
import it.denning.search.bank.BankActivity;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.document.DocumentActivity;
import it.denning.search.filenote.FileNoteActivity;
import it.denning.search.legal_firm.LegalFirmActivity;
import it.denning.search.paymentrecord.PaymentRecordActivity;
import it.denning.search.template.TemplateActivity;
import it.denning.search.utils.*;
import it.denning.ui.activities.base.MyBaseActivity;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

/**
 * Created by denningit on 27/04/2017.
 */

public class MatterActivity extends MyBaseActivity implements OnMatterCodeClickListener,
        OnDetailItemClickListener,
        OnFileFolderClickListener,
        OnAccountsClickListener,
        OnFileNoteClickListener,
        OnPaymentRecordClickListener,
        OnTemplateClickListener,
        OnUploadClickListener {
    private  MatterModel relatedMatter;

    public static void start(Context context, MatterModel relatedMatter) {
        Intent i = new Intent(context, MatterActivity.class);
        i.putExtra("relatedMatter", relatedMatter);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.matter_title));
        relatedMatter = (MatterModel) getIntent().getSerializableExtra("relatedMatter");

        if (relatedMatter != null) {
            setupRecyclerView(relatedMatter);
        }
    }

    private void setupRecyclerView(MatterModel relatedMatter) {
        MatterAdapter matterAdapter = new MatterAdapter(relatedMatter, this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        matterAdapter.setClickListeners(this, this, this, this, this, this, this, this);

        recyclerView.setAdapter(matterAdapter);
    }

    public void onEditMatter() {
        Intent intent = new Intent(this, AddMatterActivity.class);
        intent.putExtra("model", relatedMatter);
        startActivityForResult(intent, DIConstants.MATTER_REQUEST_CODE);
    }

    @Override
    public void onFileFolderClick(View view, String code, String type) {
        openFileFolder(code);
    }

    @Override
    public void onAccountsClick(View view, String code) {
        openAccounts(code);
    }

    @Override
    public void onFileNoteClick(View view, String code, String title) {
        FileNoteActivity.start(this, code, title);
    }

    @Override
    public void onPaymentRecordClick(View view, String code) {
        PaymentRecordActivity.start(this, code);
    }

    @Override
    public void onClick(View view, String type, String code) {
        showActionBarProgress();
        switch (type) {
            case "contact":
                gotoContact(code);
                break;
            case "bank":
                gotoBank(code);
                break;
            case "property":
                gotoProperty(code);
                break;
            case "solicitor":
                gotoSolicitor(code);
                break;

            default:
                break;
        }
    }

    void openFileFolder(String code) {
        String url= DIConstants.MATTER_GET_URL + code + "/fileFolder";

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                DISharedPreferences.getInstance().documentModel = new Gson().fromJson(jsonElement, DocumentModel.class);
                DocumentActivity.start(MatterActivity.this, "File Folder");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(MatterActivity.this, error);
            }
        });
    }

    @Override
    public void onMatterClick(View view, MatterCodeModel matterCodeModel) {
        MatterCodeActivity.start(this, matterCodeModel);
    }

    void openAccounts(String code) {
        showActionBarProgress();
        String url = "v1/" + code + "/fileLedger";
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                DISharedPreferences.accounts = new Gson().fromJson(jsonElement, Accounts.class);
                AccountsActivity.start(MatterActivity.this);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void gotoContact(String code) {
        String url = DIConstants.CONTACT_GET_URL + code;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.contact = new Gson().fromJson(jsonElement.getAsJsonObject(), Contact.class);
                SearchContactActivity.start(MatterActivity.this, "");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });

    }

    void gotoBank(String code) {
        String url = DIConstants.BANK_GET_GET_URL + code;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.bank = new Gson().fromJson(jsonElement.getAsJsonObject(), Bank.class);
                BankActivity.start(MatterActivity.this, R.string.bank_title);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void gotoProperty(String code) {
        String url = DIConstants.PROPERTY_GET_URL + code;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                Property property = new Gson().fromJson(jsonElement.getAsJsonObject(), Property.class);
                AddPropertyActivity.start(MatterActivity.this, property);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    void gotoSolicitor(String code) {
        String url = DIConstants.SOLICITOR_GET_URL + code;
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                DISharedPreferences.legalFirm = new Gson().fromJson(jsonElement.getAsJsonObject(), LegalFirm.class);
                LegalFirmActivity.start(MatterActivity.this);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIConstants.MATTER_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                relatedMatter = (MatterModel) data.getSerializableExtra("model");
            }
        }
    }

    @Override
    public void onTemplateClick(View view, String code, String title) {
        TemplateActivity.start(this, code, title);
    }

    @Override
    public void onUploadClick(View view, String code, int title, String url, String defaultFileName) {
        UploadActivity.start(this, code, title, url, defaultFileName);
    }
}

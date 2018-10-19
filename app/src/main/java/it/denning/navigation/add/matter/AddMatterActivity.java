package it.denning.navigation.add.matter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.BankBranchModel;
import it.denning.model.CaseType;
import it.denning.model.CodeDescription;
import it.denning.model.Contact;
import it.denning.model.Coram;
import it.denning.model.CourtDiaryCourt;
import it.denning.model.FullPropertyModel;
import it.denning.model.LabelValueDetail;
import it.denning.model.MatterCodeModel;
import it.denning.model.MatterModel;
import it.denning.model.Property;
import it.denning.model.StaffModel;
import it.denning.navigation.add.property.AddPropertyActivity;
import it.denning.navigation.add.utils.branchlist.BranchListActivity;
import it.denning.navigation.add.utils.casetypelist.CaseTypeListActivity;
import it.denning.navigation.add.utils.contactlist.ContactListActivity;
import it.denning.navigation.add.utils.coramlist.CoramListActivity;
import it.denning.navigation.add.utils.courtdiarycourtlist.CourtDiaryCourtListActivity;
import it.denning.navigation.add.utils.listmatter.ListMatterActivity;
import it.denning.navigation.add.utils.propertylist.PropertyListActivity;
import it.denning.navigation.add.utils.solicitorlist.SolicitorListActivity;
import it.denning.navigation.add.utils.stafflist.StaffListActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddMatterActivity extends MyBaseActivity implements
        OnSectionItemClickListener,
        DatePickerDialog.OnDateSetListener {

    @BindView(R.id.button_edit)
    FloatingActionButton floatingActionButton;

    AddMatterAdapter adapter;
    public MatterModel matter;
    public boolean isSaved = false;
    private boolean isUpdateMode = false;
    private DatePickerDialog dpd;
    private int selectedSection, selectedItem;

    public static void start(Context context, MatterModel model) {
        Intent i = new Intent(context, AddMatterActivity.class);
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
        matter = (MatterModel) getIntent().getSerializableExtra("model");
        isUpdateMode = matter != null;
        updateTitle();
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    private void updateTitle() {
        if (isUpdateMode) {
            toolbarTitle.setText(R.string.matter_update_title);
        } else {
            toolbarTitle.setText(R.string.matter_save_title);
        }
    }

    private void setupRecyclerView() {
        adapter = new AddMatterAdapter(this, this, isUpdateMode);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
        if (isUpdateMode) {
            adapter.adjustModelForUpdate(matter);
        }
    }


    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedSection = sectionIndex;
        selectedItem = itemIndex;
        switch (name) {
            case "Save":
            case "Update":
                saveMatter();
                break;
            case "Primary Client":
                gotoPrimaryClient();
                break;
            case "File Status":
                gotoFileStatus();
                break;
            case "Partner-in-Charge":
                gotoStaff(R.string.select_clerk_title, DIConstants.STAFF_REQUEST_CODE, "partner");
                break;
            case "LA-in-Charge":
                gotoStaff(R.string.select_clerk_title, DIConstants.STAFF_REQUEST_CODE, "la");
                break;
            case "Clerk-in-Charge":
                gotoStaff(R.string.select_clerk_title, DIConstants.STAFF_REQUEST_CODE, "clerk");
                break;
            case "Matter":
                gotoMatter();
                break;
            case "Branch":
                gotoBranch();
                break;
            case "Case Type":
                gotoCourtCaseType();
                break;
            case "Court":
            case "Place":
                gotoCourtDiaryCourt();
                break;
            case "Judge":
            case "SAR":
                gotoCourtCoram();
                break;
            case "Add Party":
                addParty();
                break;
            case "Load Party":
                loadParty();
                break;
            case "Delete Party":
                break;
            case "Add Property":
                addProperty();
                break;
            case "Load Property":
                loadProperty();
                break;
            case "Bank":
                addBank();
                break;
            case "Solicitor":
                addSolicitor();
                break;
            case "Selection":
                addCalendar();
                break;
        }

        findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    @OnClick(R.id.button_edit)
    void saveMatter() {
//        findViewById(R.id.search_bank_layout).requestFocus();

//        if (isSaved) {
//            return;
//        }

        if (!adapter.isValidProceed()) {
            return;
        }

        if (isUpdateMode) {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_update_matter, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    _update();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        } else {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_matter, new MyCallbackInterface() {
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
        isUpdateMode = true;
        // Disable save button
        matter = new Gson().fromJson(jsonElement, MatterModel.class);
        ErrorUtils.showError(this, "Successfully Saved");
        updateTitle();
        adapter.adjustModelForUpdate(matter);
    }

    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.MATTER_SAVE_URL;
        NetworkManager.getInstance().sendPrivatePostRequest(url, adapter.buildSaveParam(), new CompositeCompletion() {
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

    private void manageUpdateResponse(JsonElement jsonElement) {
        hideProgress();
        isSaved = true;
        // Disable save button
        matter = new Gson().fromJson(jsonElement, MatterModel.class);
        ErrorUtils.showError(this, "Successfully Updated");
    }

    void _update() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.MATTER_SAVE_URL;
        NetworkManager.getInstance().sendPrivatePutRequest(url, adapter.buildUpdateParam(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageUpdateResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void gotoPrimaryClient() {
        Intent intent = new Intent(this, ContactListActivity.class);
        intent.putExtra("title", R.string.select_contact_title);
        startActivityForResult(intent, DIConstants.CONTACT_REQUEST_CODE);
    }

    private void gotoStaff(int resTitle, int requestCode, String type) {
        Intent intent = new Intent(this, StaffListActivity.class);
        intent.putExtra("title", resTitle);
        intent.putExtra("type", type);
        startActivityForResult(intent, requestCode);
    }

    private void gotoMatter() {
        Intent intent = new Intent(this, ListMatterActivity.class);
        intent.putExtra("title", R.string.select_matter_title);
        startActivityForResult(intent, DIConstants.MATTER_REQUEST_CODE);
    }

    private void gotoFileStatus() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.select_file_status_title);
        i.putExtra("url", DIConstants.MATTER_FILE_STATUS_GET_LIST_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.FILE_STATUS_RQUEST_CODE);
    }

    private void gotoBranch() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.branch_title);
        i.putExtra("url", DIConstants.MATTER_BRANCH_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "strCity");
        startActivityForResult(i, DIConstants.MATTER_BRANCH_REQUEST_CODE);
    }

    private void gotoCourtDiaryCourt() {
        Intent intent = new Intent(this, CourtDiaryCourtListActivity.class);
        startActivityForResult(intent, DIConstants.COURT_DIARY_COURT_REQUEST_CODE);
    }

    private void gotoCourtCaseType() {
        Intent intent = new Intent(this, CaseTypeListActivity.class);
        startActivityForResult(intent, DIConstants.COURT_CASE_TYPE_REQUEST_CODE);
    }

    private void gotoCourtCoram() {
        Intent intent = new Intent(this, CoramListActivity.class);
        startActivityForResult(intent, DIConstants.COURT_CORAM_REQUEST_CODE);
    }

    private void addParty() {
        Intent intent = new Intent(this, ContactListActivity.class);
        intent.putExtra("title", R.string.select_contact_title);
        startActivityForResult(intent, DIConstants.PARTY_ADD_REQUEST_CODE);
    }

    private void loadParty() {
        String code = adapter.getCode(selectedSection, selectedItem);
        String url = DIConstants.CONTACT_GET_URL + code;
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                DISharedPreferences.contact = new Gson().fromJson(jsonElement, Contact.class);
                SearchContactActivity.start(AddMatterActivity.this, "");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void addProperty() {
        Intent intent = new Intent(this, PropertyListActivity.class);
        intent.putExtra("title", R.string.select_property_title);
        startActivityForResult(intent, DIConstants.PROPERTY_ADD_REQUEST_CODE);
    }

    private void loadProperty() {
        String code = adapter.getCode(selectedSection, selectedItem);
        String url = DIConstants.PROPERTY_GET_URL + code;
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                Property property = new Gson().fromJson(jsonElement, Property.class);
                AddPropertyActivity.start(AddMatterActivity.this, property);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void addBank() {
        Intent intent = new Intent(this, BranchListActivity.class);
        intent.putExtra("title", R.string.select_bank_branch_title);
        startActivityForResult(intent, DIConstants.BANK_ADD_REQUEST_CODE);
    }

    private void addSolicitor() {
        Intent intent = new Intent(this, SolicitorListActivity.class);
        intent.putExtra("title", R.string.select_solicitor_title);
        startActivityForResult(intent, DIConstants.SOLICITOR_ADD_REQUEST_CODE);
    }

    private void addCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date testDate = null;
        String date = adapter.getAddingModel().items.get(selectedSection).items.get(selectedItem).value;
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
        dpd = DatePickerDialog.newInstance(
                this,
                calendar
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = DIHelper.getBirthday(datePickerDialog.getSelectedDay());
        adapter.updateDataAndRefresh(date, selectedSection, selectedItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIConstants.CONTACT_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                StaffModel model = (StaffModel) data.getSerializableExtra("model");
                adapter.updateCodeDescData(new CodeDescription(model.code, model.name), adapter.MATTER_INFO, adapter.PRIMARY_CLIENT);
            }
        } else if (requestCode == DIConstants.FILE_STATUS_RQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription model = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(model, adapter.MATTER_INFO, adapter.FILE_STATUS);
            }
        } else if (requestCode == DIConstants.STAFF_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                StaffModel model = (StaffModel) data.getSerializableExtra("model");
                adapter.updateCodeDescData(new CodeDescription(model.code, model.name), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.MATTER_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                MatterCodeModel model = (MatterCodeModel)data.getSerializableExtra("model");
                adapter.updateCodeDescData(new CodeDescription(model.code, model.description), adapter.MATTER_INFO, adapter.MATTER);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.MATTER_BRANCH_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription model = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(model, adapter.MATTER_INFO, adapter.BRANCH);
            }
        } else if (requestCode == DIConstants.PARTY_ADD_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                StaffModel model = (StaffModel) data.getSerializableExtra("model");
                adapter.updateParty(new LabelValueDetail("", model.name, model.code, DIConstants.ONE_TYPE), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.PROPERTY_ADD_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                FullPropertyModel model = (FullPropertyModel) data.getSerializableExtra("model");
                adapter.updateProperty(new LabelValueDetail(model.fullTitle, model.code), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.BANK_ADD_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                BankBranchModel model = (BankBranchModel) data.getSerializableExtra("model");
                adapter.updateBankOrSolicitor(new LabelValueDetail(model.HQ.name, model.code), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.SOLICITOR_ADD_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                StaffModel model = (StaffModel) data.getSerializableExtra("model");
                adapter.updateBankOrSolicitor(new LabelValueDetail(model.name, model.code), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.COURT_CASE_TYPE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CaseType model = (CaseType) data.getSerializableExtra("model");
                adapter.updateItem(new LabelValueDetail(model.strBahasa, model.code), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.COURT_CORAM_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                Coram model = (Coram) data.getSerializableExtra("model");
                adapter.updateItem(new LabelValueDetail(model.name, model.code), selectedSection, selectedItem);
            }
        } else if (requestCode == DIConstants.COURT_DIARY_COURT_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CourtDiaryCourt model = (CourtDiaryCourt) data.getSerializableExtra("model");
                adapter.updateItem(new LabelValueDetail(model.typeE, model.code), selectedSection, adapter.COURT);
                adapter.updateItem(new LabelValueDetail(model.place, model.code), selectedSection, adapter.PLACE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isUpdateMode) {
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
            saveMatter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onBack() {
        Intent intent = new Intent();
        intent.putExtra("model", adapter.getModel());
        setResult(Activity.RESULT_OK, intent);
        super.onBack();
    }
}

package it.denning.navigation.add.property;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.Calendar;

import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.CodeDescription;
import it.denning.model.MasterTitle;
import it.denning.model.Mukim;
import it.denning.model.ProjectHousing;
import it.denning.model.Property;
import it.denning.navigation.add.utils.mattertitle.MasterTitleListActivity;
import it.denning.navigation.add.utils.projecthousing.ProjectHousingListActivity;
import it.denning.navigation.add.utils.simplespinerdialog.SimpleSpinnerDialog;
import it.denning.navigation.add.utils.simplespinerdialog.SpinnerDialog;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.OnSpinerItemClick;
import it.denning.search.utils.desc.GeneralDescActivity;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

import static it.denning.general.DIConstants.PROPERTY_TYPE_GET_LIST_URL;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddPropertyActivity extends MyBaseActivity implements
        OnSectionItemClickListener,
        DatePickerDialog.OnDateSetListener {
    AddPropertyAdapter adapter;
    public Property property;
    public boolean isSaved = false;
    private boolean isUpdateMode = false;
    private DatePickerDialog dpd;
    private int selectedSectionIndex, selectedItemIndex, twoColumn;

    public static void start(Context context, Property model) {
        Intent i = new Intent(context, AddPropertyActivity.class);
        i.putExtra("model", model);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        property = (Property) getIntent().getSerializableExtra("model");
        isUpdateMode = property != null;
        if (isUpdateMode) {
            toolbarTitle.setText(R.string.update_property_title);
        } else {
            toolbarTitle.setText(R.string.add_property_title);
        }
    }

    private void setupRecyclerView() {
        adapter = new AddPropertyAdapter(this, property, this);
//        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
//        recyclerView.setLayoutManager(searchLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedSectionIndex = sectionIndex;
        selectedItemIndex = itemIndex;
        twoColumn = -1; // 0: left, 1: right, -1:nothing
        switch (name) {
            case "Property Type":
                gotoCodeDesc(R.string.select_property_type_title, PROPERTY_TYPE_GET_LIST_URL);
                break;
            case "Individual / Sarata Title":
                gotoCodeDesc(R.string.issued_title_of_property_title, DIConstants.PROPERTY_TITLE_ISSUED_GET_URL);
                break;
            case "Title Type":
                twoColumn = 0;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_TITLE_TYPE_GET_URL, R.string.title_type_title);
                break;
            case "Lot Type":
                twoColumn = 0;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_LOT_TYPE_GET_URL, R.string.lot_type_title);
                break;
            case "Mukim Type":
                twoColumn = 0;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_MUKIM_TYPE_GET_URL, R.string.mukim_type);
                break;
            case "Mukim":
                gotoMukim();
                break;
            case "Area Type":
                twoColumn = 1;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_AREA_TYPE_GET_URL, R.string.select_area_type_title);
                break;
            case "Tenure":
                gotoDesc(R.string.tenure_type_title, DIConstants.PROPERTY_TENURE_TYPE_GET_URL);
                break;
            case "Lease Expiry Date":
                showDateOfBirth();
                break;
            case "Restriction in Interest":
                gotoCodeDesc(R.string.restriction_in_interest_title, DIConstants.PROPERTY_RESTRICTION_GET_URL);
                break;
            case "Restriction Against":
                gotoSimpleAutoComplete(DIConstants.PROPERTY_RESTRICTION_AGAINST_GET_URL, R.string.restriction_against_title);
                break;
            case "Approving Authority":
                gotoDetailAutoComplete(DIConstants.PROPERTY_APPROVING_AUTHORITY_GET_URL, "description", R.string.restriction_against_title);
                break;
            case "Category of Land Use":
                gotoSimpleAutoComplete(DIConstants.PROPERTY_LANDUSE_GET_URL, R.string.category_of_land_use);
                break;
            case "Parcel Type":
                twoColumn = 0;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_PARCEL_TYPE_GETLIST_URL, R.string.parcel_type_title);
                break;
            case "Measurement Unit":
                twoColumn = 1;
                gotoSimpleAutoComplete(DIConstants.PROPERTY_AREA_TYPE_GET_URL, R.string.measurement_type_of_property);
                break;
            case "Project Name":
                gotoProjectHousing();
                break;
            case "Block/Master Title":
                gotoMasterTitle();
                break;
        }

//        findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void manageError(String error) {
        hideProgress();
        ErrorUtils.showError(this, error);
    }

    private void saveContact() {
//        findViewById(R.id.search_bank_layout).requestFocus();

//        if (isSaved) {
//            return;
//        }

        if (!adapter.isValidProceed()) {
            return;
        }

        if (isUpdateMode) {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_update_property, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    _update();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        } else {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_property, new MyCallbackInterface() {
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
        Property property = new Gson().fromJson(jsonElement, Property.class);
        ErrorUtils.showError(this, "Successfully Saved");
        adapter.updateID(property.propertyID);
    }

    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.PROPERTY_SAVE_URL;
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
        property = new Gson().fromJson(jsonElement, Property.class);
        ErrorUtils.showError(this, "Successfully Updated");
    }

    private void _update() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.PROPERTY_SAVE_URL;
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

    private void gotoMasterTitle() {
        Intent i = new Intent(this, MasterTitleListActivity.class);
        startActivityForResult(i, DIConstants.MASTER_TITLE_REQUEST_CODE);
    }

    private void gotoProjectHousing() {
        Intent i = new Intent(this, ProjectHousingListActivity.class);
        startActivityForResult(i, DIConstants.PROJECT_HOUSING_REQUEST_CODE);
    }

    private void gotoMukim() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.select_mukim_value);
        i.putExtra("value", "mukim");
        i.putExtra("url", DIConstants.PROPERTY_MUKIM_GET_LIST_URL);
        i.putExtra("isMukim", true);
        startActivityForResult(i, DIConstants.MUKIM_REQUEST_CODE);
    }

    private void gotoAreaType() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.select_area_type_title);
        i.putExtra("value", "description");
        i.putExtra("code", "code");
        i.putExtra("url", DIConstants.PROPERTY_AREA_TYPE_GET_URL);
        startActivityForResult(i, DIConstants.AREA_TYPE_REQUEST_CODE);
    }

    private void gotoCodeDesc(int resTitle, String url) {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", resTitle);
        i.putExtra("url", url);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.CODE_DESC_REQUEST_CODE);
    }

    protected void gotoDesc(int resTitle, String url) {
        Intent i = new Intent(this, GeneralDescActivity.class);
        i.putExtra("title", resTitle);
        i.putExtra("url", url);
        startActivityForResult(i, DIConstants.DESC_REQUEST_CODE);
    }

    private void gotoDetailAutoComplete(String url, String key, int resTitle) {
        SpinnerDialog dialog = new SpinnerDialog(this, url, key, resTitle);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateDataAndRefresh(item, selectedSectionIndex, selectedItemIndex);
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoSimpleAutoComplete(String url, int resTitle) {
        SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(this, url, resTitle);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (twoColumn == -1) {
                    adapter.updateDataAndRefresh(item, selectedSectionIndex, selectedItemIndex);
                } else {
                    adapter.updateLeftRightInput(item, selectedSectionIndex, selectedItemIndex, twoColumn);
                }
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void showDateOfBirth() {
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now
            );
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = DIHelper.getBirthday(datePickerDialog.getSelectedDay());
        adapter.updateDataAndRefresh(date, selectedSectionIndex, selectedItemIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIConstants.CODE_DESC_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                if (twoColumn == -1) {
                    adapter.updateCodeDescData(accountType, selectedSectionIndex, selectedItemIndex);
                } else {
                    adapter.updateLeftRightInput(accountType.description, selectedSectionIndex, selectedItemIndex, twoColumn);
                }
            }
        } else if (requestCode == DIConstants.AREA_TYPE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                String desc = data.getStringExtra("desc");
                adapter.updateLeftRightInput(desc, selectedSectionIndex, selectedItemIndex, 1);
            }
        } else if (requestCode == DIConstants.DESC_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                String desc = data.getStringExtra("desc");
                adapter.updateDataAndRefresh(desc, selectedSectionIndex, selectedItemIndex);
            }
        } else if (requestCode == DIConstants.MUKIM_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Mukim mukim = (Mukim) data.getSerializableExtra("model");
                adapter.updateMukim(mukim, selectedSectionIndex);
            }
        } else if (requestCode == DIConstants.PROJECT_HOUSING_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                ProjectHousing projectHousing = (ProjectHousing) data.getSerializableExtra("model");
                adapter.updateProjectHousing(projectHousing, selectedSectionIndex);
            }
        } else if (requestCode == DIConstants.MASTER_TITLE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                MasterTitle masterTitle = (MasterTitle) data.getSerializableExtra("model");
                adapter.updateDataAndRefresh(masterTitle.fullTitle, selectedSectionIndex, selectedItemIndex);
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
            saveContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

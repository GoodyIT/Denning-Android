package it.denning.navigation.add.contact;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.DatePicker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;
import it.denning.MainActivity;
import it.denning.general.*;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.denning.R;
import it.denning.model.CodeDescription;
import it.denning.model.Contact;
import it.denning.navigation.add.utils.simplespinerdialog.SimpleSpinnerDialog;
import it.denning.navigation.add.utils.simplespinerdialog.SpinnerDialog;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.contact.SearchContactActivity;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.OnSpinerItemClick;
import it.denning.search.utils.generallist.GeneralListActivity;
import it.denning.ui.activities.base.MyBaseActivity;

/**
 * Created by denningit on 2018-01-16.
 */

public class AddContactActivity extends MyBaseActivity implements
        OnSectionItemClickListener{
    AddContactAdapter adapter;
    Contact contact;
    public boolean isSaved = false, isIDDuplicated = false, isNameDuplicated = false, isOldIDDuplicated = false;
    private boolean isUpdateMode = false;
    private DatePickerDialog dpd;
    private int selectedSection, selectedItem;

    public static void start(Context context) {
        Intent i = new Intent(context, AddContactActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        setupRecyclerView();
    }

    private void initFields() {
        contact =  DISharedPreferences.contact;
        isUpdateMode = contact != null;
        if (isUpdateMode) {
            toolbarTitle.setText(R.string.update_contact_title);
        } else {
            toolbarTitle.setText(R.string.add_contact_title);
        }
    }

    private void setupRecyclerView() {
        adapter = new AddContactAdapter(this, this, contact);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedSection = sectionIndex;
        selectedItem = itemIndex;
        switch (name) {
            case "ID Type *":
                gotoIDType();
                break;
            case "Title":
                gotoTitle();
                break;
            case "Postcode":
                gotoPostCode();
                break;
            case "Town":
                gotoTown();
                break;
            case "State":
                gotoState();
                break;
            case "Country":
                gotoCountry();
                break;
            case "Citizenship / Country of Incorp":
            case "Country of Incorporation":
                gotoCitizenship();
                break;
            case "Date of Birth":
                showDateOfBirth();
                break;
            case "Occupation":
                gotoOccupation();
                break;
            case "IRD Branch":
                gotoIRDBranch();
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

        if (isSaved) {
            return;
        }

        if (DISharedPreferences.isDuplicationChecking) {
            return;
        }

//        if (isNameDuplicated) {
//            DIAlert.showSimpleAlert(this, R.string.alert_Name_duplicate);
//            return;
//        }

        if (isOldIDDuplicated) {
            DIAlert.showSimpleAlert(this, R.string.alert_Old_ID_duplcate);
            return;
        }

        if (DISharedPreferences.isIDDuplicated) {
            DIAlert.showSimpleAlert(this, R.string.alert_ID_duplicate);
            return;
        }

        if (!adapter.isValidProceed()) {
            return;
        }

        if (isUpdateMode) {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_update_contact, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    _update();
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        } else {
            DIAlert.showSimpleAlertWithCompletion(this, R.string.alert_save_contact, new MyCallbackInterface() {
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
        DISharedPreferences.contact = new Gson().fromJson(jsonElement, Contact.class);
        ErrorUtils.showError(this, "Successfully Saved");
        SearchContactActivity.start(this, "");
    }

    private void _save() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.CONTACT_SAVE_URL;
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
        contact = new Gson().fromJson(jsonElement, Contact.class);
        ErrorUtils.showError(this, "Successfully Updated");
    }

    private void _update() {
        showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.CONTACT_SAVE_URL;
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

    private void gotoTitle() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.title_title);
        i.putExtra("url", DIConstants.CONTACT_TITLE_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.TITLE_REQUEST_CODE);
    }

    private void gotoIDType() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.ID_type_title);
        i.putExtra("url", DIConstants.CONTACT_IDTYPE_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.ID_TYPE_REQUEST_CODE);
    }

    private void gotoPostCode() {
        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.CONTACT_POSTCODE_URL, "postcode", R.string.transaction_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
            }

            @Override
            public void onClick(JsonObject object) {
                adapter.updatePostCode(object);
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoIRDBranch() {
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.IRD_branch_title);
        i.putExtra("url", DIConstants.CONTACT_IRDBRANCH_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.IRD_BRANCH_REQUEST_CODE);
    }

    private void gotoTown() {
        SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(this, DIConstants.CONTACT_CITY_URL, R.string.city_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateDataAndRefresh(item, adapter.CONTACT_INFO, adapter.TOWN);
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoState() {
        SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(this, DIConstants.CONTACT_STATE_URL, R.string.state_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateDataAndRefresh(item, adapter.CONTACT_INFO, adapter.STATE);
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoCountry() {
        SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(this, DIConstants.CONTACT_COUNTRY_URL, R.string.country_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateDataAndRefresh(item, adapter.CONTACT_INFO, adapter.COUNTRY);
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoCitizenship() {
        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.CONTACT_CITIZENSHIP_URL, "description", R.string.citizenship_title);
        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                adapter.updateDataAndRefresh(item, adapter.OTHER_INFO, adapter.CITIZENSHIP);
            }

            @Override
            public void onClick(JsonObject object) {
            }
        });

        dialog.showSpinerDialog();
    }

    private void gotoOccupation() {
//        SpinnerDialog dialog = new SpinnerDialog(this, DIConstants.CONTACT_OCCUPATION_URL, "description", R.string.occupation_title);
//        dialog.bindOnSpinerListener(new OnSpinerItemClick() {
//            @Override
//            public void onClick(String item, int position) {
//                adapter.updateDataAndRefresh(item, adapter.OTHER_INFO, adapter.OCCUPATION);
//            }
//
//            @Override
//            public void onClick(JsonObject object) {
//            }
//        });
//
//        dialog.showSpinerDialog();
        Intent i = new Intent(this, GeneralListActivity.class);
        i.putExtra("title", R.string.occupation_title);
        i.putExtra("url", DIConstants.CONTACT_OCCUPATION_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.OCCUPATION_REQUEST_CODE);
    }

    private void showDateOfBirth() {
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

        new SpinnerDatePickerDialogBuilder()
                .context(AddContactActivity.this)
                .callback(new com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = DIHelper.getBirthday(year, monthOfYear, dayOfMonth);
                        adapter.updateDataAndRefresh(date, adapter.OTHER_INFO, adapter.DATE_OF_BIRTH);
                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH))
                .build()
                .show();

    }

//    @Override
////    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
//        String date = DIHelper.getBirthday(datePickerDialog.getSelectedDay());
////        adapter.updateDataAndRefresh(date, adapter.OTHER_INFO, adapter.DATE_OF_BIRTH);
////    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DIConstants.ID_TYPE_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateIDType(accountType, adapter.PERSONAL_INFO, adapter.ID_TYPE);
            }
        } else if (requestCode == DIConstants.TITLE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(accountType, adapter.PERSONAL_INFO, adapter.TITLE);
            }
        } else if (requestCode == DIConstants.IRD_BRANCH_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(accountType, adapter.OTHER_INFO, adapter.IRD_BRANCH);
            }
        } else if (requestCode == DIConstants.OCCUPATION_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription accountType = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(accountType, adapter.OTHER_INFO, adapter.OCCUPATION);
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

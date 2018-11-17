package it.denning.navigation.add.diary;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TimePicker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.model.CodeDescription;
import it.denning.model.Coram;
import it.denning.model.CourtDiaryCourt;
import it.denning.model.MatterLitigation;
import it.denning.model.StaffModel;
import it.denning.navigation.add.diary.court.CourtDiaryAdapter;
import it.denning.navigation.add.diary.office.OfficeDiaryAdapter;
import it.denning.navigation.add.diary.personal.PersonalDiaryAdapter;
import it.denning.navigation.add.utils.simplespinerdialog.SimpleSpinnerDialog;
import it.denning.navigation.add.utils.simplespinerdialog.SpinnerDialog;
import it.denning.navigation.add.utils.coramlist.CoramListActivity;
import it.denning.navigation.add.utils.courtdiarycourtlist.CourtDiaryCourtListActivity;
import it.denning.navigation.add.utils.matterlitigation.MatterLitigationActivity;
import it.denning.navigation.add.utils.stafflist.StaffListActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.OnSpinerItemClick;
import it.denning.search.utils.generallist.GeneralListActivity;

/**
 * Created by denningit on 2018-02-03.
 */

public class BaseDiaryFragment extends Fragment implements
        OnSectionItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    protected int selectedSectionIndex, selectedItemIndex;
    protected DatePickerDialog dpd;
    protected TimePickerDialog tpd;
    public BaseDiaryAdapter adapter;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        selectedSectionIndex = sectionIndex;
        selectedItemIndex = itemIndex;
        switch (name) {
            case "Start Date":
            case "End Date":
            case "Next Start Date":
            case "Next End Date":
                openDate();
                break;
            case "Time":
                openTime();
                break;
            case "Court":
            case "Place":
                gotoCourt();
                break;
            case "Nature of Hearing":
            case "Next Nature of Hearing":
                gotoNatureOfHearing();
                break;
            case "Details":
            case "Next Details":
                gotoDetails(DIConstants.COURT_HEARINGDETAIL_GET_URL);
                break;
            case "Council Assigned":
                gotoCouncilAssigned();
                break;
            case "Staff Attended":
                gotoStaffAttended();
                break;
            case "Staff Assigned":
                gotoStaffAssigned();
                break;
            case "File No (if relevant)":
            case "File No":
                gotoFileNo();
                break;
            case "Appointment":
                gotoAppointment();
                break;
            case "Counsel Attended":
                gotoCounselAttended();
                break;
            case "Coram":
                gotoCoram();
                break;
            case "Court Decision":
                gotoCourtDecision();
                break;
            case "Next Date Type":
                gotoNextDateType();
                break;
        }

        getActivity().findViewById(R.id.search_bank_layout).requestFocus();
    }

    protected void gotoCounselAttended() {
        gotoDetails(DIConstants.STAFF_GET_URL + "attest", "name");
    }

    protected void gotoCoram() {
        Intent intent = new Intent(getActivity(), CoramListActivity.class);
        startActivityForResult(intent, DIConstants.COURT_CORAM_REQUEST_CODE);
    }

    protected void gotoCourtDecision() {
        gotoDetails(DIConstants.COURT_DECISION_GET_URL);
    }

    protected void gotoNextDateType() {
        if(adapter instanceof CourtDiaryAdapter && ((CourtDiaryAdapter)adapter).isNextDetailRequired()) {
            return;
        }
        Intent i = new Intent(getContext(), GeneralListActivity.class);
        i.putExtra("title", R.string.next_date_title_title);
        i.putExtra("url", DIConstants.COURT_NEXTDATE_TYPE_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.CODE_DESC_REQUEST_CODE);
    }

    protected void gotoAppointment(){
        SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(getActivity(), DIConstants.COURT_OFFICE_APPOINTMENT_GET_LIST_URL, R.string.select_appointment);
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

    protected void gotoFileNo() {
        Intent intent = new Intent(getContext(), MatterLitigationActivity.class);
        startActivityForResult(intent, DIConstants.MATTER_REQUEST_CODE);
    }

    protected void gotoStaffAssigned() {
        Intent intent = new Intent(getContext(), StaffListActivity.class);
        intent.putExtra("title", R.string.select_staff_title);
        intent.putExtra("type", "attest");
        startActivityForResult(intent, DIConstants.STAFF_REQUEST_CODE);
    }

    protected void gotoStaffAttended() {
        if (adapter instanceof OfficeDiaryAdapter) {
            SpinnerDialog dialog = new SpinnerDialog(getActivity(), DIConstants.STAFF_GET_URL + "attest" + "&search=", "description", R.string.select_staff_title);
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
        } else {
            Intent intent = new Intent(getContext(), StaffListActivity.class);
            intent.putExtra("title", R.string.select_staff_title);
            intent.putExtra("type", "attest");
            startActivityForResult(intent, DIConstants.STAFF_REQUEST_CODE);
        }
    }

    protected void gotoCouncilAssigned() {
        gotoStaffAssigned();
    }

    protected void gotoDetails(String url, String key) {
        SpinnerDialog dialog = new SpinnerDialog(getActivity(), url, key, R.string.details_title);
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

    protected void gotoDetails(String url) {
        if(adapter instanceof CourtDiaryAdapter && ((CourtDiaryAdapter)adapter).isNextDetailRequired()) {
            return;
        }

        gotoDetails(url, "description");
    }

    protected void gotoNatureOfHearing() {
        if(adapter instanceof CourtDiaryAdapter && ((CourtDiaryAdapter)adapter).isNextDetailRequired()) {
            return;
        }

        Intent i = new Intent(getContext(), GeneralListActivity.class);
        i.putExtra("title", R.string.list_of_hearing_type_title);
        i.putExtra("url", DIConstants.COURT_HEARINGTYPE_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, DIConstants.CODE_DESC_REQUEST_CODE);
    }

    protected void gotoCourt() {
        if (adapter.getViewType(selectedSectionIndex, selectedItemIndex) == DIConstants.TWO_COLUMN_TYPE) {
            Intent intent = new Intent(getContext(), CourtDiaryCourtListActivity.class);
            startActivityForResult(intent, DIConstants.COURT_DIARY_COURT_REQUEST_CODE);
        } else {
            String url = DIConstants.COURT_OFFICE_PLACE_GET_LIST_URL;
            if (adapter instanceof PersonalDiaryAdapter) {
                url = DIConstants.COURT_PERSONAL_PLACE_GET_LIST_URL;
            }
            SimpleSpinnerDialog dialog = new SimpleSpinnerDialog(getActivity(), url, R.string.select_place);
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
    }

    protected void openDate() {
        if(adapter instanceof CourtDiaryAdapter && ((CourtDiaryAdapter)adapter).isNextDetailRequired()) {
            return;
        }
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now
            );
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        }
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    protected void openTime() {
        if(adapter instanceof CourtDiaryAdapter && ((CourtDiaryAdapter)adapter).isNextDetailRequired()) {
            return;
        }
        Calendar now = Calendar.getInstance();
        tpd = new TimePickerDialog(
                getActivity(),
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.show();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = DIHelper.getBirthday(datePickerDialog.getSelectedDay());
        adapter.updateDate(date, selectedSectionIndex, selectedItemIndex);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time  = DIHelper.getTime(hourOfDay, minute);
        adapter.updateTime(time, selectedSectionIndex, selectedItemIndex);
    }

    public void save(String url) {

//        getActivity().findViewById(R.id.search_bank_layout).requestFocus();
        adapter.clearFocus();

        ((DiaryActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPrivatePostRequest(url, adapter.buildSaveParam(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccessResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    public void update(String url) {
     //   getActivity().findViewById(R.id.search_bank_layout).requestFocus();

        ((DiaryActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPrivatePutRequest(url, adapter.buildUpdateParam(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccessResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageSuccessResponse(JsonElement jsonElement) {
        ((DiaryActivity)getActivity()).hideProgress();
        ErrorUtils.showError(App.getInstance(), "Successfully Done");
    }

    private void manageError(String error) {
        ((DiaryActivity)getActivity()).hideProgress();
        ErrorUtils.showError(App.getInstance(), error);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DIConstants.CODE_DESC_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CodeDescription model = (CodeDescription) data.getSerializableExtra("model");
                adapter.updateCodeDescData(model, selectedSectionIndex, selectedItemIndex);
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.MATTER_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                adapter.updateFileNo((MatterLitigation)data.getSerializableExtra("model"));
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        } else if (requestCode == DIConstants.COURT_DIARY_COURT_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                CourtDiaryCourt model = (CourtDiaryCourt) data.getSerializableExtra("model");
                adapter.updateCourt(model, selectedSectionIndex, selectedItemIndex);
            }

        } else if (requestCode == DIConstants.STAFF_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                StaffModel model = (StaffModel) data.getSerializableExtra("model");
                adapter.updateCodeDescData(new CodeDescription(model.code, model.name), selectedSectionIndex, selectedItemIndex);
            }
        } else if (requestCode == DIConstants.COURT_CORAM_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                Coram model = (Coram) data.getSerializableExtra("model");
                adapter.updateCodeDescData(new CodeDescription(model.code, model.name), selectedSectionIndex, selectedItemIndex);
            }
        }
    }

}

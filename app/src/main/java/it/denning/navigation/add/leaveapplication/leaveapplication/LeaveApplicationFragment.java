package it.denning.navigation.add.leaveapplication.leaveapplication;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.CodeDescription;
import it.denning.model.StaffLeaveModel;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.navigation.dashboard.section1.staffleave.leavependingapproval.LeavePendingApprovalActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;
import it.denning.search.utils.generallist.GeneralListActivity;

/**
 * Created by denningit on 2018-01-26.
 */

public class LeaveApplicationFragment extends Fragment implements
        OnSectionItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{

    private static final int typeRequestCode = 1;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private LeaveApplicationAdapter adapter;
    private boolean isStartDate;
    private MonthAdapter.CalendarDay selectedDay;

    private TimePickerDialog tpd;
    private DatePickerDialog dpd;

    public static LeaveApplicationFragment newInstance() {
        LeaveApplicationFragment fragment = new LeaveApplicationFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        return view;
    }

    private void setupRecyclerView() {
        adapter = new LeaveApplicationAdapter(getActivity(), this);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.updateSubmittedBy(((LeaveApplicationActivity)getActivity()).submittedBy);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        switch (name) {
            case "Start Date":
                showCalendar(true);
                break;
            case "End Date":
                showCalendar(false);
                break;
            case "Type of Leave":
                showTypeOfLeave();
                break;
            case "Submit":
                submitApplication();
                break;
        }

//        getView().findViewById(R.id.search_bank_layout).requestFocus();
    }

    private void submitApplication() {
        ((LeaveApplicationActivity)getActivity()).showProgress();
        String url = DISharedPreferences.getInstance().getServerAPI() + DIConstants.STAFF_LEAVE_SAVE_URL;
        NetworkManager.getInstance().sendPrivatePostRequest(url, adapter.buildSaveParams(), new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageSuccess(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageSuccess(JsonElement jsonElement) {
        ((LeaveApplicationActivity)getActivity()).hideProgress();
        StaffLeaveModel staffLeaveModel = new Gson().fromJson(jsonElement, StaffLeaveModel.class);
        LeavePendingApprovalActivity.start(getActivity(), ((LeaveApplicationActivity)getActivity()).submittedBy, staffLeaveModel, false);
    }

    private void manageError(String error) {
        ((LeaveApplicationActivity)getActivity()).hideProgress();
        ErrorUtils.showError(getActivity(), error);
    }

    private void showTypeOfLeave() {
        Intent i = new Intent(getActivity(), GeneralListActivity.class);
        i.putExtra("title", R.string.leave_type_title);
        i.putExtra("url", DIConstants.LEAVE_TYPE_GET_URL);
        i.putExtra("code", "code");
        i.putExtra("value", "description");
        startActivityForResult(i, typeRequestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == typeRequestCode) {
            if(resultCode == Activity.RESULT_OK){
                CodeDescription model= (CodeDescription) data.getSerializableExtra("model");
                adapter.updateTypeOfLeave(model);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void showCalendar(boolean isStartDate) {
        this.isStartDate = isStartDate;
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

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        selectedDay = datePickerDialog.getSelectedDay();
        Calendar now = Calendar.getInstance();
        tpd = new TimePickerDialog(
                getActivity(),
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                updateTime(9, 0);
            }
        });
        tpd.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateTime(hourOfDay, minute);
    }

    private void updateTime(int hourOfDay, int minute) {
        Calendar calendar= Calendar.getInstance();
        calendar.set(selectedDay.getYear(), selectedDay.getMonth(), selectedDay.getDay(), hourOfDay, minute);
        String date = DIHelper.getDate(calendar);
        if (isStartDate) {
            adapter.updateStartDate(date);
        } else {
            adapter.updateEndDate(date);
        }
    }
}

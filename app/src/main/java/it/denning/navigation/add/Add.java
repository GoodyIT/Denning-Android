package it.denning.navigation.add;

import android.content.Context;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyNameCodeCallback;
import it.denning.model.Attendance;
import it.denning.model.MenuModel;
import it.denning.model.NameCode;
import it.denning.navigation.add.bill.AddBillActivity;
import it.denning.navigation.add.contact.AddContactActivity;
import it.denning.navigation.add.diary.DiaryActivity;
import it.denning.navigation.add.leaveapplication.LeaveApplicationActivity;
import it.denning.navigation.add.matter.AddMatterActivity;
import it.denning.navigation.add.property.AddPropertyActivity;
import it.denning.navigation.add.quotation.AddQuotationActivity;
import it.denning.navigation.add.receipt.AddReceiptActivity;
import it.denning.navigation.home.attendance.AttendanceActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnSectionItemClickListener;

/**
 * Created by denningit on 09/04/2017.
 */

public class Add extends Fragment implements OnSectionItemClickListener {
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    public static void start(Context context) {
        Intent i = new Intent(context, Add.class);
        context.startActivity(i);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        ((MainActivity)getActivity()).hideDennigSupport();
        ((MainActivity)getActivity()).hideBottomBar();
        ((MainActivity)getActivity()).showNavigation(false);

        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).titleView.setText(R.string.add_title);

        getDynamicMenu();
    }

    private void parseDynamicMenu(JsonElement jsonElement) {
        MenuModel[] models = new Gson().fromJson(jsonElement, MenuModel[].class);
        setupRecyclerView(models);
    }

    private void getDynamicMenu() {
        ((MainActivity)getActivity()).showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(DIConstants.ADD_DYNAMIC_MENU, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                ((MainActivity)getActivity()).hideActionBarProgress();
                parseDynamicMenu(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideActionBarProgress();
                ErrorUtils.showError(getActivity(), error);
            }
        });
    }

    private void setupRecyclerView(MenuModel[] models) {
        AddAdapter addAdapter = new AddAdapter(this, models);
        recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(addAdapter);
    }

    private void gotoAttendance() {
        if (!DISharedPreferences.getInstance().isStaff()) {
            DIAlert.showSimpleAlertAndGotoLogin(getContext(), R.string.access_restricted, R.string.access_restricted_staff);
            return;
        }
        String url = DIConstants.ATTENDANCE_GET_URL;

        ((MainActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                ((MainActivity)getActivity()).hideProgress();
                DISharedPreferences.attendance = new Gson().fromJson(jsonElement.getAsJsonObject(), Attendance.class);
                AttendanceActivity.start(getContext());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    @Override
    public void onClick(View view, int sectionIndex, int itemIndex, String name) {
        if (DISharedPreferences.getInstance().isSessionExpired) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_session_expired);
            return;
        }
        switch (name) {
            case "add_contact":
                AddContactActivity.start(getActivity(), null);
                break;
            case "add_property": // Property
                AddPropertyActivity.start(getActivity(), null);
                break;
            case "add_matter": // Matter
                AddMatterActivity.start(getActivity(), null);
                break;
            case "add_court": // Court Diary
                DiaryActivity.start(getActivity(), R.string.add_court_diary_title);
                break;
            case "add_office": // Office Diary
                DiaryActivity.start(getActivity(), R.string.add_office_diary_title);
                break;
            case "add_leave": // Leave Application
                NetworkManager.getInstance().getSubmittedBy(new MyNameCodeCallback() {
                    @Override
                    public void next(NameCode value) {
                        LeaveApplicationActivity.start(getActivity(), value);
                    }
                });
                break;
            case "add_quotation": // Quotation
                AddQuotationActivity.start(getActivity());
                break;
            case "add_invoice": // Tax Invoice
                AddBillActivity.start(getActivity(), true, null);
                break;
            case "add_receipt": // Receipt
                AddReceiptActivity.start(getActivity(), null);
                break;
            case "add_attendance":
                gotoAttendance();
                break;
        }
    }
}

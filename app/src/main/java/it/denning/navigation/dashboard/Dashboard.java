package it.denning.navigation.dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.App;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.model.DashboardModel;
import it.denning.model.DocumentModel;
import it.denning.model.S3;
import it.denning.navigation.dashboard.section1.FileListingActivity;
import it.denning.navigation.dashboard.section1.MyDueTaskActivity;
import it.denning.navigation.dashboard.section1.staffleave.StaffLeaveActivity;
import it.denning.navigation.dashboard.section2.CollectionActivity;
import it.denning.navigation.dashboard.section3.CompletionDateActivity;
import it.denning.navigation.dashboard.section4.bankandcash.BankCashActivity;
import it.denning.navigation.dashboard.section4.bankrecon.BankReconActivity;
import it.denning.navigation.dashboard.section4.contacts.DashboardContactActivity;
import it.denning.navigation.dashboard.section4.dashboardattendance.DashboardAttendanceActivity;
import it.denning.navigation.dashboard.section4.dashboardcontactfolder.DashboardContactFolderActivity;
import it.denning.navigation.dashboard.section4.dashboardquotation.DashboardQuotationActivity;
import it.denning.navigation.dashboard.section4.feestransfer.FeesTransferActivity;
import it.denning.navigation.dashboard.section4.fileledgers.FileLedgerActivity;
import it.denning.navigation.dashboard.section4.graph.FeeAndGrowth;
import it.denning.navigation.dashboard.section4.profitloss.ProfitLossActivity;
import it.denning.navigation.dashboard.section4.staffonline.StaffOnlineActivity;
import it.denning.navigation.dashboard.section4.taskchecklist.StaffDueTaskActivity;
import it.denning.navigation.dashboard.section4.taxinvoice.TaxInvoiceActivity;
import it.denning.navigation.dashboard.section4.trialbalance.TrialBalanceActivity;
import it.denning.navigation.dashboard.util.DashboardFifthItemAdapter;
import it.denning.navigation.dashboard.util.DashboardForthItemAdapter;
import it.denning.navigation.dashboard.util.DashboardSecondItemClickListener;
import it.denning.navigation.dashboard.util.DashboardSecondItemFrameAdapter;
import it.denning.navigation.dashboard.util.DashboardThirdAdapter;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.document.DocumentActivity;

/**
 * Created by denningit on 09/04/2017.
 */

public class Dashboard extends Fragment implements DashboardSecondItemClickListener {
    DashboardThirdAdapter dashboardThirdAdapter;
    DashboardModel dashboardModel;

    @BindView(R.id.recycler_list)
    RecyclerView dashboardList;
    @BindView(R.id.textview_today)
    TextView today;
    @BindView(R.id.second_gridview)
    GridView secondGridView;
    @BindView(R.id.forth_gridview)
    GridView forthGridView;
    @BindView(R.id.fifth_gridview)
    GridView fifthGridView;
    @BindView(R.id.forth_section_title)
    TextView forthSectionTitle;

    Point size;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        ((MainActivity)getActivity()).hideDennigSupport();
        ((MainActivity)getActivity()).hideBottomBar();
        ((MainActivity)getActivity()).showNavigation(false);
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ((MainActivity)getActivity()).titleView.setText("Dashboard");

        initFields();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDashboard();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).hideActionBarProgress();
        NetworkManager.getInstance().clearQueue();
    }

    public void getDashboard() {
        ((MainActivity)getActivity()).showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(DIConstants.DASHBOARD_MAIN_URL, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                if (getActivity() != null) {
                    ((MainActivity)getActivity()).hideActionBarProgress();
                    dashboardModel = new Gson().fromJson(jsonElement, DashboardModel.class);
                    if (dashboardModel != null) {
                        setupDashboard();
                    }
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideActionBarProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    public void initFields() {
        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public void setupDashboard() {
        setupTodaySection();
        setupSecondSection();
        setupThirdSection();
        setupForthSection();
        setupFifthSection();
    }

    public void setupTodaySection() {
        today.setText(DIHelper.getOnlyDateFromDateTime(dashboardModel.todayDate));
    }

    public void setupSecondSection() {
        final DashboardSecondItemFrameAdapter itemAdapter = new DashboardSecondItemFrameAdapter(getContext(), dashboardModel.s1.items);
        ViewGroup.LayoutParams layoutParams = secondGridView.getLayoutParams();
        layoutParams.height = size.x/3 * (int)(Math.ceil((dashboardModel.s1.items.size()/3.0))); //this is in pixels
        secondGridView.setLayoutParams(layoutParams);
        secondGridView.setAdapter(itemAdapter);
        secondGridView.setColumnWidth(size.x/3-9);
        secondGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSecondItemClick(dashboardModel.s1.items.get(position).formName, position, "",  dashboardModel.s1.items.get(position).mainAPI);
            }
        });
    }

    public void setupThirdSection() {
        dashboardThirdAdapter = new DashboardThirdAdapter(getContext(), dashboardModel, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        dashboardList.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(App.getInstance(), R.drawable.item_decorator)));
        linearLayoutManager.setItemPrefetchEnabled(false);
        dashboardList.setLayoutManager(linearLayoutManager);
        dashboardList.setHasFixedSize(true);
        dashboardList.setItemAnimator(new DefaultItemAnimator());
        dashboardList.setAdapter(dashboardThirdAdapter);
    }

    public void setupForthSection() {
        forthSectionTitle.setText(dashboardModel.s3.title);

        final DashboardForthItemAdapter itemAdapter = new DashboardForthItemAdapter(getContext(), dashboardModel.s3.items, -1);
        ViewGroup.LayoutParams layoutParams = forthGridView.getLayoutParams();
        layoutParams.height = (size.x/4-50) * 2; //this is in pixels
        forthGridView.setLayoutParams(layoutParams);
        forthGridView.setAdapter(itemAdapter);
        forthGridView.setColumnWidth(size.x/4-9);
        forthGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSecondItemClick(dashboardModel.s3.items.get(position).nextLevelForm, position, "", dashboardModel.s3.items.get(position).api);
            }
        });
    }

    public void setupFifthSection() {
        final DashboardFifthItemAdapter itemAdapter = new DashboardFifthItemAdapter(getContext(), dashboardModel.s4.items);
        ViewGroup.LayoutParams layoutParams = fifthGridView.getLayoutParams();
        layoutParams.height = size.x/3 * (int)(Math.ceil(dashboardModel.s4.items.size()/3.0)); //this is in pixels
        fifthGridView.setLayoutParams(layoutParams);
        fifthGridView.setAdapter(itemAdapter);
        fifthGridView.setColumnWidth(size.x/3-9);
        fifthGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSecondItemClick(dashboardModel.s4.items.get(position).formName, position, "",  dashboardModel.s4.items.get(position).mainAPI);
            }
        });
    }

    @Override
    public void onSecondItemClick(String viewType, int position, String value, String api) {
        if (DISharedPreferences.getInstance().isSessionExpired) {
            DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_session_expired);
            return;
        }
        switch (viewType) {
            case "new_matters":
                gotoNextActivity(FileListingActivity.class, value, api);
                break;
            case "court_today":
                loadEvents();
                break;
            case "my_due_tasks":
                gotoNextActivity(MyDueTaskActivity.class, value, api);
                break;
            case "collection":
                gotoNextActivity(CollectionActivity.class, value, api, position);
                break;
            case "leave_pending_approval":
                StaffLeaveActivity.start(getActivity(), api);
                break;
            case "transit_folder":
                openTrasitFolder(api);
                break;
            case "contact_folder":
                gotoNextActivity(DashboardContactFolderActivity.class, "", api);
                break;
            case "contacts":
                gotoNextActivity(DashboardContactActivity.class, "", api);
                break;
            case "bank_recon":
                gotoNextActivity(BankReconActivity.class, "", api);
                break;
            case "task_checklist_alert":
                gotoNextActivity(StaffDueTaskActivity.class, "", api);
                break;
            case "file_ledgers":
                gotoNextActivity(FileLedgerActivity.class, "", api);
                break;
            case "bank_cash_balance":
                gotoNextActivity(BankCashActivity.class, "", api);
                break;
            case "trial_balance":
                gotoNextActivity(TrialBalanceActivity.class, "", api);
                break;
            case "tax_invoice":
                gotoNextActivity(TaxInvoiceActivity.class, "", api);
                break;
            case "fees_transfer":
                gotoNextActivity(FeesTransferActivity.class, "", api);
                break;
            case "profit_loss":
                gotoNextActivity(ProfitLossActivity.class, "", api);
                break;
            case "staff_online":
                gotoNextActivity(StaffOnlineActivity.class, "", api);
                break;
            case "attendance":
                gotoNextActivity(DashboardAttendanceActivity.class, "", api);
                break;
            case "fee_matter_growth":
                gotoNextActivity(FeeAndGrowth.class, "", api);
                break;
            case "quotation":
                gotoNextActivity(DashboardQuotationActivity.class, "", api);
                break;
            case "view_spa_completion_tracking":
                gotoNextActivity(CompletionDateActivity.class, "", api, position, dashboardModel.s3);
                break;
        }
    }

    public void gotoNextActivity(Class<?> cls, String value, String api) {
        gotoNextActivity(cls, value, api, -1);
    }

    public void gotoNextActivity(Class<?> cls, String value, String api, int position ) {
        gotoNextActivity(cls, value, api, -1, null);
    }

    public void gotoNextActivity(Class<?> cls, String value, String api, int position, S3 model) {
        Intent i = new Intent(getContext(), cls);
        i.putExtra("api", api);
        i.putExtra("label", value);
        i.putExtra("position", position);
        i.putExtra("model", model);
        startActivity(i);
    }

    private void openTrasitFolder(String api) {
        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading", true, false);
        NetworkManager.getInstance().sendPrivateGetRequest(api, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                pd.dismiss();
                DISharedPreferences.documentModel = new Gson().fromJson(jsonElement, DocumentModel.class);
                DocumentActivity.start(getActivity(), "Transit Folder");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                pd.dismiss();
                ErrorUtils.showError(getActivity(), error);
            }
        });
    }

    private void loadEvents() {
        String url = DIConstants.EVENT_LATEST_URL + "?dateStart=" + DIHelper.todayWithTime() +
                "&dateEnd=" + DIHelper.todayWithTime() + "&filterBy=1court";

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading", true, false);

        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                pd.dismiss();
                it.denning.model.Event[] events = new Gson().fromJson(jsonElement, it.denning.model.Event[].class);
                DISharedPreferences.events = Arrays.asList(events);
                CalendarActivity.start(getActivity());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                pd.dismiss();
                ErrorUtils.showError(getActivity(), error);
            }
        });
    }

}

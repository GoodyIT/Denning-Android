package it.denning.navigation.home.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.EndlessRecyclerViewScrollListener;
import it.denning.model.EditCourtModel;
import it.denning.model.OfficeDiaryModel;
import it.denning.navigation.add.diary.DiaryActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.KeyboardUtils;

/**
 * Created by denningit on 2017-12-17.
 */

public class CalendarActivity extends BaseActivity implements OnItemClickListener{
    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @OnClick(R.id.back_btn)
    void onBack() {
        KeyboardUtils.hideKeyboard(this);
        finish();
    }

    @BindView(R.id.calendar_search)
    SearchView searchView;

    @BindView(R.id.calendar_bottom)
    NavigationTabStrip bottomFilter;

    @BindView(R.id.calendar_top)
    NavigationTabStrip topFilter;

    @BindView(R.id.calendar_year_month)
    TextView yearMonth;

    @BindView(R.id.calendar_view)
    CompactCalendarView compactCalendarView;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private String[] bottomFilterArray = {"1court", "2office", "3personal", "0All"};
    private int curBottomFilter = 0;
    private String  curYear, curMonth, startDate, endDate;
    private List<it.denning.model.Event> eventList = new ArrayList<>();
    private List<Event> monthlyEvents = new ArrayList<>();
    private CalendarAdapter adapter, modalAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int page = 1;
    private String filter = "";
    private String eventType;
    private boolean isModalList = false;
    private MaterialDialog md;

    private String eventLoadType = "main";

    public static void start(Context context) {
        Intent intent = new Intent(context, CalendarActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_calendar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initActionBar();

        loadMonthlyEvents();
    }

    private void initFields() {
        toolbarTitle.setText(getString(R.string.calendar_title));
        curYear = DIHelper.getYear(new Date());
        curMonth = DIHelper.getMonth(new Date());
        startDate = endDate = DIHelper.todayF();

        eventList = DISharedPreferences.events;

        configureCalendar();

        setupSearchView();

        setupFilters();

        setupRecyclerView();

        setupEndlessScroll();
    }

    private void configureCalendar() {
        yearMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                startDate  = endDate = DIHelper.getDate(dateClicked);
                curBottomFilter = 3;
                page = 1;
                eventLoadType = "dialog";
                adapter.clear();
                loadEvents();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                yearMonth.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                curYear = DIHelper.getYear(firstDayOfNewMonth);
                curMonth = DIHelper.getMonth(firstDayOfNewMonth);

                loadMonthlyEvents();
            }
        });
    }

    private void searchEvents(String query) {
        filter = query;
        page = 1;
        NetworkManager.getInstance().clearQueue();
        adapter.clear();
        loadEvents();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(CalendarActivity.this);
                searchEvents(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchEvents(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(CalendarActivity.this);
                searchEvents("");
                return false;
            }
        });
    }

    private void setupFilters() {
        topFilter.setTabIndex(0);
        topFilter.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                switch (index) {
                    case 0: // Today
                        startDate = endDate = DIHelper.todayF();
                        break;

                    case 1: // Next 7 days
                        startDate = DIHelper.todayF();
                        endDate = DIHelper.sevenDaysAfter(DIHelper.todayF());
                        break;

                    case 2: // Future
                        startDate = DIHelper.todayF();
                        endDate = "2999-12-31";
                        break;

                    case 3: // past
                        startDate = "1000-01-01";
                        endDate = DIHelper.todayF();
                        break;
                }
                page = 1;
                eventLoadType = "main";
                adapter.clear();
                loadEvents();
            }

            @Override
            public void onEndTabSelected(String title, int index) {

            }
        });

        bottomFilter.setTabIndex(0);
        bottomFilter.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {
                curBottomFilter = index;
                page = 1;
                eventLoadType = "main";
                adapter.clear();
                loadEvents();
            }

            @Override
            public void onEndTabSelected(String title, int index) {

            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CalendarAdapter(new ArrayList<it.denning.model.Event>(), this);
        linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new it.denning.general.DividerItemDecoration(ContextCompat.getDrawable(this, R.drawable.item_decorator)));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.updateAdapter(DISharedPreferences.events);
    }

    private void setupEndlessScroll() {
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                eventLoadType = "main";
                loadEvents();
            }
        };
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void manageMonthlyEvents(JsonArray jsonArray) {
        String[] months = new Gson().fromJson(jsonArray, String[].class);
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.YEAR, Integer.parseInt(curYear));
        currentCalender.set(Calendar.MONTH, Integer.parseInt(curMonth)-1);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        for (int i = 0; i < months.length; i++) {
            currentCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(months[i]));
            long timeInMillis = currentCalender.getTimeInMillis();
            Event event = new Event(Color.argb(255, 169, 68, 65), timeInMillis, "event");
            monthlyEvents.add(event);
        }

        compactCalendarView.addEvents(monthlyEvents);

        compactCalendarView.invalidate();
    }
    private void loadMonthlyEvents() {
        String url = DIConstants.CALENDAR_MONTHLY_SUMMARY_URL +
                "?year=" + curYear + "&month=" + curMonth +"&filterBy=" + bottomFilterArray[curBottomFilter];

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideActionBarProgress();
                manageMonthlyEvents(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                ErrorUtils.showError(getApplication(), error);
            }
        });
    }

    private void showEventDialog(List<it.denning.model.Event> events, String date) {
        isModalList = true;
        modalAdapter = new CalendarAdapter(events, this);
        md = new MaterialDialog.Builder(this)
                .title(date)
                // second parameter is an optional layout manager. Must be a LinearLayoutManager or GridLayoutManager.
                .adapter(modalAdapter, null)
                .show();
    }

    private void parseEvents(JsonArray jsonArray) {
        hideActionBarProgress();
        it.denning.model.Event[] events = new Gson().fromJson(jsonArray, it.denning.model.Event[].class);
        if (events.length > 0) {
            page++;
        }
        if (eventLoadType.equals("main")) {
            adapter.updateAdapter(Arrays.asList(events));
        } else if (eventLoadType.equals("dialog") && events.length > 0) {
            showEventDialog(Arrays.asList(events), startDate);
        }
    }

    private void loadEvents() {
        String url = DIConstants.EVENT_LATEST_URL + "?dateStart=" + startDate +
                "&dateEnd=" + endDate + "&filterBy="+ bottomFilterArray[curBottomFilter] +"&search="+ filter +"&page=" + page;

        showActionBarProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                parseEvents(jsonElement.getAsJsonArray());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideActionBarProgress();
                showSnackbar(error, Snackbar.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        it.denning.model.Event event = null;
        if (isModalList) {
            isModalList = false;
            event = modalAdapter.getModels().get(position);
            if (md != null) {
                md.dismiss();
            }
        } else {
            event = adapter.getModels().get(position);
        }
        eventType = event.eventType;
        String courtString = "";
        switch (event.eventType) {
            case "1court":
                courtString = "courtDiary";
                break;
            case "2office":
                courtString = "OfficeDiary";
                break;
            default:
                courtString = "PersonalDiary";
                break;

        }
        String url = "v1/" + courtString + "/" + event.code;
        gotoEditDiary(url);
    }

    private void manageCourtResponse(JsonElement jsonElement) {
        hideProgress();

        switch (eventType) {
            case "1court":
                EditCourtModel editCourtModel = new Gson().fromJson(jsonElement, EditCourtModel.class);
                DiaryActivity.start(this, R.string.update_court_diary_title, editCourtModel);
                break;
            case "2office":
                OfficeDiaryModel officeDiaryModel = new Gson().fromJson(jsonElement, OfficeDiaryModel.class);
                DiaryActivity.start(this, R.string.update_office_diary_title, officeDiaryModel);
                break;
            default:
                OfficeDiaryModel personalDiaryModel = new Gson().fromJson(jsonElement, OfficeDiaryModel.class);
                DiaryActivity.start(this, R.string.update_personal_diary_title, personalDiaryModel);
                break;
        }
    }

    private void gotoEditDiary(String url) {
        showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageCourtResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(getApplication(), error);
            }
        });
    }
}

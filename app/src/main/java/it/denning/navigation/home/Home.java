package it.denning.navigation.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.App;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.auth.branch.FirmBranchActivity;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.AdsModel;
import it.denning.model.Attendance;
import it.denning.model.FirmURLModel;
import it.denning.model.News;
import it.denning.navigation.home.attendance.AttendanceActivity;
import it.denning.navigation.home.branch.HomeBranchAdapter;
import it.denning.navigation.home.calculators.CalculatorActivity;
import it.denning.navigation.home.calendar.CalendarActivity;
import it.denning.navigation.home.comingsoon.ComingSoonActivity;
import it.denning.navigation.home.news.NewsActivity;
import it.denning.navigation.home.util.AdsViwerActivity;
import it.denning.navigation.home.util.HomeMenu;
import it.denning.navigation.home.util.HomeMenuAdapter;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.search.SearchActivity;
import it.denning.search.utils.ClearableAutoCompleteTextView;

/**
 * Created by denningit on 09/04/2017.
 */

public class Home extends Fragment {
    private ArrayList<HomeMenu> homeMenuList;
    private String[] menuNameList = {"News", "Property", "Market", "Services", "Calculators", "Jobs", "Forum", "Products", "Shared", "Upload", "Calendar", "Top-Up"};
    private Integer[] menuIconList = {R.drawable.icon_news, R.drawable.icon_real_estate, R.drawable.icon_market, R.drawable.icon_services, R.drawable.icon_calculator, R.drawable.icon_jobs, R.drawable.icon_forum, R.drawable.icon_products, R.drawable.icon_shared, R.drawable.icon_upload, R.drawable.icon_calendar, R.drawable.icon_topup};

    @BindView(R.id.home_search)
    ClearableAutoCompleteTextView searchView;

    @BindView(R.id.branch_layout)
    RelativeLayout branchLayout;

    @BindView(R.id.home_gridview)
    GridView home_gridview;

    @BindView(R.id.carouselView)
    CarouselView carouselView;

    boolean userTouchedView;
    public static View.OnClickListener onClickListener;
    private MaterialDialog branchDialog;
    static AdsModel[] adsModelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).titleView.setText("Denning");

        ButterKnife.bind(this, view);
        // Draw menulist
        drawScreenMenu(view);

        changeUI();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staffSignIn();
            }
        };

        ((MainActivity)getActivity()).hideDennigSupport();
        ((MainActivity)getActivity()).showBottomBar();
        ((MainActivity)getActivity()).showNavigation(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!userTouchedView) {
                    //YOUR CASE 2
                    gotoSearch();
                    userTouchedView = true;
                    v.clearFocus();
                    return true;
                }
                return true;
            }
        });

        loadsAds();
    }

    @Override
    public void onStart() {
        super.onStart();
        userTouchedView = false;
    }

    @OnClick(R.id.branch_layout)
    void didTapBranch() {

        loginAndGotoBranch(new MyCallbackInterface() {
            @Override
            public void nextFunction() {
            }
            @Override
            public void nextFunction(JsonElement jsonElement) {
                showBranch(jsonElement.getAsJsonObject());
            }
        });
    };

    private void displayAds(JsonElement jsonElement) {
        adsModelList = new Gson().fromJson(jsonElement, AdsModel[].class);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                AdsModel adsModel = adsModelList[position];
                imageView.setImageBitmap(DIHelper.base64ToBitmap(adsModel.getBase64Image()));
            }
        });

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                AdsModel adsModel = adsModelList[position];
                AdsViwerActivity.start(getContext(), adsModel.URL);
            }
        });

        carouselView.setPageCount(adsModelList.length);
    }

    private void loadsAds() {
        NetworkManager.getInstance().loadAds(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                displayAds(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(App.getInstance(), error);
            }
        });
    }

    private void gotoSearch() {
        Intent myIntent = new Intent(getActivity(), SearchActivity.class);
        startActivity(myIntent);
    }

    private void changeUI() {
        if (!DISharedPreferences.getInstance().isLoggedIn()) {
            branchLayout.setVisibility(View.GONE);
        }
    }

    private void drawScreenMenu(View view) {
        createMenuList();

        ViewGroup.LayoutParams layoutParams = home_gridview.getLayoutParams();
        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        layoutParams.height = (int)((size.x/4-9)*Math.ceil(menuNameList.length/4.0))+3; //this is in pixels
        home_gridview.setLayoutParams(layoutParams);
        HomeMenuAdapter homeMenuAdapter = new HomeMenuAdapter(getContext(), homeMenuList);
        home_gridview.setAdapter(homeMenuAdapter);
        home_gridview.setColumnWidth(size.x/4-9);
        home_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DISharedPreferences.getInstance().isSessionExpired) {
                    DIAlert.showSimpleAlert(getActivity(), R.string.warning_title, R.string.alert_session_expired);
                    return;
                }
                String menuName = menuNameList[position];
                switch (menuName) {
                    case "News":
                        gotoNews();
                        break;

                    case "Calculators":
                        gotoCalculators();
                        break;

                    case "Shared":
                        gotoShared();
                        break;

                    case "Calendar":
                        gotoCalendar();
                        break;

                    case "Upload":
                        gotoUpload();
                        break;

                    case "Property":
                    case "Jobs":
                    case "Market":
                    case "Services":
                    case "Forum":
                    case "Products":
                    case "Top-Up":
                        ComingSoonActivity.start(getContext());
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private List<HomeMenu> createMenuList() {
        homeMenuList = new ArrayList();

        if (!DISharedPreferences.getInstance().isStaff()) {
            menuNameList = new String[] {"News", "Calculators", "Forum", "Products", "Shared", "Upload",};
            menuIconList = new Integer[] {R.drawable.icon_news, R.drawable.icon_calculator, R.drawable.icon_forum, R.drawable.icon_products, R.drawable.icon_shared, R.drawable.icon_upload,};
        }

        for (int i = 0; i < menuNameList.length; i++) {
            HomeMenu homeMenu = new HomeMenu();
            homeMenu.resId = menuIconList[i];
            homeMenu.menuName = menuNameList[i];
            homeMenuList.add(homeMenu);
        }
        return homeMenuList;
    }

    private void gotoCalendar() {
        if (!DISharedPreferences.getInstance().isStaff()) {
            DIAlert.showSimpleAlertAndGotoLogin(getContext(), R.string.access_restricted, R.string.access_restricted_staff);
            return;
        }

        String url = DIConstants.EVENT_LATEST_URL + "?dateStart=" + DIHelper.todayF() +
                "&dateEnd=" + DIHelper.todayF() + "&filterBy=1court&search=&page=1";

        ((MainActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPrivateGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                ((MainActivity)getActivity()).hideProgress();
                it.denning.model.Event[] events = new Gson().fromJson(jsonElement.getAsJsonArray(), it.denning.model.Event[].class);
                DISharedPreferences.events = Arrays.asList(events);
                CalendarActivity.start(getContext());
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    Boolean isPossibleToUse() {
        if (!DISharedPreferences.getInstance(getContext()).isLoggedIn()) {
            DIAlert.showSimpleAlert(getActivity(), R.string.access_restricted, R.string.access_restricted_client);
            return false;
        }

        if (!DISharedPreferences.getInstance(getContext()).isClient()) {
            DIAlert.showSimpleAlert(getActivity(), R.string.alert_shared_folder);
            return false;
        }

        return true;
    }

    private void gotoBranch(JsonObject jsonObject) {
        ((MainActivity)getActivity()).hideProgress();
        FirmURLModel firmURLModel = new Gson().fromJson(jsonObject, FirmURLModel.class);
        DISharedPreferences.getInstance(getContext()).saveUserInfoFromResponse(firmURLModel);
        FirmBranchActivity.start(getContext());
    }

    private void showBranch(JsonObject jsonObject) {
        ((MainActivity)getActivity()).hideProgress();
        FirmURLModel firmURLModel = new Gson().fromJson(jsonObject, FirmURLModel.class);
        DISharedPreferences.getInstance(getContext()).saveUserInfoFromResponse(firmURLModel);

        branchDialog = new MaterialDialog.Builder(getContext())
                .title(R.string.branch_title)
                // second parameter is an optional layout manager. Must be a LinearLayoutManager or GridLayoutManager.
                .adapter(new HomeBranchAdapter(firmURLModel), null)
                .show();
    }

    private void loginAndGotoBranch(final MyCallbackInterface myCallbackInterface) {
        JsonObject param = new JsonObject();
        param.addProperty("email", DISharedPreferences.getInstance(getContext()).getEmail());
        param.addProperty("password", DISharedPreferences.getInstance(getContext()).getPassword());

        ((MainActivity)getActivity()).showProgress();
        NetworkManager.getInstance().mainLogin(param, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
               myCallbackInterface.nextFunction(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    private void gotoUpload() {
        if (!isPossibleToUse()) {
            return;
        }
        DISharedPreferences.documentView = "upload";

        loginAndGotoBranch(new MyCallbackInterface() {
            @Override
            public void nextFunction() {
            }
            @Override
            public void nextFunction(JsonElement jsonElement) {
                gotoBranch(jsonElement.getAsJsonObject());
            }
        });
    }

    private void gotoShared() {
        if (!isPossibleToUse()) {
            return;
        }
        DISharedPreferences.documentView = "shared";

        loginAndGotoBranch(new MyCallbackInterface() {
            @Override
            public void nextFunction() {
            }
            @Override
            public void nextFunction(JsonElement jsonElement) {
                gotoBranch(jsonElement.getAsJsonObject());
            }
        });
    }

    private void gotoNews() {
        ((MainActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPublicGetRequest(DIConstants.NEWS_LATEST_URL, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                News[] events = new Gson().fromJson(jsonElement.getAsJsonArray(), News[].class);
                DISharedPreferences.news = Arrays.asList(events);
                NewsActivity.start(getContext(), "news");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    private void gotoUpdates() {
        ((MainActivity)getActivity()).showProgress();
        NetworkManager.getInstance().sendPublicGetRequest(DIConstants.UPDATES_LATEST_URL, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                News[] events = new Gson().fromJson(jsonElement.getAsJsonArray(), News[].class);
                DISharedPreferences.news = Arrays.asList(events);
                NewsActivity.start(getContext(), "updates");
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ((MainActivity)getActivity()).hideProgress();
                ErrorUtils.showError(getContext(), error);
            }
        });
    }

    private void gotoCalculators() {
        CalculatorActivity.start(getContext());
    }

    private void staffSignIn() {
        NetworkManager.getInstance().staffSignIn(new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                FirmURLModel firmURLModel = new Gson().fromJson(jsonElement.getAsJsonObject(), FirmURLModel.class);
                if (firmURLModel.statusCode == 200) {
                    DISharedPreferences.getInstance().saveSessionID(firmURLModel.sessionID);
                    branchDialog.dismiss();
                } else {
                    ErrorUtils.showError(getActivity(), getContext().getResources().getString(R.string.alert_no_access_to_firm));
                }
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(getActivity(), error);
            }
        });
    }
}


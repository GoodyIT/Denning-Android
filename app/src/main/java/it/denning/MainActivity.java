package it.denning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.helpers.CoreSharedHelper;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.denning.auth.SignInActivity;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.general.MyCallbackInterface;
import it.denning.model.Agreement;
import it.denning.model.LegalFirm;
import it.denning.navigation.add.Add;
import it.denning.navigation.dashboard.Dashboard;
import it.denning.navigation.home.Home;
import it.denning.navigation.home.settings.SettingsActivity;
import it.denning.navigation.home.util.AdsViwerActivity;
import it.denning.navigation.message.DenningMessage;
import it.denning.navigation.message.DenningSupportActivity;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.ui.activities.agreements.UserAgreementActivity;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.helpers.ImportFriendsHelper;
import it.denning.utils.helpers.ServiceManager;
import rx.Subscriber;

public class MainActivity extends BaseActivity
        implements Drawer.OnDrawerItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    @BindView(R.id.bottomBar)
    public BottomBar bottomBar;

    @BindView(R.id.main_title)
    public TextView titleView;

    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawer;

    @BindView(R.id.nvView)
    public NavigationView nvDrawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.back_btn)
    ImageButton backBtn;

    @OnClick(R.id.back_btn)
    void onBack() {
        super.onBackPressed();
        replaceFragment(new Home());
    }

    private ActionBarDrawerToggle drawerToggle;
    private View headerLayout;

    private ImportFriendsSuccessAction importFriendsSuccessAction;
    private ImportFriendsFailAction importFriendsFailAction;

    public boolean isSupportMessage = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
//        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
//        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);

        drawNavigation();

        // Draw BottomBar
        drawBottomBar();

        // select home screen
        changeUI();

        initFields();

        setUpActionBarWithUpButton();

        checkAndProcessChatLogin();

        addDialogsAction();

        openPushDialogIfPossible();
    }

    void drawNavigation() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        mDrawer.requestLayout();
        nvDrawer.bringToFront();

        headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAuth();
            }
        });
    }

    public void showNavigation(Boolean isShow) {
        setActionBarUpButtonEnabled(isShow);
        if (isShow) {
            backBtn.setVisibility(View.GONE);
        } else {
            backBtn.setVisibility(View.VISIBLE);
        }
    }

    private void checkAndProcessChatLogin() {
        if (!isChatInitializedAndUserLoggedIn() && DISharedPreferences.getInstance(this).isLoggedIn()) {
            Log.d(TAG, "onCreate. !isChatInitializedAndUserLoggedIn()");
            loginChat();
        }
    }

    private void startDialogActivity(QBChatDialog chatDialog, QMUser user) {
        if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
            startPrivateChatActivity(user, chatDialog);
        } else {
            startGroupChatActivity(chatDialog);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null && getIntent().getStringExtra("fragment") != null && getIntent().getStringExtra("fragment").contains("Home")) {
            changeUI();
        }
        addActions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeActions();
        String current = getSupportFragmentManager().findFragmentById(R.id.content_frame).toString();
        getIntent().putExtra("fragment", current);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDialogsAction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DIConstants.LOGOUT_REQUEST_CODE) {

            if (resultCode == AppCompatActivity.RESULT_OK) {
                // do something with the result
                logout();
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }
    }

    @Override
    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(R.string.dlg_internet_connection_is_missing));
            setActionBarIcon(null);
        } else {
            setActionBarTitle(title);
        }
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);
    }

    private void addDialogsAction() {
        addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, new LoadChatsSuccessAction());
    }

    private void removeDialogsAction() {
        removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);

    }

    private void addActions() {
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);
        updateBroadcastActionList();
    }

    private void openPushDialogIfPossible() {
        CoreSharedHelper sharedHelper = CoreSharedHelper.getInstance();
        if (sharedHelper.needToOpenDialog()) {
            QBChatDialog chatDialog = DataManager.getInstance().getQBChatDialogDataManager()
                    .getByDialogId(sharedHelper.getPushDialogId());
            QMUser user = QMUserService.getInstance().getUserCache().get((long) sharedHelper.getPushUserId());
            if(chatDialog != null) {
                startDialogActivity(chatDialog, user);
            }
        }
    }

    public void initChatFields() {
        AppSession.load();
    }

    private void initFields() {
        Log.d(TAG, "initFields()");

        importFriendsSuccessAction = new ImportFriendsSuccessAction();
        importFriendsFailAction = new ImportFriendsFailAction();

        appInitialized = true;
        initChatFields();

        TextView username = headerLayout.findViewById(R.id.navigation_header_username);
        username.setText(DISharedPreferences.getInstance(this).getUsername());
    }

    private void gotoAuth() {
        Intent myIntent = new Intent(this, SignInActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(myIntent);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        displaySelectedScreenByMenuItem(menuItem);

                        return true;
                    }
                });

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                     //   onBack();
                        Log.i("home", "back");
                    }
                });
    }

    private void drawBottomBar() {
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                displaySelectedScreenByTabID(tabId);
            }
        });
    }

    public void hideBottomBar() {
        bottomBar.setVisibility(View.GONE);
    }

    public void showBottomBar() {
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.setDefaultTab(R.id.nav_home);
        nvDrawer.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
        } else {
            onBack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isSupportMessage) {
            getMenuInflater().inflate(R.menu.support, menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            gotoAuth();
            return true;
        }

        if (id == R.id.action_support) {
            gotoDenningSupport();
        }

        if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startImportFriends() {
        ImportFriendsHelper importFriendsHelper = new ImportFriendsHelper(MainActivity.this);

        importFriendsHelper.startGetFriendsListTask(false);

        hideProgress();
    }

    private class ImportFriendsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsSuccessAction();
        }
    }

    private class ImportFriendsFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            performImportFriendsFailAction(bundle);
        }
    }

    private void performImportFriendsSuccessAction() {
        appSharedHelper.saveUsersImportInitialized(true);
        hideProgress();
    }

    private void performImportFriendsFailAction(Bundle bundle) {
        performImportFriendsSuccessAction();
    }

    private void displaySelectedScreen(int position) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (position) {
            case 1:
                fragment = new Home();
                break;
            case 2:
                fragment = new Add();
                break;
            case 3:
                fragment = new Dashboard();
                break;
            case 4:
                fragment = new DenningMessage();
                break;
        }

        if (position == 2 || position == 3 || position == 4) {
            if(checkToLoginChat()){
                replaceFragment(fragment);
            }
        } else {
            replaceFragment(fragment);
        }

        mDrawer.closeDrawers();
    }

    private void displaySelectedScreenByMenuItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        int[] tabList = {R.id.nav_home, R.id.nav_add, R.id.nav_dashboard, R.id.nav_message};
        Boolean isBottomNavSelected = false;
        for (int i : tabList) {
            if (i == menuItem.getItemId()) {
                bottomBar.setDefaultTab(menuItem.getItemId());
                isBottomNavSelected = true;
            }
        }
        if (!isBottomNavSelected) {
            displaySelectedScreenByTabID(menuItem.getItemId());
        }
    }

    private void displaySelectedScreenByTabID(int tabID) {
        if (DISharedPreferences.getInstance().isSessionExpired) {
            DIAlert.showSimpleAlert(this, R.string.warning_title, R.string.alert_session_expired);
            return;
        }
        nvDrawer.setCheckedItem(tabID);
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        int position = -1;
        switch (tabID) {
            case R.id.nav_home:
                position = 1;
                fragment = new Home();
                break;
            case R.id.nav_add:
                position = 2;
                fragment = new Add();
                break;
            case R.id.nav_dashboard:
                position = 3;
                fragment = new Dashboard();
                break;
            case R.id.nav_message:
                position = 4;
                fragment = new DenningMessage();
                break;
            case R.id.nav_our_products:
                gotoOurProducts();
                break;
            case R.id.nav_help:
                gotoHelp();
                break;
            case R.id.nav_settings:
                gotoSettings();
                break;
            case R.id.nav_contact_us:
                gotoContactUs();
                break;
            case R.id.nav_terms_of_uses:
                gotoTermsOfUses();
                break;
            case R.id.nav_log_out:
                logout();
                break;
        }

        if (position == 2 || position == 3 || position == 4) {
            if(checkToLoginChat()){
                replaceFragment(fragment);
            }
        } else {
            replaceFragment(fragment);
        }

        mDrawer.closeDrawers();
    }

    private void gotoOurProducts() {
        AdsViwerActivity.start(this, "http://www.denning.com.my");
    }

    private void gotoHelp() {
        AdsViwerActivity.start(this, "http://denning.com.my/?page_id=198");
    }

    private void gotoSettings() {
        if(checkToLoginChat()){
//            SettingsActivity.startForResult(MainActivity.this);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, DIConstants.LOGOUT_REQUEST_CODE);
        } else {
            DIAlert.showSimpleAlertAndGotoLogin(MainActivity.this, R.string.access_restricted, R.string.access_restriceted_text);
        }
    }

    private void gotoContactUs() {
        if(checkToLoginChat()){
            DenningSupportActivity.start(MainActivity.this);
        } else {
            DIAlert.showSimpleAlertAndGotoLogin(MainActivity.this, R.string.access_restricted, R.string.access_restriceted_text);
        }
    }

    private void gotoTermsOfUses() {
        showProgress();
        NetworkManager.getInstance().sendPublicGetRequest(DIConstants.kDIAgreementGetUrl, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                manageError(error);
            }
        });
    }

    private void manageError(String error) {
        hideProgress();
        DIAlert.showSimpleAlert(this, R.string.warning_title, error);
    }

    private void manageResponse(JsonElement jsonElement) {
        hideProgress();
        Agreement agreement = new Gson().fromJson(jsonElement, Agreement.class);
        UserAgreementActivity.start(this, agreement.strItemDescription, "main");
    }

    private void logout() {
        DIAlert.showConfirm(this, R.string.warning_title, R.string.dlg_confirm, new MyCallbackInterface() {
            @Override
            public void nextFunction() {
                _logout();
            }

            @Override
            public void nextFunction(JsonElement jsonElement) {
            }
        });
    }

    private void _logout() {
        if (isChatInitializedAndUserLoggedIn()) {
            showProgress();
            ServiceManager.getInstance().logout(new Subscriber<Void>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    ErrorUtils.showError(MainActivity.this, e);
                    hideProgress();
                }

                @Override
                public void onNext(Void aVoid) {
                    mainLogOut();
                }
            });
        }
    }

    public void mainLogOut() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", DISharedPreferences.getInstance().getEmail());
        NetworkManager.getInstance().sendPublicPutRequest(DIConstants.LOGOUT_URL, jsonObject, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                hideProgress();
                DISharedPreferences.getInstance().clearData();
                changeUI();
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                hideProgress();
                ErrorUtils.showError(getApplicationContext(), error);
            }
        });
    }

    private void changeUI() {
        if (bottomBar != null) {
            displaySelectedScreen(1);

            // Change the bottom bar & navigation drawer
            if (!isChatInitializedAndUserLoggedIn() && !DISharedPreferences.getInstance(this).isLoggedIn()) {
                bottomBar.setItems(R.xml.bottombar_tabs_unregistered);
                nvDrawer.getMenu().findItem(R.id.nav_add).setVisible(false);
                nvDrawer.getMenu().findItem(R.id.nav_dashboard).setVisible(false);
            } else {
                bottomBar.setItems(R.xml.bottombar_tabs);
                nvDrawer.getMenu().findItem(R.id.nav_add).setVisible(true);
                nvDrawer.getMenu().findItem(R.id.nav_dashboard).setVisible(true);
            }
        }
    }

    private boolean checkToLoginChat() {
        boolean isValid = true;
        if (!DISharedPreferences.getInstance(this).isLoggedIn() /*|| !isChatInitializedAndUserLoggedIn()*/) {
            isValid = false;
            mDrawer.closeDrawers();
            DIAlert.showSimpleAlertWithCompletion(this, R.string.warning_title, R.string.login_required, new MyCallbackInterface() {
                @Override
                public void nextFunction() {
                    bottomBar.selectTabAtPosition(0);
                    nvDrawer.getMenu().getItem(0).setChecked(true);
                    SignInActivity.start(MainActivity.this);
                }

                @Override
                public void nextFunction(JsonElement jsonElement) {
                }
            });
        }

        return isValid;
    }

    protected void updateBadge() {
        super.updateBadge();

        // Calc push count
        List<QBChatDialog> qbDialogsList = DataManager.getInstance().getQBChatDialogDataManager().getAllSorted();
        int totalCount = 0;
        for (QBChatDialog qbDialog : qbDialogsList) {
            totalCount += qbDialog.getUnreadMessageCount();
        }

        bottomBar.getTabWithId(R.id.nav_message).setBadgeCount(totalCount);
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(BACK_STACK_ROOT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        displaySelectedScreen(position);
        return false;
    }

    public void showDenningSupport() {
        isSupportMessage = true;
        invalidateOptionsMenu();
    }

    public void hideDennigSupport() {
        isSupportMessage = false;
        invalidateOptionsMenu();
    }

    // Open Denning support chat
    public void gotoDenningSupport() {
        DenningSupportActivity.start(this);
    }
}

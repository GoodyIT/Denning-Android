package it.denning.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.service.QBService;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import it.denning.App;
import it.denning.R;
import it.denning.navigation.message.DenningMessage;
import it.denning.navigation.message.FavoriteContact;
import it.denning.navigation.message.DenningContact;
import it.denning.navigation.message.GroupMessage;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.bridges.ActionBarBridge;
import it.denning.utils.bridges.ConnectionBridge;
import it.denning.utils.bridges.LoadingBridge;
import it.denning.utils.bridges.SnackbarBridge;
import it.denning.utils.listeners.ServiceConnectionListener;
import it.denning.utils.listeners.UserStatusChangingListener;

public abstract class BaseFragment extends Fragment implements UserStatusChangingListener, ServiceConnectionListener {

    protected App app;
    protected BaseActivity baseActivity;
    protected BaseActivity.FailAction failAction;
    protected ConnectionBridge connectionBridge;
    protected ActionBarBridge actionBarBridge;
    protected LoadingBridge loadingBridge;
    protected SnackbarBridge snackbarBridge;

    protected QBFriendListHelper friendListHelper;
    protected QBChatHelper chatHelper;

    protected QBService service;


    @Optional
    @OnClick(R.id.button_message)
    void onTapMessage() {
        baseActivity.showProgress();
        changeFragment(new DenningMessage());
    }

    @Optional
    @OnClick(R.id.button_contact)
    void onTapClientTab() {
        changeFragment(new DenningContact());
    }

    @Optional
    @OnClick(R.id.button_group)
    void onTapStaffTab() {
        changeFragment(new GroupMessage());
    }

    @Optional
    @OnClick(R.id.button_favorite)
    void onTapFavoriteTab() {
        changeFragment(new FavoriteContact());
    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("BaseFragment", "onAttach");

        if (activity instanceof BaseActivity) {
            Log.d("BaseFragment", "activity instanceof BaseActivity");
            baseActivity = (BaseActivity) activity;
            service = baseActivity.getService();
            friendListHelper = baseActivity.getFriendListHelper();
            chatHelper = baseActivity.getChatHelper();
        }

        if (activity instanceof ConnectionBridge) {
            connectionBridge = (ConnectionBridge) activity;
        }

        if (activity instanceof ActionBarBridge) {
            actionBarBridge = (ActionBarBridge) activity;
        }

        if (activity instanceof LoadingBridge) {
            loadingBridge = (LoadingBridge) activity;
        }

        if (activity instanceof SnackbarBridge) {
            snackbarBridge = (SnackbarBridge) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseFragment", "onCreate");

        addListeners();

        app = App.getInstance();
        failAction = baseActivity.getFailAction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("BaseFragment", "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("BaseFragment", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    public void initActionBar() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("BaseFragment", "onResume()");
        initActionBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BaseFragment", "onDestroy()");
        removeListeners();
    }

    private void addListeners() {
        baseActivity.addFragmentUserStatusChangingListener(this);
        baseActivity.addFragmentServiceConnectionListener(this);
    }

    private void removeListeners() {
        baseActivity.removeFragmentUserStatusChangingListener(this);
        baseActivity.removeFragmentServiceConnectionListener(this);
    }

    protected boolean isExistActivity() {
        return ((!isDetached()) && (baseActivity != null));
    }

    protected void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onChangedUserStatus(int userId, boolean online) {
        // nothing by default
    }

    @Override
    public void onConnectedToService(QBService service) {
        // nothing by default
    }
}
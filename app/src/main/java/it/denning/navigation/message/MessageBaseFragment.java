package it.denning.navigation.message;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.qb.commands.friend.QBAddFriendCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.managers.DialogNotificationDataManager;
import com.quickblox.q_municate_db.managers.base.BaseManager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_cache.QMUserCacheImpl;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DISharedPreferences;
import it.denning.loaders.DialogsListLoader;
import it.denning.navigation.message.utils.MessageBaseAdapter;
import it.denning.navigation.message.utils.OnMessageFavClickListener;
import it.denning.navigation.message.utils.OnMessageItemClickListener;
import it.denning.ui.activities.chats.NewGroupDialogActivity;
import it.denning.ui.fragments.base.BaseFragment;
import it.denning.ui.fragments.search.GlobalSearchFragment;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.ToastUtils;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class MessageBaseFragment extends BaseFragment implements
        OnMessageItemClickListener,
        OnMessageFavClickListener,
        RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.message_segmented)
    SegmentedGroup messageTopFilter;

    protected static final String TAG = MessageBaseFragment.class.getSimpleName();

    protected List<QBChatDialog> myDialogList;
    protected MessageBaseAdapter messageAdapter;
    protected DataManager dataManager;
    protected QBUser qbUser;

    public static OkHttpClient client = null;
    public static final MediaType jsonType
            = MediaType.parse("application/json; charset=utf-8");


    protected Handler handler = new Handler();

    @Override
    public void onMessageItemClick(View view, int position, QMUser user) {
        startPrivateChatActivity(user);
    }

    @Override
    public void onFavClick(View view, int sectionIndex, int itemIndex, QMUser user, boolean isAdd) {
        baseActivity.showProgress();
        if (isAdd) {
            addFav(user);
        } else {
            removeFav(user);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int checkedIndex = 0;
        switch (checkedId) {
            case R.id.message_filter_all:
                checkedIndex = 0;
                break;
            case R.id.message_filter_colleagues:
                checkedIndex = 1;
                break;
            case R.id.message_filter_clients:
                checkedIndex = 2;
                break;
            case R.id.message_filter_matters:
                checkedIndex = 3;
                break;
        }

        messageAdapter.filterTag(checkedIndex);
    }

    public enum State {started, stopped, finished}

    @BindView(R.id.empty_list_textview)
    public TextView emptyListTextView;

    @BindView(R.id.chat_list)
    public RecyclerView chatListView;

    @BindView(R.id.message_search)
    SearchView searchView;

    @Optional
    @OnClick(R.id.button_new_chat)
    void onTapNewChat()
    {
        addChat();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity)getActivity()).titleView.setText("Message");
        ((MainActivity)getActivity()).hideBottomBar();

        Log.d(TAG, "onCreate");
        initFields();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
//        actionBarBridge.setActionBarUpButtonEnabled(false);

        loadingBridge.hideActionBarProgress();
        messageTopFilter.setOnCheckedChangeListener(this);
    }

    void initFields() {
        dataManager = DataManager.getInstance();
        qbUser = AppSession.getSession().getUser();
        if (client == null) {
            client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).retryOnConnectionFailure(true).writeTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
        }
    }

    void addFav(final QMUser user) {
        new Thread(new Runnable() {
            public void run() {
                String url  = DIConstants.CHAT_ADD_FAVORITE;
                JsonObject json = new JsonObject();
                json.addProperty("email", AppSession.getSession().getUser().getEmail());
                json.addProperty("favourite", user.getEmail());

                Request request = new Request.Builder()
                        .url(url)
                        .header("Content-Type", "application/json")
                        .addHeader("webuser-sessionid", "{334E910C-CC68-4784-9047-0F23D37C9CF9}")
                        .addHeader("webuser-id", "iPhone@denning.com.my")
                        .post(RequestBody.create(jsonType, json.toString()))
                        .build();

                try {
                    Call call = client.newCall(request);
                    Response response = call.execute();

                    parseResponseForFav(response, user);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void removeFav(final QMUser user) {
        new Thread(new Runnable() {
            public void run() {
                String url  = DIConstants.CHAT_ADD_FAVORITE;
                JsonObject json = new JsonObject();
                json.addProperty("email", AppSession.getSession().getUser().getEmail());
                json.addProperty("favourite", user.getEmail());

                Request request = new Request.Builder()
                        .url(url)
                        .header("Content-Type", "application/json")
                        .addHeader("webuser-sessionid", "{334E910C-CC68-4784-9047-0F23D37C9CF9}")
                        .addHeader("webuser-id", "iPhone@denning.com.my")
                        .post(RequestBody.create(jsonType, json.toString()))
                        .build();

                try {
                    Call call = client.newCall(request);
                    Response response = call.execute();

                    parseResponseForFav(response, user);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void parseResponseForFav(final Response response, final QMUser user) {
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                baseActivity.hideProgress();
                if (response != null && response.isSuccessful()) {
                    messageAdapter.changeFavoriteData(user);
                    messageAdapter.notifyAllSectionsDataSetChanged();
                } else {
                    ErrorUtils.showError(getContext(), response.message());
                }
            }
        });

    }

    public void initSearchView() {
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                KeyboardUtils.hideKeyboard(baseActivity);
                search("");
                return true;
            }
        });
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                KeyboardUtils.hideKeyboard(baseActivity);
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
    }

    public void search(String query) {
        messageAdapter.filterItem(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (messageAdapter != null) {
            checkVisibilityEmptyLabel();
        }
        if (messageAdapter != null) {
            messageAdapter.notifyAllSectionsDataSetChanged();
        }

        baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy() removeActions and deleteObservers");
        removeActions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult " + requestCode + ", data= " + data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectedToService(QBService service) {
        if (chatHelper == null) {
            if (service != null) {
                chatHelper = (QBChatHelper) service.getHelper(QBService.CHAT_HELPER);
            }
        }
    }

    private void checkVisibilityEmptyLabel() {
        emptyListTextView.setVisibility(messageAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void removeActions() {
        baseActivity.removeAction(QBServiceConsts.ADD_FRIEND_SUCCESS_ACTION);
        baseActivity.updateBroadcastActionList();
    }

    protected void addActions() {
        baseActivity.addAction(QBServiceConsts.ADD_FRIEND_SUCCESS_ACTION, new AddFriendSuccessAction());
        baseActivity.updateBroadcastActionList();
    }

    protected void addChat() {
        boolean hasFriends = !dataManager.getFriendDataManager().getAll().isEmpty();
        if (!hasFriends) {
            ToastUtils.longToast(R.string.new_message_no_friends_for_new_message);
        } else {
            NewGroupDialogActivity.start(getContext());
        }
    }

    protected void startPrivateChatActivity(QMUser user) {
        baseActivity.selectedUser = user;
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        boolean isPending = dataManager.getUserRequestDataManager().getUserRequestById(user.getId()) != null;
        if (isPending) {
            baseActivity.showProgress();
            QBAddFriendCommand.start(baseActivity, user.getId());
        }
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            baseActivity.startPrivateChat(chatDialog);
        } else {
            if (baseActivity.checkNetworkAvailableWithError()) {
                baseActivity.showProgress();
                QBCreatePrivateChatCommand.start(getContext(), user);
            }
        }
    }

    public void checkEmptyList(int listSize) {
//        if (listSize > 0) {
//            emptyListTextView.setVisibility(View.GONE);
//        } else {
//            emptyListTextView.setVisibility(View.VISIBLE);
//        }
    }

    private class AddFriendSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            int userId = bundle.getInt(QBServiceConsts.EXTRA_FRIEND_ID);
            baseActivity.hideProgress();

            QMUser addedUser = QMUserService.getInstance().getUserCache().get((long)userId);
//            startPrivateChatActivity(addedUser);
        }
    }
}

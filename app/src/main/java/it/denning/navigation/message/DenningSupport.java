package it.denning.navigation.message;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.helper.CollectionsUtil;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.qb.commands.chat.QBDeleteChatCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogsCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.managers.DialogNotificationDataManager;
import com.quickblox.q_municate_db.managers.base.BaseManager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_user_cache.QMUserCacheImpl;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.loaders.DialogsListLoader;
import it.denning.navigation.message.utils.OnMessageDeleteListener;
import it.denning.ui.activities.chats.GroupDialogActivity;
import it.denning.ui.activities.chats.NewGroupDialogActivity;
import it.denning.ui.activities.chats.PrivateDialogActivity;
import it.denning.ui.adapters.chats.DialogsListAdapter;
import it.denning.ui.fragments.base.BaseLoaderFragment;
import it.denning.utils.KeyboardUtils;
import it.denning.utils.ToastUtils;

public class DenningSupport extends BaseLoaderFragment<List<DialogWrapper>> implements
        OnMessageDeleteListener {
    private static final String TAG = DenningSupport.class.getSimpleName();
    private static final int LOADER_ID = DenningSupport.class.hashCode();

    @BindView(R.id.chats_listview)
    ListView dialogsListView;

    @BindView(R.id.message_search)
        SearchView searchView;

    private DialogsListAdapter dialogsListAdapter;
    private SwipeLayout swipeLayout;

    private DataManager dataManager;
    private QBUser qbUser;
    private Observer commonObserver;
    private DialogsListLoader dialogsListLoader;
    private Queue<LoaderConsumer> loaderConsumerQueue = new ConcurrentLinkedQueue<>();

            Set<String> dialogsIdsToUpdate;

    protected Handler handler = new Handler();
    private State updateDialogsProcess;

    private LoginChatCompositeSuccessAction loginChatCompositeSuccessAction;
    private DeleteDialogSuccessAction deleteDialogSuccessAction;
    private DeleteDialogFailAction deleteDialogFailAction;
    private LoadChatsSuccessAction loadChatsSuccessAction;
    private LoadChatsFailedAction loadChatsFailedAction;
    private UpdateDialogSuccessAction updateDialogSuccessAction;

    @Override
    public void onMessageDelete(View v, QBChatDialog dialog) {
            deleteDialog(dialog);
            }

    enum State {started, stopped, finished}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.d(TAG, "onCreate");
            initFields();
            initChatsDialogs();
            initActions();
            addObservers();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_support_message, container, false);
            activateButterKnife(view);
            registerForContextMenu(dialogsListView);

            dialogsListView.setAdapter(dialogsListAdapter);

            initSearchView();

            return view;
        }

        @Override
        public void initActionBar() {
            super.initActionBar();
            loadingBridge.hideActionBarProgress();
        }

        private void initFields() {
            dataManager = DataManager.getInstance();
            commonObserver = new CommonObserver();
            qbUser = AppSession.getSession().getUser();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, view, menuInfo);
            MenuInflater menuInflater = baseActivity.getMenuInflater();
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            QBChatDialog chatDialog = dialogsListAdapter.getItem(adapterContextMenuInfo.position).getChatDialog();
            if(chatDialog.getType().equals(QBDialogType.GROUP)){
                menuInflater.inflate(R.menu.dialogs_list_group_ctx_menu, menu);
            } else{
                menuInflater.inflate(R.menu.dialogs_list_private_ctx_menu, menu);
            }
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch (item.getItemId()) {
                case R.id.action_delete:
                    if (baseActivity.checkNetworkAvailableWithError() && checkDialogsLoadFinished()) {
                        QBChatDialog chatDialog = dialogsListAdapter.getItem(adapterContextMenuInfo.position).getChatDialog();
                        deleteDialog(chatDialog);
                    }
                    break;
            }
            return true;
        }

        private void initSearchView() {
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
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        private void search(String query) {
            dialogsListAdapter.filterItem(query);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.d(TAG, "onActivityCreated");
            addActions();
            if (dialogsListAdapter.getCount() == 0){
                initDataLoader(LOADER_ID);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "onResume");
            if (dialogsListAdapter != null) {
                checkVisibilityEmptyLabel();
            }
            if (dialogsListAdapter != null) {
                dialogsListAdapter.notifyDataSetChanged();
            }
            checkLoaderConsumerQueue();
            checkUpdateDialogs();

            if (State.finished == updateDialogsProcess){
                baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
            }
        }

        private void checkUpdateDialogs() {
    //        check if needs update dialog list
            if(!CollectionsUtil.isEmpty(dialogsIdsToUpdate)) {
                QBLoadDialogByIdsCommand.start(getContext(), new ArrayList<>(dialogsIdsToUpdate));
                dialogsIdsToUpdate.clear();
            }
        }

        private void checkLoaderConsumerQueue() {
    //        check if the update process can be proceeded
            if(State.stopped == updateDialogsProcess) {
                Log.d(TAG, "checkLoaderConsumerQueue proceeded updateDialogsListFromQueue");
                updateDialogsListFromQueue();
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause()");
        }

        @Override
        public void onStop(){
            super.onStop();
            setStopStateUpdateDialogsProcess();
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
            deleteObservers();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.i(TAG, "onActivityResult " + requestCode + ", data= " + data);
            if (PICK_DIALOG == requestCode && data != null) {
                String dialogId = data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID);
                checkDialogsIds(dialogId);
                updateOrAddDialog(dialogId, data.getBooleanExtra(QBServiceConsts.EXTRA_DIALOG_UPDATE_POSITION, false));
            } else if (CREATE_DIALOG == requestCode && data != null) {
                updateOrAddDialog(data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID), true);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        private void setStopStateUpdateDialogsProcess() {
            if(updateDialogsProcess != State.finished) {
                updateDialogsProcess = State.stopped;
            }
        }

        private boolean checkDialogsLoadFinished() {
            if (updateDialogsProcess != State.finished) {
                ToastUtils.shortToast(R.string.chat_service_is_initializing);
                return false;
            }
            return true;
        }

        private void checkDialogsIds(String dialogId) {
    //       no need update dialog cause it's already updated
            if (dialogsIdsToUpdate != null) {
                for(String dialogIdToUpdate : dialogsIdsToUpdate){
                    if(dialogIdToUpdate.equals(dialogId)){
                        dialogsIdsToUpdate.remove(dialogId);
                        break;
                    }
                }
            }
        }

        private void updateOrAddDialog(String dialogId, boolean updatePosition) {
            QBChatDialog qbChatDialog = dataManager.getQBChatDialogDataManager().getByDialogId(dialogId);
            DialogWrapper dialogWrapper = new DialogWrapper(getContext(), dataManager, qbChatDialog);
            Log.i(TAG, "updateOrAddDialog dialogWrapper= " + dialogWrapper.getTotalCount());
            if (updateDialogsProcess == State.finished || dialogsListAdapter.getCount() != 0) {
                dialogsListAdapter.updateItem(dialogWrapper);
            }

            if(updatePosition) {
                dialogsListAdapter.moveToFirstPosition(dialogWrapper);
            }

            int start = dialogsListView.getFirstVisiblePosition();
            for (int i = start, j = dialogsListView.getLastVisiblePosition(); i <= j; i++) {
                DialogWrapper result = (DialogWrapper) dialogsListView.getItemAtPosition(i);
                if (result.getChatDialog().getDialogId().equals(dialogId)) {
                    View view = dialogsListView.getChildAt(i - start);
                    dialogsListView.getAdapter().getView(i, view, dialogsListView);
                    break;
                }
            }
        }

        @OnItemClick(R.id.chats_listview)
        void startChat(int position) {
            QBChatDialog chatDialog = dialogsListAdapter.getItem(position).getChatDialog();

            if (!baseActivity.checkNetworkAvailableWithError() && isFirstOpeningDialog(chatDialog.getDialogId())) {
                return;
            }

            if (QBDialogType.PRIVATE.equals(chatDialog.getType())) {
                startPrivateChatActivity(chatDialog);
            } else {
                startGroupChatActivity(chatDialog);
            }
        }

        private boolean isFirstOpeningDialog(String dialogId){
            return !dataManager.getMessageDataManager().getTempMessagesByDialogId(dialogId).isEmpty();
        }

        @Override
        public void onConnectedToService(QBService service) {
            if (chatHelper == null) {
                if (service != null) {
                    chatHelper = (QBChatHelper) service.getHelper(QBService.CHAT_HELPER);
                }
            }
        }

        @Override
        protected Loader<List<DialogWrapper>> createDataLoader() {
            dialogsListLoader = new DialogsListLoader(getActivity(), dataManager);
            return dialogsListLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<DialogWrapper>> loader, List<DialogWrapper> dialogsList) {
            updateDialogsProcess = State.started;
            Log.d(TAG, "onLoadFinished!!! dialogsListLoader.isLoadCacheFinished() " + dialogsListLoader.isLoadCacheFinished());
            if (dialogsListLoader.isLoadCacheFinished()){
                //clear queue after loading all dialogs from cache before updating all dialogs from REST
                loaderConsumerQueue.clear();
            }else {
                updateDialogsListFromQueue();
            }

            baseActivity.hideProgress();
            updateDialogsAdapter(dialogsList);

            checkEmptyList(dialogsListAdapter.getCount());

            if (!baseActivity.isDialogLoading()) {
                baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
            }

    //        startForResult load dialogs from REST when finished loading from cache
            if (dialogsListLoader.isLoadCacheFinished()) {
                if (!QBLoginChatCompositeCommand.isRunning()) {
                    QBLoadDialogsCommand.start(getContext(), true);
                }
            }
        }

        private ArrayList<DialogWrapper> filterDenningSupportGroupChatDialog(List<DialogWrapper> dialogsList) {
            ArrayList<DialogWrapper> groupDialogs = new ArrayList<>();
            for (DialogWrapper dialogWrapper : dialogsList) {
                if (QBDialogType.GROUP.equals(dialogWrapper.getChatDialog().getType()) && DIHelper.getTag(dialogWrapper.getChatDialog()).equals(DIConstants.kChatDenningTag)) {
                    groupDialogs.add(dialogWrapper);
                }
            }
            return groupDialogs;
        }

        private void updateDialogsAdapter(List<DialogWrapper> dialogsList) {
            if (dialogsListLoader.isLoadAll()) {
                dialogsListAdapter.setNewData(filterDenningSupportGroupChatDialog(dialogsList));
            } else {
                dialogsListAdapter.addNewData(filterDenningSupportGroupChatDialog(dialogsList));
            }

            if(dialogsListLoader.isLoadRestFinished()) {
                loadingBridge.hideActionBarProgress();
                updateDialogsProcess = State.finished;
                Log.d(TAG, "onLoadFinished isLoadRestFinished updateDialogsProcess= " + updateDialogsProcess);
            }
            Log.d(TAG, "onLoadFinished dialogsListAdapter.getCount() " + dialogsListAdapter.getCount());
        }

        private void addChat() {
            boolean hasFriends = !dataManager.getFriendDataManager().getAll().isEmpty();
            if (isFriendsLoading()) {
                ToastUtils.longToast(R.string.chat_service_is_initializing);
            } else if (!hasFriends) {
                ToastUtils.longToast(R.string.new_message_no_friends_for_new_message);
            } else {
                NewGroupDialogActivity.start(getContext());
            }
        }

        private boolean isFriendsLoading(){
            return QBLoginChatCompositeCommand.isRunning();
        }

        private void checkVisibilityEmptyLabel() {
    //        emptyListTextView.setVisibility(dialogsListAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        }

        private void addObservers() {
            dataManager.getQBChatDialogDataManager().addObserver(commonObserver);
            dataManager.getMessageDataManager().addObserver(commonObserver);
            dataManager.getDialogOccupantDataManager().addObserver(commonObserver);
            dataManager.getDialogNotificationDataManager().addObserver(commonObserver);
            ((Observable) QMUserService.getInstance().getUserCache()).addObserver(commonObserver);
        }

        private void deleteObservers() {
            if (dataManager != null) {
                dataManager.getQBChatDialogDataManager().deleteObserver(commonObserver);
                dataManager.getMessageDataManager().deleteObserver(commonObserver);
                dataManager.getDialogOccupantDataManager().deleteObserver(commonObserver);
                dataManager.getDialogNotificationDataManager().deleteObserver(commonObserver);
                ((Observable)QMUserService.getInstance().getUserCache()).deleteObserver(commonObserver);
            }
        }

        private void removeActions() {
            baseActivity.removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
            baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION);
            baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION);
            baseActivity.removeAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION);
            baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
            baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION);

            baseActivity.updateBroadcastActionList();
        }

        private void addActions() {
            baseActivity.addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, loginChatCompositeSuccessAction);
            baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION, deleteDialogSuccessAction);
            baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION, deleteDialogFailAction);
            baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, loadChatsSuccessAction);
            baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION, loadChatsFailedAction);
            baseActivity.addAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION, updateDialogSuccessAction);

            baseActivity.updateBroadcastActionList();
        }

        private void initChatsDialogs() {
            List<DialogWrapper> dialogsList = new ArrayList<>();
            dialogsListAdapter = new DialogsListAdapter(baseActivity, dialogsList, this);
        }

        private void initActions() {
            loginChatCompositeSuccessAction = new LoginChatCompositeSuccessAction();
            deleteDialogSuccessAction = new DeleteDialogSuccessAction();
            deleteDialogFailAction = new DeleteDialogFailAction();
            loadChatsSuccessAction = new LoadChatsSuccessAction();
            loadChatsFailedAction = new LoadChatsFailedAction();
            updateDialogSuccessAction = new UpdateDialogSuccessAction();
        }

        private void startPrivateChatActivity(QBChatDialog chatDialog) {
            boolean isFriend = DataManager.getInstance().getFriendDataManager().getByUserId(
                    qbUser.getId()) != null;
            if (!isFriend) {

            }
            List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                    .getDialogOccupantsListByDialogId(chatDialog.getDialogId());
            QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(qbUser), occupantsList);

            if (!TextUtils.isEmpty(chatDialog.getDialogId())) {
                PrivateDialogActivity.startForResult(this, opponent, chatDialog, PICK_DIALOG);
            }
        }

        private void startGroupChatActivity(QBChatDialog chatDialog) {
            GroupDialogActivity.startForResult(this, chatDialog, PICK_DIALOG);
        }

        private void updateDialogsList(int startRow, int perPage) {
    //        logic for correct behavior of pagination loading dialogs
    //        we can't fire onChangedData until we have incomplete loader task in queue
            if (!loaderConsumerQueue.isEmpty()) {
                Log.d(TAG, "updateDialogsList loaderConsumerQueue.add");
                loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
                return;
            }

    //        if Loader is in loading process, we don't fire onChangedData, cause we do not want interrupt current load task
            if (dialogsListLoader.isLoading) {
                Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
                loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
            } else {
    //        we don't have tasks in queue, so load dialogs by pages
                if(!isResumed()) {
                    loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
                } else {
                    Log.d(TAG, "updateDialogsList onChangedData");
                    dialogsListLoader.setPagination(startRow, perPage);
                    onChangedData();
                }
            }
        }

        private void updateDialogsList() {
            if(!loaderConsumerQueue.isEmpty()){
                Log.d(TAG, "updateDialogsList loaderConsumerQueue.add");
                loaderConsumerQueue.offer(new LoaderConsumer(true));
                return;
            }

            if(dialogsListLoader.isLoading) {
                Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
                loaderConsumerQueue.offer(new LoaderConsumer(true));
            } else {
    //        load All dialogs
                if(!isResumed()) {
                    Log.d(TAG, "updateDialogsList !isResumed() offer");
                    loaderConsumerQueue.offer(new LoaderConsumer(true));
                } else {
                    dialogsListLoader.setLoadAll(true);
                    onChangedData();
                }
            }
        }

        private void updateDialogsListFromQueue() {
            if(!loaderConsumerQueue.isEmpty()) {
                LoaderConsumer consumer = loaderConsumerQueue.poll();
                handler.post(consumer);
            }
        }

    private class LoaderConsumer implements Runnable {
        boolean loadAll;
        int startRow;
        int perPage;

        LoaderConsumer(boolean loadAll) {
            this.loadAll = loadAll;
        }

        LoaderConsumer(int startRow, int perPage) {
            this.startRow = startRow;
            this.perPage = perPage;
        }

        @Override
        public void run() {
            Log.d(TAG, "LoaderConsumer onChangedData");
            dialogsListLoader.setLoadAll(loadAll);
            dialogsListLoader.setPagination(startRow, perPage);
            onChangedData();
        }
    }

        public void deleteDialog(QBChatDialog chatDialog) {
            if(chatDialog == null || chatDialog.getDialogId() == null){
                return;
            }

            baseActivity.showProgress();
            QBDeleteChatCommand.start(baseActivity, chatDialog.getDialogId(), chatDialog.getType().getCode());
        }

        private void checkEmptyList(int listSize) {
    //        if (listSize > 0) {
    //            emptyListTextView.setVisibility(View.GONE);
    //        } else {
    //            emptyListTextView.setVisibility(View.VISIBLE);
    //        }
        }

        private void updateDialogIds(String dialogId) {
            if(dialogsIdsToUpdate == null){
                dialogsIdsToUpdate = new HashSet<>();
            }
            dialogsIdsToUpdate.add(dialogId);
        }

    private class LoginChatCompositeSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            Log.i(TAG, "LoginChatCompositeSuccessAction bundle= " + bundle);
            if (dialogsListLoader.isLoadCacheFinished()) {
                QBLoadDialogsCommand.start(getContext(), true);
            }
        }
    }

    private class DeleteDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            baseActivity.hideProgress();
            dialogsListAdapter.removeItem(bundle.getString(QBServiceConsts.EXTRA_DIALOG_ID));
        }
    }

    private class DeleteDialogFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ToastUtils.longToast(R.string.dlg_internet_connection_error);
            baseActivity.hideProgress();
        }
    }

    private class LoadChatsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Log.d(TAG, "LoadChatsSuccessAction bundle= " + bundle);
            if(bundle != null) {
                if (isLoadPerPage(bundle)) {
                    updateDialogsList(bundle.getInt(ConstsCore.DIALOGS_START_ROW), bundle.getInt(ConstsCore.DIALOGS_PER_PAGE));
                } else if(bundle.getBoolean(ConstsCore.DIALOGS_UPDATE_ALL)) {
                    updateDialogsList();
                }
            }
        }

        private boolean isLoadPerPage(Bundle bundle) {
            return bundle.get(ConstsCore.DIALOGS_START_ROW) != null && bundle.get(ConstsCore.DIALOGS_PER_PAGE) != null;
        }
    }

    private class LoadChatsFailedAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            Log.d(TAG, "LoadChatsFailedAction bundle= " + bundle);
            updateDialogsProcess = State.finished;
        }
    }

    private class UpdateDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            baseActivity.hideProgress();
            Log.d(TAG, "UpdateDialogSuccessAction action UpdateDialogSuccessAction bundle= " + bundle);
            if(bundle != null) {
                updateDialogIds((String) bundle.get(QBServiceConsts.EXTRA_DIALOG_ID));
            }
        }
    }

    private class CommonObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.d(TAG, "CommonObserver update " + observable + " data= " + data.toString());
            if (data != null) {
                if (data instanceof Bundle) {
                    String observeKey = ((Bundle) data).getString(BaseManager.EXTRA_OBSERVE_KEY);
                    Log.i(TAG, "CommonObserver update, key="+observeKey);
                    if (observeKey.equals(dataManager.getMessageDataManager().getObserverKey())
                            && (((Bundle) data).getSerializable(BaseManager.EXTRA_OBJECT) instanceof Message)){
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);
                        Log.i(TAG, "CommonObserver action =  " + action);
                        Message message = getObjFromBundle((Bundle) data);
                        if (message.getDialogOccupant() != null && message.getDialogOccupant().getDialog() != null) {
                            boolean updatePosition = message.isIncoming(AppSession.getSession().getUser().getId());
                            Log.i(TAG, "CommonObserver getMessageDataManager updatePosition= " + updatePosition);

                            updateOrAddDialog(message.getDialogOccupant().getDialog().getDialogId(), action == BaseManager.CREATE_ACTION);
                        }
                    }
                    else if (observeKey.equals(dataManager.getQBChatDialogDataManager().getObserverKey())) {
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);
                        if (action == BaseManager.DELETE_ACTION
                                || action == BaseManager.DELETE_BY_ID_ACTION) {
                            return;
                        }
                        Dialog dialog = getObjFromBundle((Bundle) data);
                        if (dialog != null) {
                            updateOrAddDialog(dialog.getDialogId(), false);
                        }
                    } else if (observeKey.equals(dataManager.getDialogOccupantDataManager().getObserverKey())) {
                        DialogOccupant dialogOccupant = getObjFromBundle((Bundle) data);
                        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
                            updateOrAddDialog(dialogOccupant.getDialog().getDialogId(), false);
                        }
                    } else if(observeKey.equals(dataManager.getDialogNotificationDataManager().getObserverKey())) {
                        Bundle observableData = (Bundle) data;
                        DialogNotification dialogNotification = (DialogNotification) observableData.getSerializable(DialogNotificationDataManager.EXTRA_OBJECT);
                        if(dialogNotification != null) {
                            updateOrAddDialog(dialogNotification.getDialogOccupant().getDialog().getDialogId(), true);
                        }
                    }
                } else if (data.equals(QMUserCacheImpl.OBSERVE_KEY)) {
                    Log.d(TAG, "else if (data.equals(QMUserCacheImpl.OBSERVE_KEY))");
                        updateDialogsList();
                }
            }
        }
    }

    private <T> T getObjFromBundle(Bundle data){
        return (T)(data).getSerializable(BaseManager.EXTRA_OBJECT);
    }
}

package it.denning.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.OnClick;
import it.denning.R;
import it.denning.general.DIHelper;
import it.denning.ui.adapters.chats.GroupChatMessagesAdapter;
import it.denning.utils.ChatDialogUtils;

public class GroupDialogActivity extends BaseDialogActivity {

    private static final String TAG = GroupDialogActivity.class.getSimpleName();

    public static void start(Context context, ArrayList<QMUser> friends) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friends);
        context.startActivity(intent);
    }

    public static void start(Context context, QBChatDialog chatDialog) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, QBChatDialog chatDialog, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualizeCurrentDialogInfo();
    }

    private void actualizeCurrentDialogInfo() {
        if (currentChatDialog != null) {
            QBLoadDialogByIdsCommand.start(this, new ArrayList<>(Collections.singletonList(currentChatDialog.getDialogId())));
        }
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new GroupChatMessagesAdapter(this, currentChatDialog, combinationMessagesList);
    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(messagesAdapter));
        messagesRecyclerView.setAdapter(messagesAdapter);

        scrollMessagesToBottom(0);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.group_dialog_menu, menu);
//        return true;
//    }

    @OnClick(R.id.toolbar)
    void openProfile(View view) {
        if (DIHelper.canChangeGroupInfoforDialog(currentChatDialog)) {
            GroupDialogDetailsActivity.start(this, currentChatDialog.getDialogId());
        }
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (GroupDialogDetailsActivity.UPDATE_DIALOG_REQUEST_CODE == requestCode && GroupDialogDetailsActivity.RESULT_DELETE_GROUP == resultCode) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        return null;
    }

    @Override
    protected void updateMessagesList() {

    }

    @Override
    protected void checkMessageSendingPossibility() {
        checkMessageSendingPossibility(isNetworkAvailable());
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_group_details:
//                GroupDialogDetailsActivity.start(this, currentChatDialog.getDialogId());
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void updateActionBar() {
        if (isNetworkAvailable() && currentChatDialog != null) {
            setActionBarTitle(ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager));
            checkActionBarLogo(currentChatDialog.getPhoto(), R.drawable.placeholder_group);
        }
    }

    @Override
    protected void initFields() {
        super.initFields();
        if (currentChatDialog != null) {
            title = ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager);
        }
    }

    public void sendMessage(View view) {
        sendMessage();
    }

}
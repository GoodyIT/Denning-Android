package it.denning.ui.activities.chats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.qb.commands.chat.QBUpdateGroupDialogCommand;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnTextChanged;
import info.hoang8f.android.segmented.SegmentedGroup;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.utils.ToastUtils;
import it.denning.utils.listeners.simple.SimpleActionModeCallback;

public class GroupDialogDetailPositionActivity extends BaseLoggableActivity {
    public static final int UPDATE_DIALOG_REQUEST_CODE = 100;

    @BindView(R.id.name_textview)
    public EditText groupNameEditText;

    @BindView(R.id.group_tag_segmented)
    SegmentedGroup groupTagSegment;

    @BindView(R.id.position_textview)
    public EditText groupPositionEditText;

    private Object actionMode;
    private QBChatDialog qbDialog;
    private String groupNameCurrent, groupTagCurrent, groupPositionCurrent;
    private String groupNameOld, groupTagOld, groupPositionOld;
    private List<DialogNotification.Type> currentNotificationTypeList;
    private DataManager dataManager;
    private String dialogId;

    private String[] tagArray = {DIConstants.kChatColleaguesTag, DIConstants.kChatClientsTag, DIConstants.kChatMattersTag, DIConstants.kChatDenningTag};
    private int[] tagIDArray = { R.id.type_colleagues, R.id.type_clients, R.id.type_matters, R.id.type_denning };
    private int curTagIndex = 0;

    public static void start(Activity context, String dialogId) {
        Intent intent = new Intent(context, GroupDialogDetailPositionActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, dialogId);
        context.startActivityForResult(intent, UPDATE_DIALOG_REQUEST_CODE);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_group_dialog_details_position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();

        addActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillUIWithData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (actionMode != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            groupNameEditText.setText(groupNameCurrent != null ? groupNameCurrent : qbDialog.getName());
            groupPositionEditText.setText(groupPositionCurrent != null ? groupPositionCurrent : groupPositionOld);
            ((ActionMode) actionMode).finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initFields() {
        title = getString(R.string.dialog_details_title);
        dataManager = DataManager.getInstance();
        dialogId = (String) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);
        currentNotificationTypeList = new ArrayList<>();

        if (DIHelper.canChangeGroupTagforDialog(qbDialog)) {
            groupTagSegment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.type_colleagues:
                            curTagIndex = 0;
                            break;
                        case R.id.type_clients:
                            curTagIndex = 1;
                            break;
                        case R.id.type_matters:
                            curTagIndex = 2;
                        case R.id.type_denning:
                            curTagIndex = 3;
                            break;
                    }
                    if (!groupTagOld.equals(groupTagCurrent)) {
                        startAction();
                    }
                }
            });
        }

        if (DIHelper.canChangeGroupTagforDialog(qbDialog)) {
            groupPositionEditText.setEnabled(true);
        } else {
            groupPositionEditText.setEnabled(false);
        }
    }

    private void fillUIWithData() {
        updateDialog();

        updateOldGroupData();

        groupNameEditText.setText(groupNameCurrent != null ? groupNameCurrent : qbDialog.getName());
        groupPositionEditText.setText(groupPositionCurrent != null ? groupPositionCurrent : groupPositionOld);
        int index = Arrays.asList(tagArray).indexOf(groupTagOld);
        groupTagCurrent = groupTagOld;
        groupTagSegment.check(tagIDArray[index]);
    }

    private void updateDialog() {
        if (qbDialog == null){
            qbDialog = dataManager.getQBChatDialogDataManager().getByDialogId(dialogId);
        }

        qbDialog.initForChat(QBChatService.getInstance());
    }

    private void addActions() {
        addAction(QBServiceConsts.UPDATE_GROUP_DIALOG_SUCCESS_ACTION, new UpdateGroupDialogSuccessAction());
        addAction(QBServiceConsts.UPDATE_GROUP_DIALOG_FAIL_ACTION, new UpdateGroupFailAction());
    }

    private void removeActions() {
        removeAction(QBServiceConsts.UPDATE_GROUP_DIALOG_SUCCESS_ACTION);
        removeAction(QBServiceConsts.UPDATE_GROUP_DIALOG_FAIL_ACTION);
    }

    @OnTextChanged(R.id.name_textview)
    void onGroupNameTextChanged(CharSequence s) {
        if (groupNameOld != null) {
            if (!s.toString().equals(groupNameOld)) {
                startAction();
            }
        }
    }

    @OnTextChanged(R.id.position_textview)
    void onGroupPositionChanged(CharSequence s) {
        if (groupPositionOld != null) {
            if (!s.toString().equals(groupPositionOld)) {
                startAction();
            }
        }
    }

    private void startAction() {
        if (actionMode != null) {
            return;
        }
        actionMode = startSupportActionMode(new ActionModeCallback());
    }

    private void updateCurrentData() {
        groupNameCurrent = groupNameEditText.getText().toString();
        groupPositionCurrent = groupPositionEditText.getText().toString();
        groupTagCurrent = tagArray[curTagIndex];
    }

    private void checkForSaving() {
        updateCurrentData();
        if (isGroupDataChanged()) {
            saveChanges();
        }
    }

    private boolean isGroupDataChanged() {
        return !groupNameCurrent.equals(groupNameOld) || !groupPositionCurrent.equals(groupPositionOld) || !groupTagCurrent.equals(groupTagOld);
    }

    private void saveChanges() {
        if (!isUserDataCorrect()) {
            ToastUtils.longToast(R.string.dialog_details_name_not_entered);
            return;
        }

        if (!qbDialog.getName().equals(groupNameCurrent)) {
            qbDialog.setName(groupNameCurrent);

            currentNotificationTypeList.add(DialogNotification.Type.NAME_DIALOG);
        }

        if (!groupPositionOld.equals(groupPositionCurrent)) {
            QBDialogCustomData customData = qbDialog.getCustomData();
            customData.put("position", groupPositionCurrent);
            customData.put("tag", groupTagOld);
            qbDialog.setCustomData(customData);

            currentNotificationTypeList.add(DialogNotification.Type.POSITION_DIALOG);
        }

        if (!groupTagOld.equals(groupTagCurrent)) {
            QBDialogCustomData customData = qbDialog.getCustomData();
            customData.put("tag", groupTagCurrent);
            customData.put("position", groupPositionOld);
            qbDialog.setCustomData(customData);

            currentNotificationTypeList.add(DialogNotification.Type.TAG_DIALOG);
        }

        updateGroupDialog();

        showProgress();
    }

    private void updateGroupDialog() {
        QBUpdateGroupDialogCommand.start(this, qbDialog, null);
    }

    private void sendNotificationToGroup(boolean leavedFromDialog) {
        for (DialogNotification.Type messagesNotificationType : currentNotificationTypeList) {
            try {
                QBChatDialog localDialog = qbDialog;
                if (qbDialog != null) {
                    localDialog = dataManager.getQBChatDialogDataManager().getByDialogId(qbDialog.getDialogId());
                }
                chatHelper.sendGroupMessageToFriends(localDialog, messagesNotificationType,
                        null, leavedFromDialog);
            } catch (QBResponseException e) {
                ErrorUtils.logError(e);
                hideProgress();
            }
        }
        currentNotificationTypeList.clear();
    }

    private boolean isUserDataCorrect() {
        return !TextUtils.isEmpty(groupNameCurrent);
    }

    private void updateOldGroupData() {
        groupNameOld = qbDialog.getName();
        groupPositionOld = DIHelper.getPosition(qbDialog);
        groupTagOld = DIHelper.getTag(qbDialog);
    }

    private void resetGroupData() {
        groupNameEditText.setText(groupNameOld);
        groupPositionEditText.setText(groupPositionOld);
        int index = Arrays.asList(tagArray).indexOf(groupTagOld);
        groupTagSegment.check(tagIDArray[index]);
    }

    private class ActionModeCallback extends SimpleActionModeCallback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.done_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_done:
                    if (checkNetworkAvailableWithError()) {
                        checkForSaving();
                    } else {
                        onDestroyActionMode(actionMode);
                    }
                    actionMode.finish();
                    return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    }


    private class UpdateGroupDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            qbDialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);

            updateCurrentData();
            updateOldGroupData();
            fillUIWithData();

            sendNotificationToGroup(false);
            hideProgress();
        }
    }

    private class UpdateGroupFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Exception exception = (Exception) bundle.getSerializable(QBServiceConsts.EXTRA_ERROR);
            if (exception != null) {
                ToastUtils.longToast(exception.getMessage());
            }

            resetGroupData();
            hideProgress();
        }
    }
}

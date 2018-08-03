package it.denning.ui.adapters.chats;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.denning.MainActivity;
import it.denning.R;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.navigation.message.utils.OnMessageDeleteListener;
import it.denning.search.utils.OnItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.adapters.base.BaseListAdapter;
import it.denning.ui.views.roundedimageview.RoundedImageView;

public class DialogsListAdapter extends BaseListAdapter<DialogWrapper> {

    private static final String TAG = DialogsListAdapter.class.getSimpleName();
    private  OnMessageDeleteListener itemClickListener;

    private String query = "";

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList) {
        super(baseActivity, objectsList);
    }

    public DialogsListAdapter(BaseActivity baseActivity, List<DialogWrapper> objectsList, OnMessageDeleteListener itemClickListener) {
        super(baseActivity, objectsList);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        DialogWrapper dialogWrapper = getItem(position);
        QBChatDialog currentDialog = dialogWrapper.getChatDialog();

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog, null);

            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = (RoundedImageView) convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.lastMessageTextView = (TextView) convertView.findViewById(R.id.last_message_textview);
            viewHolder.unreadMessagesTextView = (TextView) convertView.findViewById(
                    R.id.unread_messages_textview);
            viewHolder.btnDelete = convertView.findViewById(R.id.btn_delete);
            viewHolder.swipeLayout = (SwipeLayout)convertView.findViewById(R.id.swipe_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (QBDialogType.PRIVATE.equals(currentDialog.getType())) {
            QMUser opponentUser = dialogWrapper.getOpponentUser();
            if (opponentUser.getFullName() != null) {
                viewHolder.nameTextView.setText(opponentUser.getFullName());
                displayAvatarImage(opponentUser.getAvatar(), viewHolder.avatarImageView);
            } else {
                viewHolder.nameTextView.setText(resources.getString(R.string.deleted_user));
            }
        } else {
            viewHolder.nameTextView.setText(currentDialog.getName());
            viewHolder.avatarImageView.setImageResource(R.drawable.placeholder_group);
            displayGroupPhotoImage(currentDialog.getPhoto(), viewHolder.avatarImageView);
        }

        long totalCount = dialogWrapper.getTotalCount();

        if (totalCount > ConstsCore.ZERO_INT_VALUE) {
            viewHolder.unreadMessagesTextView.setText(totalCount + ConstsCore.EMPTY_STRING);
            viewHolder.unreadMessagesTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.unreadMessagesTextView.setVisibility(View.GONE);
        }

        viewHolder.btnDelete.setOnClickListener(onDeleteListener(position, viewHolder));

        viewHolder.lastMessageTextView.setText(dialogWrapper.getLastMessage());

        return convertView;
    }

    public void updateItem(DialogWrapper dlgWrapper) {
        Log.i(TAG, "updateItem = " + dlgWrapper.getChatDialog().getUnreadMessageCount());
        int position = -1;
        for (int i = 0; i < objectsList.size() ; i++) {
            DialogWrapper dialogWrapper  = objectsList.get(i);
            if (dialogWrapper.getChatDialog().getDialogId().equals(dlgWrapper.getChatDialog().getDialogId())){
                position = i;
                break;
            }
        }

        if (position != -1) {
            Log.i(TAG, "find position = " + position);
            objectsList.set(position, dlgWrapper);
        } else {
            addNewItem(dlgWrapper);
        }
    }

    public void moveToFirstPosition(DialogWrapper dlgWrapper) {
        if (objectsList.size() != 0 && !objectsList.get(0).equals(dlgWrapper)) {
            objectsList.remove(dlgWrapper);
            objectsList.add(0, dlgWrapper);
            notifyDataSetChanged();
        }
    }

    public void removeItem(String dialogId) {
        for (DialogWrapper dialogWrapper : objectsList) {
            if (dialogWrapper.getChatDialog().getDialogId().equals(dialogId)){
                objectsList.remove(dialogWrapper);
                copyOfObjectsList.remove(dialogWrapper);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void filterTag(int index) {
        ArrayList<DialogWrapper> clientsDialogs = new ArrayList<>();
        ArrayList<DialogWrapper> staffDialogs = new ArrayList<>();
        ArrayList<DialogWrapper> matterDialogs = new ArrayList<>();

        for (DialogWrapper dialogWrapper : copyOfObjectsList) {
            String tag = DIHelper.getTag(dialogWrapper.getChatDialog());
            if (tag.equals(DIConstants.kChatColleaguesTag)) {
                staffDialogs.add(dialogWrapper);
            } else if (tag.equals(DIConstants.kChatClientsTag)) {
                clientsDialogs.add(dialogWrapper);
            } else if (tag.equals(DIConstants.kChatMattersTag)) {
                matterDialogs.add(dialogWrapper);
            }
        }

        switch (index) {
            case 0:
                setNewData(copyOfObjectsList);
                break;
            case 1:
                setNewData(staffDialogs);
                break;
            case 2:
                setNewData(clientsDialogs);
                break;
            case 3:
                setNewData(matterDialogs);
                break;
        }

        filterItem(query);
    }

    public void filterItem(String query) {
        this.query = query;
        if (query.trim().length() == 0) {
            setNewData(copyOfObjectsList);
            return;
        }
        query = query.toLowerCase();
        ArrayList<DialogWrapper> newList = new ArrayList<>();

        for (DialogWrapper dialogWrapper : copyOfObjectsList) {
            QBChatDialog dialog = dialogWrapper.getChatDialog();
            if (dialog.getName().toLowerCase().contains(query) || dialogWrapper.getLastMessage().toLowerCase().contains(query)) {
                newList.add(dialogWrapper);
            }
        }

        setNewData(newList);
    }

    private static class ViewHolder {

        public RoundedImageView avatarImageView;
        public TextView nameTextView;
        public TextView lastMessageTextView;
        public TextView unreadMessagesTextView;
        public Button btnDelete;
        private SwipeLayout swipeLayout;

    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.close();
                itemClickListener.onMessageDelete(v, objectsList.get(position).getChatDialog());
            }
        };
    }
}
package it.denning.ui.adapters.chats;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter;
import com.quickblox.ui.kit.chatmessage.adapter.utils.LinkUtils;

import java.io.File;
import java.util.List;

import it.denning.R;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.ColorUtils;
import it.denning.utils.StringUtils;


public class GroupChatMessagesAdapter extends BaseChatMessagesAdapter {
    private static final String TAG = GroupChatMessagesAdapter.class.getSimpleName();
    private ColorUtils colorUtils;

    public GroupChatMessagesAdapter(BaseActivity baseActivity, QBChatDialog chatDialog,
                                    List<CombinationMessage> chatMessages) {
        super(baseActivity, chatDialog, chatMessages);
        colorUtils = new ColorUtils();
    }

    @Override
    protected void onBindViewCustomHolder(QBMessageViewHolder holder, CombinationMessage chatMessage, int position) {
        int valueType = this.getItemViewType(position);
        switch(valueType) {
            case TYPE_REQUEST_MESSAGE:
                RequestsViewHolder viewHolder = (RequestsViewHolder) holder;
                boolean notificationMessage = chatMessage.getNotificationType() != null;

                if (notificationMessage) {
                    viewHolder.messageTextView.setText(chatMessage.getBody());
                    viewHolder.timeTextMessageTextView.setText(getDate(chatMessage.getCreatedDate()));
                } else {
                    Log.d(TAG, "onBindViewCustomHolder else");
                }

                if (!State.READ.equals(chatMessage.getState()) && isIncoming(chatMessage) && baseActivity.isNetworkAvailable()) {
                    updateMessageState(chatMessage, chatDialog);
                }
                break;

            case CUSTOM_FILE_INCOMING_MESSAGE:
                onBindViewAttachLeftHolder((QBMessagesAdapter.ImageAttachHolder)holder, chatMessage, position);
                break;

            case CUSTOM_FILE_OUTGOING_MESSAGE:
                onBindViewAttachRightHolder((QBMessagesAdapter.ImageAttachHolder)holder, chatMessage, position);
                break;
        }
    }



    @Override
    protected QBMessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        QBMessageViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_REQUEST_MESSAGE:
                viewHolder = new RequestsViewHolder(inflater.inflate(R.layout.item_notification_message, parent, false));
                break;

            case CUSTOM_FILE_INCOMING_MESSAGE:
                viewHolder = new ImageAttachHolder(inflater.inflate(com.quickblox.ui.kit.chatmessage.adapter.R.layout.list_item_attach_left, parent, false), com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_image_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_progressbar_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_text_time_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_signs_attach);
                break;

            case CUSTOM_FILE_OUTGOING_MESSAGE:
                viewHolder = new ImageAttachHolder(inflater.inflate(com.quickblox.ui.kit.chatmessage.adapter.R.layout.list_item_attach_right, parent, false), com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_image_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_progressbar_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_text_time_attach, com.quickblox.ui.kit.chatmessage.adapter.R.id.msg_signs_attach);
                break;

            default:
                viewHolder = null;
        }

        return  viewHolder;
    }

    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, CombinationMessage chatMessage, int position) {
        holder.timeTextMessageTextView.setVisibility(View.GONE);

        String senderName;
        senderName = chatMessage.getDialogOccupant().getUser().getFullName();

        TextView opponentNameTextView = (TextView) holder.itemView.findViewById(R.id.opponent_name_text_view);
        opponentNameTextView.setTextColor(colorUtils.getRandomTextColorById(chatMessage.getDialogOccupant().getUser().getId()));
        opponentNameTextView.setText(senderName);

        TextView customMessageTimeTextView = (TextView) holder.itemView.findViewById(R.id.custom_msg_text_time_message);
        customMessageTimeTextView.setText(getDate(chatMessage.getDateSent()));

        updateMessageState(chatMessage, chatDialog);

        View customViewTopLeft = holder.itemView.findViewById(R.id.custom_view_top_left);
        ViewGroup.LayoutParams layoutParams = customViewTopLeft.getLayoutParams();

        final List<String> urlsList = LinkUtils.extractUrls(chatMessage.getBody());
        if (!urlsList.isEmpty()) {
            layoutParams.width = (int) context.getResources().getDimension(com.quickblox.ui.kit.chatmessage.adapter.R.dimen.link_preview_width);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        customViewTopLeft.setLayoutParams(layoutParams);
        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }
}
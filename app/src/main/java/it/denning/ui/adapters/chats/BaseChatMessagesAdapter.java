package it.denning.ui.adapters.chats;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.qb.commands.chat.QBUpdateStatusMessageCommand;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;
import com.quickblox.users.model.QBUser;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.general.MySimpleCallback;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.utils.DateUtils;
import it.denning.utils.FileUtils;
import it.denning.utils.StringUtils;

public class BaseChatMessagesAdapter extends QBMessagesAdapter<CombinationMessage> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = BaseChatMessagesAdapter.class.getSimpleName();
    protected static final int TYPE_REQUEST_MESSAGE = 100;
    protected static final int CUSTOM_FILE_INCOMING_MESSAGE = 200;
    protected static final int CUSTOM_FILE_OUTGOING_MESSAGE = 201;
    protected QBUser currentUser;
    protected final BaseActivity baseActivity;
    protected FileUtils fileUtils;
    private QBChatAttachClickListener attachClickListener;

    private DataManager dataManager;
    protected QBChatDialog chatDialog;

    BaseChatMessagesAdapter(BaseActivity baseActivity, QBChatDialog dialog, List<CombinationMessage> chatMessages) {
        super(baseActivity, chatMessages);
        this.baseActivity = baseActivity;
        chatDialog = dialog;
        currentUser = AppSession.getSession().getUser();
        fileUtils = new FileUtils();
        dataManager = DataManager.getInstance();
    }

    public void setFileAttachClickListener(QBChatAttachClickListener attachClickListener) {
        this.attachClickListener = attachClickListener;
    }

    public void removeFileAttachClickListener() {
        this.attachClickListener = null;
    }

    @Override
    public long getHeaderId(int position) {
        CombinationMessage combinationMessage = getItem(position);
        return DateUtils.toShortDateLong(combinationMessage.getCreatedDate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_chat_sticky_header_date, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView headerTextView = (TextView) view.findViewById(R.id.header_date_textview);
        CombinationMessage combinationMessage = getItem(position);
        headerTextView.setText(DateUtils.toTodayYesterdayFullMonthDate(combinationMessage.getCreatedDate()));
    }


    @Override
    public int getItemViewType(int position) {
        CombinationMessage combinationMessage = getItem(position);
        if (combinationMessage.getNotificationType() != null) {
            return TYPE_REQUEST_MESSAGE;
        }
        CombinationMessage chatMessage = getItem(position);
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        if(attachments != null && !attachments.isEmpty()) {
            QBAttachment attachment = this.getQBAttach(position);
            if ("file".equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? CUSTOM_FILE_INCOMING_MESSAGE : CUSTOM_FILE_OUTGOING_MESSAGE;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    protected RequestListener getRequestListener(QBMessageViewHolder holder, int position) {
        CombinationMessage chatMessage = getItem(position);

        return new ImageRequestListener((ImageAttachHolder) holder, isIncoming(chatMessage));
    }

    @Override
    public String obtainAvatarUrl(int valueType, CombinationMessage chatMessage) {
        return chatMessage.getDialogOccupant().getUser().getAvatar();
    }

    private void resetAttachUI(ImageAttachHolder viewHolder) {
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_bubble_background), View.GONE);
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_image_avatar), View.GONE);
    }

    protected void showAttachUI(ImageAttachHolder viewHolder, boolean isIncoming) {
        if (isIncoming) {
            setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_image_avatar), View.VISIBLE);
        }
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_bubble_background), View.VISIBLE);
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public boolean isEmpty() {
        return chatMessages.size() == 0;
    }

    @Override
    protected boolean isIncoming(CombinationMessage chatMessage) {
        return chatMessage.isIncoming(currentUser.getId());
    }

    protected void updateMessageState(CombinationMessage message, QBChatDialog dialog) {
        if (!State.READ.equals(message.getState()) && baseActivity.isNetworkAvailable()) {
            message.setState(State.READ);
            Log.d(TAG, "updateMessageState");

            message.setState(State.READ);
            QBUpdateStatusMessageCommand.start(baseActivity, dialog, message, true);
        }
    }

    @Override
    protected void onBindViewAttachLeftAudioHolder(AudioAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        super.onBindViewAttachLeftAudioHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftVideoHolder(VideoAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        super.onBindViewAttachLeftVideoHolder(holder, chatMessage, position);
    }

    public void addAllInBegin(List<CombinationMessage> collection) {
        chatMessages.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
    }

    public void addAllInEnd(List<CombinationMessage> collection) {
        chatMessages.addAll(collection);
        notifyItemRangeInserted(chatMessages.size() - collection.size(), chatMessages.size());
    }

    public void setList(List <CombinationMessage> collection, boolean notifyDataChanged){
        chatMessages = collection;
        if (notifyDataChanged) {
            this.notifyDataSetChanged();
        }
    }

    public class ImageRequestListener implements RequestListener<String, GlideBitmapDrawable> {
        private ImageAttachHolder viewHolder;
        private Bitmap loadedImageBitmap;
        private boolean isIncoming;

        public ImageRequestListener(ImageAttachHolder viewHolder, boolean isIncoming) {
            this.viewHolder = viewHolder;
            this.isIncoming = isIncoming;
        }

        @Override
        public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
            updateUIAfterLoading();
            resetAttachUI(viewHolder);
            Log.d(TAG, "onLoadingFailed");
            return false;
        }

        @Override
        public boolean onResourceReady(GlideBitmapDrawable loadedBitmap, String imageUri, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            initMaskedImageView(loadedBitmap.getBitmap());
            fileUtils.checkExistsFile(imageUri, loadedBitmap.getBitmap());
            return false;
        }

        protected void initMaskedImageView(Bitmap loadedBitmap) {
            loadedImageBitmap = loadedBitmap;
            viewHolder.attachImageView.setImageBitmap(loadedImageBitmap);

            showAttachUI(viewHolder, isIncoming);

            updateUIAfterLoading();
        }

        private void updateUIAfterLoading() {
            if (viewHolder.attachmentProgressBar != null) {
                setViewVisibility(viewHolder.attachmentProgressBar, View.GONE);
            }
        }
    }

    protected static class RequestsViewHolder extends QBMessageViewHolder {
        @Nullable
        @BindView(R.id.message_textview)
        TextView messageTextView;

        @Nullable
        @BindView(R.id.time_text_message_textview)
        TextView timeTextMessageTextView;

        @Nullable
        @BindView(R.id.accept_friend_imagebutton)
        ImageView acceptFriendImageView;

        @Nullable
        @BindView(R.id.divider_view)
        View dividerView;

        @Nullable
        @BindView(R.id.reject_friend_imagebutton)
        ImageView rejectFriendImageView;


        public RequestsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

    protected void displayFileAttachment(final QBMessagesAdapter.ImageAttachHolder holder, int position) {
        QBAttachment attachment = this.getQBAttach(position);
        int resId = StringUtils.getAttachImage(attachment.getContentType());
        int preferredImageWidth = (int)this.context.getResources().getDimension(com.quickblox.ui.kit.chatmessage.adapter.R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int)this.context.getResources().getDimension(com.quickblox.ui.kit.chatmessage.adapter.R.dimen.attach_image_height_preview);
        Glide.with(this.context).load(resId).override(preferredImageWidth, preferredImageHeight).dontTransform().error(com.quickblox.ui.kit.chatmessage.adapter.R.drawable.ic_error).into(((QBMessagesAdapter.BaseImageAttachHolder)holder).attachImageView);

        final CombinationMessage chatMessage = getItem(position);

        FileUtils.saveAttachmentToFile(attachment, new MySimpleCallback() {
            @Override
            public void next(String value) {
                showAttachUI(holder, isIncoming(chatMessage));
                if (holder.attachmentProgressBar != null) {
                    setViewVisibility(holder.attachmentProgressBar, View.GONE);
                }
            }
        });
    }

    @Override
    protected void onBindViewAttachLeftHolder(QBMessagesAdapter.ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
        updateMessageState(chatMessage, chatDialog);
        int valueType = this.getItemViewType(position);
        if (valueType == CUSTOM_FILE_INCOMING_MESSAGE || valueType == CUSTOM_FILE_OUTGOING_MESSAGE) {
            this.setDateSentAttach(holder, chatMessage);
            String avatarUrl = this.obtainAvatarUrl(valueType, chatMessage);
            if(avatarUrl != null) {
                this.displayAvatarImage(avatarUrl, holder.avatar);
            }
            displayFileAttachment(holder, position);
            QBAttachment attachment = this.getQBAttach(position);
            setFileAttachClickListener(this.attachClickListener, holder, attachment, position);
        } else {
            super.onBindViewAttachLeftHolder(holder, chatMessage, position);
        }
    }

    @Override
    protected void onBindViewAttachRightHolder(QBMessagesAdapter.ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
        int valueType = this.getItemViewType(position);
        if (valueType == CUSTOM_FILE_INCOMING_MESSAGE || valueType == CUSTOM_FILE_OUTGOING_MESSAGE) {
            this.setDateSentAttach(holder, chatMessage);
            String avatarUrl = this.obtainAvatarUrl(valueType, chatMessage);
            if(avatarUrl != null) {
                this.displayAvatarImage(avatarUrl, holder.avatar);
            }
            displayFileAttachment(holder, position);
            QBAttachment attachment = this.getQBAttach(position);
            setFileAttachClickListener(this.attachClickListener, holder, attachment, position);
        } else {
            super.onBindViewAttachRightHolder(holder, chatMessage, position);
        }
    }

    protected void setFileAttachClickListener(QBChatAttachClickListener listener, QBMessagesAdapter.QBMessageViewHolder holder, QBAttachment qbAttachment, int position) {
        if(listener != null) {
            holder.bubbleFrame.setOnClickListener(new QBItemClickListenerFilter(this.attachClickListener, qbAttachment, position));
        }
    }

    protected class QBItemClickListenerFilter implements View.OnClickListener {
        protected int position;
        protected QBAttachment attachment;
        protected QBChatAttachClickListener chatAttachClickListener;

        QBItemClickListenerFilter(QBChatAttachClickListener qbChatAttachClickListener, QBAttachment attachment, int position) {
            this.position = position;
            this.attachment = attachment;
            this.chatAttachClickListener = qbChatAttachClickListener;
        }

        public void onClick(View view) {
            this.chatAttachClickListener.onLinkClicked(this.attachment, this.position);
        }
    }
}
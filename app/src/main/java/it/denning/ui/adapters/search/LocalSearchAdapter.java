package it.denning.ui.adapters.search;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.q_municate_core.models.DialogSearchWrapper;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.List;

import butterknife.BindView;
import it.denning.R;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.adapters.base.BaseClickListenerViewHolder;
import it.denning.ui.adapters.base.BaseFilterAdapter;
import it.denning.ui.adapters.base.BaseViewHolder;
import it.denning.ui.views.roundedimageview.RoundedImageView;
import it.denning.utils.ChatDialogUtils;
import it.denning.utils.DateUtils;
import it.denning.utils.helpers.TextViewHelper;
import it.denning.utils.image.ImageLoaderUtils;

public class LocalSearchAdapter extends BaseFilterAdapter<DialogSearchWrapper, BaseClickListenerViewHolder<DialogSearchWrapper>> {

    private DataManager dataManager;
    private QBFriendListHelper qbFriendListHelper;

    public LocalSearchAdapter(BaseActivity baseActivity, List<DialogSearchWrapper> list) {
        super(baseActivity, list);
    }

    @Override
    protected boolean isMatch(DialogSearchWrapper item, String query) {
        String chatTitle = ChatDialogUtils.getTitleForChatDialog(item.getChatDialog(), dataManager);
        return chatTitle != null && chatTitle.toLowerCase().contains(query);
    }

    @Override
    public BaseClickListenerViewHolder<DialogSearchWrapper> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this, layoutInflater.inflate(R.layout.item_local_search, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<DialogSearchWrapper> baseClickListenerViewHolder, int position) {
        DialogSearchWrapper dialogWrapper = getItem(position);
        ViewHolder viewHolder = (ViewHolder) baseClickListenerViewHolder;


        String label;

        if (QBDialogType.PRIVATE.equals(dialogWrapper.getChatDialog().getType())) {
            QMUser opponentUser = dialogWrapper.getOpponentUser();
            setOnlineStatus(viewHolder, opponentUser);
            displayAvatarImage(opponentUser.getAvatar(), viewHolder.avatarImageView);
            viewHolder.titleTextView.setText(opponentUser.getFullName());
        } else {
            label = dialogWrapper.getLabel();
            viewHolder.labelTextView.setText(label);
            viewHolder.labelTextView.setTextColor(resources.getColor(R.color.dark_gray));
            displayGroupPhotoImage(dialogWrapper.getChatDialog().getPhoto(), viewHolder.avatarImageView);
            viewHolder.titleTextView.setText(dialogWrapper.getChatDialog().getName());
        }

        if (!TextUtils.isEmpty(query)) {
            TextViewHelper.changeTextColorView(baseActivity, viewHolder.titleTextView, query);
        }
    }

    public void removeItemByDialogId(String dialogId){
        for (DialogSearchWrapper dialogSearchWrapper : getAllItems()){
            if (dialogSearchWrapper.getChatDialog().getDialogId().equals(dialogId)){
                removeItem(dialogSearchWrapper);
            }
        }
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }

    private void setOnlineStatus(ViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && user.getId()!= null && qbFriendListHelper.isUserOnline(user.getId());

        if (online) {
            viewHolder.labelTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.labelTextView.setTextColor(resources.getColor(R.color.green));
        } else {
            viewHolder.labelTextView.setText(user.getLastRequestAt() == null ? null : resources.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
            viewHolder.labelTextView.setTextColor(resources.getColor(R.color.dark_gray));
        }
    }

    private void displayGroupPhotoImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView,
                ImageLoaderUtils.UIL_GROUP_AVATAR_DISPLAY_OPTIONS);
    }

    protected static class ViewHolder extends BaseViewHolder<DialogSearchWrapper> {

        @BindView(R.id.avatar_imageview)
        RoundedImageView avatarImageView;

        @BindView(R.id.title_textview)
        TextView titleTextView;

        @BindView(R.id.label_textview)
        TextView labelTextView;

        public ViewHolder(LocalSearchAdapter adapter, View view) {
            super(adapter, view);
        }
    }
}
package it.denning.ui.adapters.chats;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import java.util.List;

import it.denning.App;
import it.denning.R;
import it.denning.general.DIAlert;
import it.denning.general.DIConstants;
import it.denning.general.DIHelper;
import it.denning.navigation.message.utils.OnMessageItemClickListener;
import it.denning.ui.activities.base.BaseActivity;
import it.denning.ui.adapters.base.BaseListAdapter;
import it.denning.ui.views.roundedimageview.RoundedImageView;
import it.denning.utils.DateUtils;
import it.denning.utils.listeners.UserOperationListener;

public class GroupDialogOccupantsAdapter extends BaseListAdapter<QMUser> {

    private OnMessageItemClickListener userOperationListener;
    private QBFriendListHelper qbFriendListHelper;
    private QBChatDialog qbChatDialog;

    public GroupDialogOccupantsAdapter(BaseActivity baseActivity, OnMessageItemClickListener userOperationListener, List<QMUser> objectsList) {
        super(baseActivity, objectsList);
        this.userOperationListener = userOperationListener;
    }

    public void setQbChatDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialog = qbChatDialog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        QMUser user = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog_friend_role, null);
            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = (RoundedImageView) convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.onlineStatusTextView = (TextView) convertView.findViewById(R.id.status_textview);
            viewHolder.roleBtn = (Button) convertView.findViewById(R.id.role_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fullName;
        if (isFriendValid(user)) {
            fullName = user.getFullName();
        } else {
            fullName = String.valueOf(user.getId());
        }
        viewHolder.nameTextView.setText(fullName);

        setStatus(viewHolder, user);
//        viewHolder.roleBtn.setVisibility(isFriend(user) ? View.GONE : View.VISIBLE);
        String role = DIHelper.getCurrentUserRole(user, qbChatDialog);
        int color = App.getInstance().getResources().getColor(R.color.md_deep_purple_100);
        switch (role) {
            case DIConstants.kRoleDenningTag:
                role = "Denning";
                color = App.getInstance().getResources().getColor(R.color.md_deep_purple_500);
                break;
            case DIConstants.kRoleAdminTag:
                role = "Admin";
                color = App.getInstance().getResources().getColor(R.color.colorAccent);
                break;
            case DIConstants.kRoleStaffTag:
                role = "Staff";
                color = App.getInstance().getResources().getColor(R.color.baby_blue);
                break;
            case DIConstants.kRoleReaderTag:
                role = "Reader";
                color = App.getInstance().getResources().getColor(R.color.babyGreen);
                break;
            default:
                role = "Client";
                color = App.getInstance().getResources().getColor(R.color.yellow_green);
                break;

        }
        viewHolder.roleBtn.setText(role);
        viewHolder.roleBtn.setTextColor(color);

        initListeners(viewHolder, user);

        displayAvatarImage(user.getAvatar(), viewHolder.avatarImageView);

        return convertView;
    }

    private void initListeners(final ViewHolder viewHolder, final QMUser user) {
        viewHolder.roleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe(user)) {
                    DIAlert.showSimpleAlert(context, R.string.warning_title, R.string.alert_role_client_change);
                } else {
                    userOperationListener.onMessageItemClick(v, 0, user);
                }
            }
        });
    }

    private void setStatus(ViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());

        if (isMe(user)) {
            online = true;
        }

        if (online) {
            viewHolder.onlineStatusTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null){
                user = userFromDb;
            }

            long milisecond = user.getLastRequestAt() != null ? user.getLastRequestAt().getTime() : 0;
            viewHolder.onlineStatusTextView.setText(context.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(milisecond),
                    DateUtils.formatDateSimpleTime(milisecond)));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.dark_gray));
        }
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }

    private boolean isFriendValid(QMUser user) {
        return user.getFullName() != null;
    }

    private boolean isFriend(QMUser user) {
        if (isMe(user)) {
            return true;
        } else {
            boolean outgoingUserRequest = DataManager.getInstance().getUserRequestDataManager().existsByUserId(user.getId());
            boolean friend = DataManager.getInstance().getFriendDataManager().getByUserId(user.getId()) != null;
            return friend || outgoingUserRequest;
        }
    }

    private boolean isMe(QMUser inputUser) {
        QBUser currentUser = AppSession.getSession().getUser();
        return currentUser.getId().intValue() == inputUser.getId().intValue();
    }

    private static class ViewHolder {

        RoundedImageView avatarImageView;
        TextView nameTextView;
        Button roleBtn;
        TextView onlineStatusTextView;
    }
}
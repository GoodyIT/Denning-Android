package it.denning.ui.activities.others;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import it.denning.R;
import it.denning.ui.activities.base.BaseLoggableActivity;
import it.denning.ui.adapters.friends.FriendsAdapter;

public abstract class BaseFriendsListActivity extends BaseLoggableActivity {

    @BindView(R.id.friends_recyclerview)
    protected RecyclerView friendsRecyclerView;

    protected FriendsAdapter friendsAdapter;
    protected DataManager dataManager;

    @Override
    protected void onStart() {
        super.onStart();
        initFields();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (checkNetworkAvailableWithError()) {
                    performDone();
                }
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        if (friendListHelper != null) {
            friendsAdapter.setFriendListHelper(friendListHelper);
        }
    }

    @Override
    public void onChangedUserStatus(int userId, boolean online) {
        super.onChangedUserStatus(userId, online);
        friendsAdapter.notifyDataSetChanged();
    }

    private void initFields() {
        dataManager = DataManager.getInstance();
    }

    protected void initRecyclerView() {
        friendsAdapter = getFriendsAdapter();
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setAdapter(friendsAdapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected List<QMUser> getFriendsList() {
//        List<Friend> fri  endsList = dataManager.getFriendDataManager().getAllSorted();
//        return UserFriendUtils.getUsersFromFriends(friendsList);
        List<QMUser> friendsList = QMUserService.getInstance().getUserCache().getAllSorted("full_name", true);
        List<QMUser> userList = new ArrayList<>(friendsList.size());
        for (QMUser friend : friendsList) {
            if (friend.getEmail() != null && !friend.getEmail().contains("denning.com.my")) {

                userList.add(friend);
            }
        }
        return userList;
    }

    protected abstract FriendsAdapter getFriendsAdapter();

    protected abstract void performDone();
}
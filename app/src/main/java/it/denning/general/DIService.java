package it.denning.general;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.utils.FinderUnknownUsers;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.denning.App;
import it.denning.model.ChatContactModel;
import it.denning.model.ChatFirmModel;
import it.denning.model.ChatUserModel;
import it.denning.network.CompositeCompletion;
import it.denning.network.ErrorHandler;
import it.denning.network.NetworkManager;
import it.denning.utils.AuthUtils;
import it.denning.utils.helpers.ServiceManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hothongmee on 10/11/2017.
 */

public class DIService {
    private static final int LIMIT_USERS = 50;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc date created_at";

    public static OkHttpClient client = null;

    private Context context;
    private List<QBChatDialog> dialogList;
    private static List<QMUser> myFriendsList;
    private DIMessageInterface messageInterface;

    public DIService(Context context, List<QBChatDialog> dialogList) {
        this.context = context;
        this.dialogList = dialogList;
        if (client == null) {
            client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).retryOnConnectionFailure(true).writeTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();
        }
    }

    public static void fetchAllContactsFromServer(final List<DialogWrapper> dialogsList, final DIMessageInterface messageInterface) {
//        this.messageInterface = messageInterface;
        QBPagedRequestBuilder qbPagedBuilder = new QBPagedRequestBuilder();
        GenericQueryRule genericQueryRule = new GenericQueryRule(ORDER_RULE, ORDER_VALUE);

        ArrayList<GenericQueryRule> rule = new ArrayList<>();
        rule.add(genericQueryRule);

        qbPagedBuilder.setPerPage(LIMIT_USERS);
        qbPagedBuilder.setRules(rule);

        QBUsers.getUsers(qbPagedBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(final ArrayList<QBUser> qbUsers, Bundle bundle) {
                myFriendsList = QMUser.convertList(qbUsers);
                fetchContacts(null, messageInterface);
            }

            @Override
            public void onError(QBResponseException e) {
                messageInterface.onError(e.getLocalizedMessage());
            }
        });
    }

    public static void fetchContacts(List<DialogWrapper> dialogsList, final DIMessageInterface messageInterface) {
        if (dialogsList != null) {
            Collection<Integer> userIDs = new ArrayList<>();
            for (DialogWrapper dialogWrapper :  dialogsList) {
                userIDs.addAll(dialogWrapper.getChatDialog().getOccupants());
            }
            myFriendsList = QMUserService.getInstance().getUserCache().getUsersByIDs(userIDs);
        }

        String url  = DIConstants.CHAT_GET_URL + DISharedPreferences.getInstance().getEmail();
        NetworkManager.getInstance().sendPublicGetRequest(url, new CompositeCompletion() {
            @Override
            public void parseResponse(JsonElement jsonElement) {
                manageResponse(jsonElement, messageInterface);
            }
        }, new ErrorHandler() {
            @Override
            public void handleError(String error) {
                ErrorUtils.showError(App.getInstance(), error);
            }
        });
    }


    private static void manageResponse(JsonElement jsonElement, final DIMessageInterface messageInterface) {
        final ChatContactModel chatContactModel = new Gson().fromJson(jsonElement, ChatContactModel.class);
                ChatContactModel newContact = new ChatContactModel();

        newContact.staffContacts = findChatFirm(chatContactModel.staffContacts);
        newContact.favoriteClientContacts = findChatFirm(chatContactModel.favoriteClientContacts);
        newContact.favoriteStaffContacts = findChatFirm(chatContactModel.favoriteStaffContacts);
        newContact.clientContacts = findChatFirm(chatContactModel.clientContacts);
        messageInterface.onSuccess(newContact);
    }

    private static List<ChatFirmModel> findChatFirm(List<ChatFirmModel> modelList) {
        List<ChatFirmModel> newModelList = new ArrayList<>();
        for (ChatFirmModel chatFirmModel : modelList) {
            ChatFirmModel newChatFirm = new ChatFirmModel();
            newChatFirm.firmName = chatFirmModel.firmName;
            newChatFirm.firmCode = chatFirmModel.firmCode;
            for (final ChatUserModel userModel : chatFirmModel.users) {
                QMUser _user = findUser(userModel, myFriendsList);
                if (_user != null) {
                    StringifyArrayList list = new StringifyArrayList<String>();
                    list.add(userModel.tag);
                    _user.setTags(list);
                    _user.setTwitterDigitsId(userModel.position);
                    QMUserService.getInstance().getUserCache().update(_user);
                    newChatFirm.chatUsers.add(_user);
                }
            }

            newModelList.add(newChatFirm);
        }
        return newModelList;
    }

    private static QMUser findUser(ChatUserModel userModel, List<QMUser> friendsList) {
        QMUser selectedUser = null;
        for (QMUser user : friendsList) {
            if (DIHelper.toSafeStr(user.getEmail()).equals(userModel.email)) {
                selectedUser = user;
                break;
            }
        }

        return selectedUser;
    }
}

package it.denning.navigation.message.utils;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.q_municate_user_service.model.QMUser;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.denning.R;
import it.denning.model.ChatContactModel;
import it.denning.model.ChatFirmModel;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class MessageBaseAdapter extends SectioningAdapter {
    protected List<ChatFirmModel> contactList;
    protected List<ChatFirmModel> clientFavoriteList =  new ArrayList<>(), staffFavoriteList = new ArrayList<>(), favoriteList = new ArrayList<>();
    protected List<ChatFirmModel> copyOfObjectsList;
    ChatContactModel chatContactModel;
    protected final OnMessageItemClickListener clickListener;
    protected final OnMessageFavClickListener favClickListener;
    protected String query = "";

    public MessageBaseAdapter(List<ChatFirmModel> contactList,  OnMessageItemClickListener itemClickListener, OnMessageFavClickListener favClickListener) {
        this.contactList = contactList;
        this.copyOfObjectsList = new ArrayList<>();
        this.clickListener = itemClickListener;
        this.favClickListener = favClickListener;
    }

    public void setFavoriteList(List<ChatFirmModel> clientFavoriteList, List<ChatFirmModel> staffFavoriteList) {
        this.clientFavoriteList = clientFavoriteList;
        this.staffFavoriteList = staffFavoriteList;
        favoriteList.addAll(clientFavoriteList);
        favoriteList.addAll(staffFavoriteList);
    }

    public class ItemViewHolder extends SectioningAdapter.ItemViewHolder{
        @BindView(R.id.avatar_imageview)
        ImageView avatar;
        @BindView(R.id.name_textview)
        TextView name;
        @BindView(R.id.last_message_textview)
        TextView lastMessageAt;
        @BindView(R.id.button_favorite)
        ImageView btnFavorite;
        @BindView(R.id.position_tag)
        TextView contactTag;
        @BindView(R.id.chat_cardview)
        CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
        @BindView(R.id.name_textview)
        TextView name;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getNumberOfSections() {
        return contactList.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return contactList.get(sectionIndex).chatUsers.size();
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_chat, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cardview_message_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
        ChatFirmModel chatFirmModel = contactList.get(sectionIndex);
        headerViewHolder.name.setText(chatFirmModel.firmName);
    }

    public void setChatContactModel(ChatContactModel chatContactModel) {
        this.chatContactModel = chatContactModel;

        this.contactList.addAll(chatContactModel.clientContacts);
        this.contactList.addAll(chatContactModel.staffContacts);
        this.copyOfObjectsList = new ArrayList<>(contactList);

        setFavoriteList(chatContactModel.favoriteClientContacts, chatContactModel.favoriteStaffContacts);
    }

    public void setNewData(List<ChatFirmModel> newContactList) {

        this.contactList.clear();
        this.contactList.addAll(newContactList);

        notifyAllSectionsDataSetChanged();
    }

    public void addNewData(List<ChatFirmModel> newContactList) {
        this.contactList.addAll(newContactList);
        this.copyOfObjectsList.addAll(newContactList);
        notifyAllSectionsDataSetChanged();
    }

    public void changeFavoriteData(QMUser user) {
        for (ChatFirmModel chatFirmModel : this.favoriteList) {
            if (chatFirmModel.chatUsers.contains(user)) {
                chatFirmModel.chatUsers.remove(user);
                break;
            } else {
                chatFirmModel.chatUsers.add(user);
                break;
            }
        }
    }

    public void clear() {
        this.contactList.clear();
        notifyAllSectionsDataSetChanged();
    }

    public int getCount() {
        return contactList.size();
    }


    public void filterItem(String query) {
        this.query = query;
        if (query.trim().length() == 0) {
            setNewData(copyOfObjectsList);
            return;
        }
        query.toLowerCase();
        ArrayList<ChatFirmModel> newList = new ArrayList<>();

        for (ChatFirmModel chatFirmModel : copyOfObjectsList) {
            List<QMUser> userArrayList = chatFirmModel.chatUsers;
            ChatFirmModel newChatFirmModel = new ChatFirmModel();
            List<QMUser> newUserList = new ArrayList<>();
            boolean isExisting = false;
            for (QMUser user : userArrayList) {
                if (user.getFullName().toLowerCase().contains(query)) {
                    newUserList.add(user);
                    isExisting = true;
                }
            }

            if (isExisting) {
                newChatFirmModel.firmCode = chatFirmModel.firmCode;
                newChatFirmModel.firmName = chatFirmModel.firmName;
                newChatFirmModel.chatUsers = newUserList;
                newList.add(newChatFirmModel);
            }
        }

        setNewData(newList);
    }

    public void filterTag(int selectedIndex) {

    }

    public boolean isEmpty() {
        int count = getCount();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
}

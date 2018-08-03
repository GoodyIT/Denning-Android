package it.denning.navigation.message.utils;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

import it.denning.model.ChatContactModel;
import it.denning.model.ChatFirmModel;
import it.denning.search.utils.OnItemClickListener;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class MessageStaffAdapter extends MessageClientAdapter {

    public MessageStaffAdapter(List<ChatFirmModel> clientContactList, OnMessageItemClickListener itemClickListener, OnMessageFavClickListener favClickListener) {
        super(clientContactList, itemClickListener, favClickListener);
    }

    @Override
    public void filterTag(int selectedIndex) {
        ArrayList<ChatFirmModel> newList = new ArrayList<>();
        switch (selectedIndex) {
            case 0:
                newList.addAll(chatContactModel.getStaffContacts());
                newList.addAll(chatContactModel.getClientContacts());
                break;
            case 1:
                newList.addAll(chatContactModel.getStaffContacts());
                break;
            case 2:
                newList.addAll(chatContactModel.getClientContacts());
        }

        copyOfObjectsList = new ArrayList<>(newList);
        filterItem(query);
    }
}

package it.denning.navigation.message.utils;

import android.annotation.SuppressLint;
import android.view.View;

import com.quickblox.q_municate_user_service.model.QMUser;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.List;

import it.denning.R;
import it.denning.model.ChatFirmModel;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class MessageFavoriteAdapter extends MessageBaseAdapter {

    public MessageFavoriteAdapter(List<ChatFirmModel> clientContactList, OnMessageItemClickListener itemClickListener, OnMessageFavClickListener favClickListener) {
        super(clientContactList, itemClickListener, favClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
        final QMUser user = contactList.get(sectionIndex).chatUsers.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.name.setText(user.getFullName());
        itemViewHolder.lastMessageAt.setText(user.getLastRequestAt().toString());
        itemViewHolder.avatar.setImageResource(R.drawable.placeholder_user);
        itemViewHolder.btnFavorite.setVisibility(View.INVISIBLE);
        itemViewHolder.contactTag.setText(user.getTwitterDigitsId());
        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onMessageItemClick(view, itemIndex, user);
            }
        });
    }

    @Override
    public void filterTag(int selectedIndex) {
        ArrayList<ChatFirmModel> newList = new ArrayList<>();
        switch (selectedIndex) {
            case 0:
                newList.addAll(chatContactModel.getFavoriteClientContacts());
                newList.addAll(chatContactModel.getFavoriteStaffContacts());
                break;
            case 1:
                newList.addAll(chatContactModel.getFavoriteStaffContacts());
                break;
            case 2:
                newList.addAll(chatContactModel.getFavoriteClientContacts());
        }

        copyOfObjectsList = new ArrayList<>(newList);
        filterItem(query);
    }
}

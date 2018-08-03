package it.denning.navigation.message.utils;

import android.annotation.SuppressLint;
import android.view.View;

import com.quickblox.q_municate_user_service.model.QMUser;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.List;

import it.denning.R;
import it.denning.model.ChatFirmModel;

/**
 * Created by hothongmee on 11/11/2017.
 */

public class MessageClientAdapter extends MessageBaseAdapter {

    public MessageClientAdapter(List<ChatFirmModel> clientContactList, OnMessageItemClickListener itemClickListener, OnMessageFavClickListener favClickListener) {
        super(clientContactList,itemClickListener, favClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, final int sectionIndex, final int itemIndex, int itemType) {
        final QMUser user = contactList.get(sectionIndex).chatUsers.get(itemIndex);
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        itemViewHolder.name.setText(user.getFullName());
        itemViewHolder.lastMessageAt.setText(user.getLastRequestAt().toString());
        itemViewHolder.avatar.setImageResource(R.drawable.placeholder_user);
        boolean isFavorite = false;
        for (ChatFirmModel chatFirmModel : favoriteList) {
            if (chatFirmModel.chatUsers.contains(user)) {
                isFavorite = true;
                break;
            }
        }

        itemViewHolder.btnFavorite.setImageResource(R.drawable.ic_favorite);
        if (isFavorite) {
            itemViewHolder.btnFavorite.setImageResource(R.drawable.ic_favorite_selected);
        }

        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onMessageItemClick(view, itemIndex, user);
            }
        });

        final boolean isAdd = !isFavorite;
        itemViewHolder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                favClickListener.onFavClick(view, sectionIndex, itemIndex, user, isAdd);
            }
        });
    }
}

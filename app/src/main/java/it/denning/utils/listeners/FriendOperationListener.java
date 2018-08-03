package it.denning.utils.listeners;

public interface FriendOperationListener {

    void onAcceptUserClicked(int position, int userId);

    void onRejectUserClicked(int position, int userId);
}
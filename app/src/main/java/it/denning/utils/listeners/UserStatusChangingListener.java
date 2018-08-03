package it.denning.utils.listeners;

public interface UserStatusChangingListener {

    void onChangedUserStatus(int userId, boolean online);
}
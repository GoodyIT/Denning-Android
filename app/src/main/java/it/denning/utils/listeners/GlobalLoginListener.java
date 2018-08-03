package it.denning.utils.listeners;

public interface GlobalLoginListener {

    void onCompleteQbLogin();

    void onCompleteQbChatLogin();

    void onCompleteWithError(String error);
}
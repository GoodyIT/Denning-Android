package it.denning.utils.bridges;

public interface ConnectionBridge {

    boolean checkNetworkAvailableWithError();

    boolean isNetworkAvailable();
}